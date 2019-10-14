package com.zacharycava.devicemediainspector.views


import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.zacharycava.devicemediainspector.sources.Codec
import com.zacharycava.devicemediainspector.sources.getCodecs

/**
 * Information fragment that displays codec information
 */
class CodecsFragment : InformationFragment() {

    override fun buildCards(cardPane: LinearLayout) {
        getCodecs().forEach{ codec ->
            val card = buildCard(codec)
            cardPane.addView(card)
        }
    }

    private fun buildCard(codec: Codec): CardView? {
        val info = ArrayList<Pair<String, String>>()

        info.add(Pair("Coding Function", codec.codingFunction.name))
        info.add(Pair("Mime Types", codec.mimeTypes.joinToString(", ")))

        info.add(Pair("Canonical Name", codec.canonicalName ?: ""))
        info.add(Pair("Is Alias", "${codec.isAlias ?: ""}"))
        info.add(Pair("Vendor Provided", "${codec.isVendorProvided ?: ""}"))
        info.add(Pair("Software Only", "${codec.isSoftwareOnly ?: ""}"))
        info.add(Pair("Hardware Accelerated", "${codec.isHardwareAccelerated ?: ""}"))

        return createCardView(codec.name, info)
    }
}
