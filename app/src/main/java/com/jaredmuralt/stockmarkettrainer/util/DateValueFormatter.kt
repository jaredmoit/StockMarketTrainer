package com.jaredmuralt.stockmarkettrainer.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*

class DateValueFormatter: ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        val date = Date()
        date.time = value.toLong() * 1000
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.YEAR).toString()
    }
}