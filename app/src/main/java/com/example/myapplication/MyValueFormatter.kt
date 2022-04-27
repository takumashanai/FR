package com.example.myapplication

import com.github.mikephil.charting.utils.ViewPortHandler

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat


class MyValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat = DecimalFormat("###,###,###")
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value).toString()
    }
}