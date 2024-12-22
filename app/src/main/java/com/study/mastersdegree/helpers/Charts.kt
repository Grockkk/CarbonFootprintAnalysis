package com.study.mastersdegree.helpers

import android.content.Context
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Charts {

    fun createBarChart(context: Context, barChart: BarChart, data: Map<String, Double>, title: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = BarDataSet(entries, title).apply {
            color = context.getColor(android.R.color.holo_blue_dark)
            valueTextColor = context.getColor(android.R.color.black)
        }
        val barData = BarData(dataSet)
        barChart.run {
            this.data = barData
            description.text = title
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(data.keys.toList())
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    fun createLineChart(context: Context, lineChart: LineChart, data: Map<String, Double>, title: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = LineDataSet(entries, title).apply {
            color = context.getColor(android.R.color.holo_green_dark)
            valueTextColor = context.getColor(android.R.color.black)
            lineWidth = 2f
            setCircleColor(context.getColor(android.R.color.holo_green_light))
            circleRadius = 5f
        }
        val lineData = LineData(dataSet)
        lineChart.run {
            this.data = lineData // Ustaw dane wykresu
            description.text = title
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(data.keys.toList())
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }
}
