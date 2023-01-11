package com.zacharycava.devicemediainspector.sources

import android.os.Build

class BuildInfo {
    val id: String = Build.ID
    val time: String = "${Build.TIME}"
    val type: String = Build.TYPE
    val user: String = Build.USER
    val details: String = Build.DISPLAY
    val tags: String = Build.TAGS
    val fingerprint: String = Build.FINGERPRINT
}

class VersionInfo {
    val base: String = Build.VERSION.BASE_OS
    val codename: String = Build.VERSION.CODENAME
    val release: String = Build.VERSION.RELEASE
    val patch: String = Build.VERSION.SECURITY_PATCH
}

class Device {
    val manufacturer: String = Build.MANUFACTURER
    val model: String = Build.MODEL
    val product: String = Build.PRODUCT
    val consumerBrand: String = Build.BRAND
    val board: String = Build.BOARD
    val device: String = Build.DEVICE
    val hardware: String = Build.HARDWARE

    val bootloader: String = Build.BOOTLOADER
    val host: String = Build.HOST
}