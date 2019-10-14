package com.zacharycava.devicemediainspector.views


import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.zacharycava.devicemediainspector.sources.DRMSystem
import com.zacharycava.devicemediainspector.sources.getDRMSystems
import kotlin.collections.ArrayList

/**
 * Information fragment that displays DRM System information
 */
class DRMSystemsFragment : InformationFragment() {

    override fun buildCards(cardPane: LinearLayout) {
        getDRMSystems().forEach { system ->
            cardPane.addView(buildCard(system))
        }
    }

    private fun buildCard(system: DRMSystem): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("Vendor", system.vendor))
        info.add(Pair("Description", system.description))
        info.add(Pair("Version", system.version))
        info.add(Pair("Algorithms", system.algorithms))

        system.vendorProperties.forEach { (property, value) ->
            info.add(Pair(property.name, value))
        }

        info.add(Pair("Connected HDCP", system.connectedHdcpLevel.name))
        info.add(Pair("Max HDCP", system.maxHdcpLevel.name))
        info.add(Pair("Max Session Count", "${system.maxSessionCount}"))

        return createCardView(system.name, info)
    }
}
