package com.zacharycava.devicemediainspector.views


import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.zacharycava.devicemediainspector.sources.Route
import com.zacharycava.devicemediainspector.sources.getRoutes

/**
 * Information fragment that displays media system route information
 */
class RoutesFragment : InformationFragment(true) {

    override fun buildCards(cardPane: LinearLayout) {
        getRoutes(activity).forEach { route ->
            cardPane.addView(buildCard(route))
        }
    }

    private fun buildCard(route: Route): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("ID", route.id))
        info.add(Pair("Description", route.description))
        info.add(Pair("Presentation Display", "${route.display}"))
        info.add(Pair("Default Route", "${route.isDefault}"))
        info.add(Pair("Is Enabled", "${route.isEnabled}"))
        info.add(Pair("Is Selected", "${route.isSelected}"))
        info.add(Pair("Is Bluetooth", "${route.isBluetooth}"))
        info.add(Pair("Playback Location", route.playbackType.name))
        info.add(Pair("Device Type", route.deviceType.name))
        info.add(Pair("Connection State", route.connectionState.name))

        return createCardView(route.name, info)
    }

}
