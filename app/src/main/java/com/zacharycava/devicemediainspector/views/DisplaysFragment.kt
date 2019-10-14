package com.zacharycava.devicemediainspector.views


import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.zacharycava.devicemediainspector.sources.Display
import com.zacharycava.devicemediainspector.sources.getDisplays
import java.util.ArrayList

/**
 * Information fragment that displays output Display information
 */
class DisplaysFragment : InformationFragment(true) {

    override fun buildCards(cardPane: LinearLayout) {
        getDisplays(activity).forEach { display ->
            val card = buildCard(display)
            cardPane.addView(card)
        }
    }

    private fun buildCard(display: Display): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("ID", "${display.id}"))
        info.add(Pair("Valid", "${display.valid}"))

        // check state
        info.add(Pair("Display State", display.state.name))
        info.add(Pair("Presentation Display", "${display.isPresentation}"))
        info.add(Pair("Private","${display.isPrivate}"))
        info.add(Pair("Round Shape","${display.isRound}"))
        info.add(Pair("Secure Output", "${display.isSecure}"))
        info.add(Pair("Supports Protected Buffers","${display.supportsProtectedBuffers}"))

        info.add(Pair("Render Output", display.renderOutput.toString()))
        info.add(Pair("Physical Output", display.physicalOutput?.toString() ?: ""))

        info.add(Pair("Supports HDR", "${display.supportsHDR}"))
        info.add(Pair("Luminance Range", if (display.supportsHDR) "${display.minimumLuminance}-${display.maximumLuminance}" else ""))
        info.add(Pair("HDR Formats", display.hdrFormats.joinToString(", ")))

        info.add(Pair("Supports Wide Color Gamut", "${display.supportsWideColorGamut}"))
        info.add(Pair("Wide Color Gamut", "${display.wideColorGamut}"))

        return createCardView(display.name, info)
    }

}
