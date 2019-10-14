package com.zacharycava.devicemediainspector.views

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.zacharycava.devicemediainspector.R

/**
 * Base InformationFragment class that handles the generic work of layout
 * inflation and card build request timings
 */
abstract class InformationFragment(private val refreshOnVisible: Boolean = false) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.detail_cards, container, false)
        val cardPane = rootView.findViewById<LinearLayout>(R.id.cardPane)
        buildCards(cardPane)
        return rootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        val mainView: View? = view
        if(isVisibleToUser && mainView != null && refreshOnVisible) {
            val cardPane = mainView.findViewById<LinearLayout>(R.id.cardPane)
            cardPane.removeAllViews()

            buildCards(cardPane)
        }
    }

    protected abstract fun buildCards(cardPane: LinearLayout)

    protected fun createTableView(rows: List<Pair<String, String>>): TableLayout {
        val table = TableLayout(context)
        table.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
        rows.filter { (_, value) -> value.isNotEmpty() }
            .forEach { (name, value) ->
                val row = TableRow(context)
                row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

                val rowName = TextView(context)
                rowName.layoutParams = TableRow.LayoutParams(1)
                rowName.setPadding(20, 10, 20, 10)
                rowName.text = name
                row.addView(rowName)

                val rowValue = TextView(context)
                rowValue.layoutParams = TableRow.LayoutParams(2)
                rowValue.setPadding(20, 10, 20, 10)
                rowValue.text = value
                row.addView(rowValue)
                table.addView(row)
            }

        return table
    }

    protected fun createCardView(name: String, rows: List<Pair<String, String>>): CardView? {
        val table = createTableView(rows)

        val title = TextView(context)
        title.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        title.setPadding(0, 0, 0, 20)
        title.setTypeface(title.typeface, Typeface.BOLD)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        title.text = name

        val cardLayout = LinearLayout(context)
        cardLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        cardLayout.orientation = LinearLayout.VERTICAL
        cardLayout.setPadding(10, 10, 10, 10)
        cardLayout.addView(title)
        cardLayout.addView(table)

        val currentContext = context
        val card = if(currentContext != null) CardView(currentContext) else null

        if(card != null) {
            card.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            (card.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 0, 0, 20)
            card.setContentPadding(20, 10, 20, 10)
            card.radius = 10f
            card.addView(cardLayout)
            card.cardElevation = 5f
        }

        return card
    }
}