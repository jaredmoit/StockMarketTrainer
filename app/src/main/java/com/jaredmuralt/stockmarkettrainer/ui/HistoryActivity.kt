package com.jaredmuralt.stockmarkettrainer.ui

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jaredmuralt.stockmarkettrainer.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // get history
        val mPrefs = getSharedPreferences(getString(R.string.prefs_id), Context.MODE_PRIVATE)
        val history = JSONArray(mPrefs.getString(getString(R.string.preference_history), "[]"))

        // load chart
        val entries: ArrayList<Entry> = ArrayList()
        for (i in 0 until history.length()) {
            entries.add(
                Entry((i + 1).toFloat(), history[i].toString().toFloat())
            )
        }
        // history line
        val pastDataSet = LineDataSet(entries,
            getString(R.string.total_savings)
        )
        pastDataSet.color = getColor(R.color.pastBlue)
        pastDataSet.lineWidth = 3f
        pastDataSet.setDrawCircles(false)
        pastDataSet.setDrawValues(false)

        // setup chart
        val chartData = LineData()
        chartData.addDataSet(pastDataSet)
        chart.data = chartData

        chart.animateXY(500, 500)

        val desc = Description()
        desc.isEnabled = false

        val l = chart.legend
        l.formSize = 12f
        l.form = Legend.LegendForm.CIRCLE
        l.textSize = 14f
        l.textColor = Color.BLACK
        l.xEntrySpace = 14f

        chart.description = desc
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setNoDataTextColor(Color.TRANSPARENT)
        chart.setScaleEnabled(false)
        chart.setNoDataText("")
        chart.invalidate()
    }
}