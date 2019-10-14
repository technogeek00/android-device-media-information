package com.zacharycava.devicemediainspector.views


import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.zacharycava.devicemediainspector.sources.BuildInfo
import com.zacharycava.devicemediainspector.sources.Device
import java.util.ArrayList

/**
 * Information fragment that displays general device information
 */
class DeviceInfoFragment : InformationFragment() {

    override fun buildCards(cardPane: LinearLayout) {
        cardPane.addView(buildGeneralCard(Device()))
        cardPane.addView(buildBuildCard(BuildInfo()))
    }

    private fun buildGeneralCard(device: Device): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("Manufacturer", device.manufacturer))
        info.add(Pair("Model", device.model))
        info.add(Pair("Product", device.product))
        info.add(Pair("Consumer Brand", device.consumerBrand))
        info.add(Pair("Board", device.board))
        info.add(Pair("Device", device.device))
        info.add(Pair("Bootloader Version", device.bootloader))
        info.add(Pair("Host", device.host))

        return createCardView("General", info)
    }

    private fun buildBuildCard(build: BuildInfo): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("ID", build.id))
        info.add(Pair("Time", build.time))
        info.add(Pair("Type", build.type))
        info.add(Pair("User", build.user))
        info.add(Pair("Details", build.details))
        info.add(Pair("Tags", build.tags))
        info.add(Pair("Fingerprint", build.fingerprint))

        return createCardView( "OS Build", info)
    }
}
