package com.zacharycava.devicemediainspector.sources

import android.app.Activity
import android.view.Display as NativeDisplay
import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build

enum class HDRFormat() {
    DOLBY_VISION, HDR10, HDR10_PLUS, HLG, UNKNOWN
}

fun HDRTypeToHDRFormat(value: Int): HDRFormat {
    return when(value) {
        NativeDisplay.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> HDRFormat.DOLBY_VISION
        NativeDisplay.HdrCapabilities.HDR_TYPE_HDR10 -> HDRFormat.HDR10
        NativeDisplay.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> HDRFormat.HDR10_PLUS
        NativeDisplay.HdrCapabilities.HDR_TYPE_HLG -> HDRFormat.HLG
        else -> HDRFormat.UNKNOWN
    }
}

enum class PowerState() {
    OFF, ON, DOZE, DOZE_SUSPEND, ON_SUSPEND, UNKNOWN
}

fun DisplayStateToPowerState(value: Int): PowerState {
    return when(value) {
        NativeDisplay.STATE_OFF -> PowerState.OFF
        NativeDisplay.STATE_ON -> PowerState.ON
        NativeDisplay.STATE_DOZE -> PowerState.DOZE
        NativeDisplay.STATE_DOZE_SUSPEND -> PowerState.DOZE_SUSPEND
        NativeDisplay.STATE_ON_SUSPEND -> PowerState.ON_SUSPEND
        else -> PowerState.UNKNOWN
    }
}

class OutputDescription(val width: Int, val height: Int, val refreshRate: Float) {
    override fun toString(): String {
        return "${width}x${height}@${refreshRate}"
    }
}

class Display(source: NativeDisplay) {
    val name: String = source.name
    val id: Int = source.displayId
    val valid: Boolean = source.isValid
    val state: PowerState = DisplayStateToPowerState(source.state)

    // flags
    val isPresentation: Boolean
    val isPrivate: Boolean
    val isRound: Boolean
    val isSecure: Boolean
    val supportsProtectedBuffers: Boolean

    val renderOutput: OutputDescription
    val physicalOutput: OutputDescription?

    // HDR information
    val supportsHDR: Boolean
    val minimumLuminance: Float?
    val maximumLuminance: Float?
    val hdrFormats: List<HDRFormat>

    // Wide Gamut information
    val supportsWideColorGamut: Boolean
    val wideColorGamut: String?

    init {
        // compute the actual flags
        val flags = source.flags
        isPresentation = (flags and NativeDisplay.FLAG_PRESENTATION) == NativeDisplay.FLAG_PRESENTATION
        isPrivate = (flags and NativeDisplay.FLAG_PRIVATE) == NativeDisplay.FLAG_PRIVATE
        isRound = (flags and NativeDisplay.FLAG_ROUND) == NativeDisplay.FLAG_ROUND
        isSecure = (flags and NativeDisplay.FLAG_SECURE) == NativeDisplay.FLAG_SECURE
        supportsProtectedBuffers = (flags and NativeDisplay.FLAG_SUPPORTS_PROTECTED_BUFFERS) == NativeDisplay.FLAG_SUPPORTS_PROTECTED_BUFFERS

        // Get render sizes
        val renderPoint = Point()
        source.getSize(renderPoint)
        renderOutput = OutputDescription(renderPoint.x, renderPoint.y, source.refreshRate)

        // If available on device get display mode
        if(Build.VERSION.SDK_INT >= 23) {
            val mode = source.mode
            physicalOutput = OutputDescription(mode.physicalWidth, mode.physicalHeight, mode.refreshRate)
        } else {
            physicalOutput = null
        }

        // Check HDR Capabilities if available on device
        if(Build.VERSION.SDK_INT >= 24) {
            val capabilities = source.hdrCapabilities
            supportsHDR = if (Build.VERSION.SDK_INT >= 26) source.isHdr else capabilities != null

            minimumLuminance = capabilities?.desiredMinLuminance
            maximumLuminance = capabilities?.desiredMaxLuminance
            hdrFormats = capabilities?.supportedHdrTypes?.map { HDRTypeToHDRFormat(it) } ?: listOf()
        } else {
            supportsHDR = false
            minimumLuminance = null
            maximumLuminance = null
            hdrFormats = listOf()
        }

        // Check Wide Gamut Space Support
        if(Build.VERSION.SDK_INT >= 26) {
            supportsWideColorGamut = source.isWideColorGamut

            if(Build.VERSION.SDK_INT >= 29) {
                wideColorGamut = source.preferredWideGamutColorSpace?.name
            } else {
                wideColorGamut = null
            }
        } else {
            supportsWideColorGamut = false
            wideColorGamut = null
        }
    }
}

fun getDisplays(activity: Activity?): List<Display> {
    if(activity == null) return listOf()
    val manager = activity.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    return manager.displays.map { Display(it) }
}