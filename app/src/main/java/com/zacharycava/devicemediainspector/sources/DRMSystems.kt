package com.zacharycava.devicemediainspector.sources

import android.media.MediaDrm
import android.media.MediaDrmResetException
import android.os.Build
import android.util.Log
import java.util.*

enum class VendorProperty(val sdkVersion: Int, val propertyName: String) {
    // widevine
    SECURITY_LEVEL(18, "securityLevel"),
    SYSTEM_ID(18, "systemId"),
    PRIVACY_MODE(19, "privacyMode"),
    SESSION_SHARING(19, "sessionSharing"),
    USAGE_REPORT_SHARING(21, "usageReportingSupport"),
    HDCP_LEVEL(21, "hdcpLevel"),
    MAX_HDCP_LEVEL(21, "maxHdcpLevel"),
    MAX_NUMBER_OF_SESSIONS(23, "maxNumberOfSessions"),
    OPEN_SESSIONS(23, "numberOfOpenSessions"),
    CRYPTO_VERSION(24, "oemCryptoApiVersion"),
    SRM_VERSION(26, "CurrentSRMVersion"),
    SRM_UPDATABLE(26, "SRMUpdateSupport")
}

enum class SystemIDs(val uuid: UUID, val properties: List<VendorProperty> = listOf()) {
    ClearKey(UUID.fromString("e2719d58-a985-b3c9-781a-b030af78d30e")),
    PlayReady(UUID.fromString("9a04f079-9840-4286-ab92-e65be0885f95")),
    Widevine(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"), listOf(
        VendorProperty.SECURITY_LEVEL,
        VendorProperty.SYSTEM_ID,
        VendorProperty.PRIVACY_MODE,
        VendorProperty.SESSION_SHARING,
        VendorProperty.USAGE_REPORT_SHARING,
        VendorProperty.HDCP_LEVEL,
        VendorProperty.MAX_HDCP_LEVEL,
        VendorProperty.MAX_NUMBER_OF_SESSIONS,
        VendorProperty.OPEN_SESSIONS,
        VendorProperty.CRYPTO_VERSION,
        VendorProperty.SRM_VERSION,
        VendorProperty.SRM_UPDATABLE
    ))
}

enum class HDCPLevel {
    UNKNOWN, NONE, NO_DIGITAL_OUTPUT, V1, V2, V2_1, V2_2, V2_3
}

fun MediaDrmToHDCPLevel(value: Int?): HDCPLevel {
    return when(value) {
        MediaDrm.HDCP_LEVEL_UNKNOWN -> HDCPLevel.UNKNOWN
        MediaDrm.HDCP_NONE -> HDCPLevel.NONE
        MediaDrm.HDCP_NO_DIGITAL_OUTPUT -> HDCPLevel.NO_DIGITAL_OUTPUT
        MediaDrm.HDCP_V1 -> HDCPLevel.V1
        MediaDrm.HDCP_V2 -> HDCPLevel.V2
        MediaDrm.HDCP_V2_1 -> HDCPLevel.V2_1
        MediaDrm.HDCP_V2_2 -> HDCPLevel.V2_2
        MediaDrm.HDCP_V2_3 -> HDCPLevel.V2_3
        else -> HDCPLevel.UNKNOWN
    }
}

class DRMSystem(id: SystemIDs) {
    val name: String = id.name
    val vendor: String
    val description: String
    val version: String
    val algorithms: String
    val vendorProperties: List<Pair<VendorProperty, String>>

    val connectedHdcpLevel: HDCPLevel
    val maxHdcpLevel: HDCPLevel
    val maxSessionCount: Int

    init {
        val system = MediaDrm(id.uuid)
        vendor = safePropertyFetch(system, MediaDrm.PROPERTY_VENDOR)
        description = safePropertyFetch(system, MediaDrm.PROPERTY_DESCRIPTION)
        version = safePropertyFetch(system, MediaDrm.PROPERTY_VERSION)
        algorithms = safePropertyFetch(system, MediaDrm.PROPERTY_ALGORITHMS)

        vendorProperties = id.properties.map { property ->
            var result: Pair<VendorProperty, String> = Pair(property, "")
            try {
                if(Build.VERSION.SDK_INT >= property.sdkVersion) {
                    result = Pair(property, system.getPropertyString(property.propertyName))
                }
            } catch(err: MediaDrm.MediaDrmStateException) { }

            result
        }.filter { (_, value) -> value.isNotEmpty() }

        connectedHdcpLevel = MediaDrmToHDCPLevel(if (Build.VERSION.SDK_INT >= 28) system.connectedHdcpLevel else null)
        maxHdcpLevel = MediaDrmToHDCPLevel(if (Build.VERSION.SDK_INT >= 28) system.maxHdcpLevel else null)
        maxSessionCount = if (Build.VERSION.SDK_INT >= 28) system.maxSessionCount else -1
    }

    private fun safePropertyFetch(system: MediaDrm, property: String): String {
        try {
            return system.getPropertyString(property)
        } catch (err: MediaDrm.MediaDrmStateException) {
            Log.e("DRMSystems", "$property failure, debugging data: ${err.diagnosticInfo}")
        }

        return "Unknown"
    }
}

fun getDRMSystems(): List<DRMSystem> {
    return listOf(SystemIDs.ClearKey, SystemIDs.PlayReady, SystemIDs.Widevine)
        .filter { MediaDrm.isCryptoSchemeSupported(it.uuid) }
        .map {
            var result: DRMSystem? = null
            try {
                result = DRMSystem(it)
            } catch(err: Exception) {
                Log.e("DRMSystems", err.localizedMessage);
            }

            result
        }
        .filterNotNull()
}