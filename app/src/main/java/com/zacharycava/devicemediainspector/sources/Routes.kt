package com.zacharycava.devicemediainspector.sources

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.mediarouter.media.MediaRouter
import android.media.MediaRouter as NativeMediaRouter

enum class RoutePlaybackType {
    LOCAL, REMOTE, NONE
}

enum class RouteDeviceType {
    TV, SPEAKER, BLUETOOTH, UNKNOWN
}

enum class RouteState {
    CONNECTED, CONNECTING, DISCONNECTED, UNKNOWN
}

class Route(source: MediaRouter.RouteInfo) {
    val id: String = source.id
    val name: String = source.name
    val description: String = source.description ?: "Not Given"
    val isDefault: Boolean = source.isDefault
    val isEnabled: Boolean = source.isEnabled
    val isSelected: Boolean = source.isSelected
    val isBluetooth: Boolean = source.isBluetooth
    val playbackType: RoutePlaybackType = when(source.playbackType) {
        MediaRouter.RouteInfo.PLAYBACK_TYPE_LOCAL -> RoutePlaybackType.LOCAL
        MediaRouter.RouteInfo.PLAYBACK_TYPE_REMOTE -> RoutePlaybackType.REMOTE
        else -> RoutePlaybackType.NONE
    }
    val deviceType: RouteDeviceType = when(source.deviceType) {
        MediaRouter.RouteInfo.DEVICE_TYPE_TV -> RouteDeviceType.TV
        MediaRouter.RouteInfo.DEVICE_TYPE_SPEAKER -> RouteDeviceType.SPEAKER
        MediaRouter.RouteInfo.DEVICE_TYPE_BLUETOOTH -> RouteDeviceType.BLUETOOTH
        else -> RouteDeviceType.UNKNOWN
    }
    val connectionState: RouteState = when(source.connectionState) {
        MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED -> RouteState.CONNECTED
        MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTING -> RouteState.CONNECTING
        MediaRouter.RouteInfo.CONNECTION_STATE_DISCONNECTED -> RouteState.DISCONNECTED
        else -> RouteState.UNKNOWN
    }
    val display: Int = source.presentationDisplayId
}

fun getRoutes(activity: Activity?): List<Route> {
    if(activity == null) return listOf()
    val router = MediaRouter.getInstance(activity)
    return router.routes.map { Route(it) }
}