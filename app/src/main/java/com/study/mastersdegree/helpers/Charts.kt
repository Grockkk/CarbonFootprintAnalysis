package com.study.mastersdegree.helpers

import android.content.Context
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Charts {

    fun createBarChart(context: Context, barChart: BarChart, data: Map<String, Double>, title: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = BarDataSet(entries, title).apply {
            color = context.getColor(android.R.color.holo_blue_light)
            valueTextColor = context.getColor(android.R.color.black)
        }
        val barData = BarData(dataSet)

        barChart.run {
            this.data = barData
            description.isEnabled = false // Ukrycie domyślnego opisu
            extraTopOffset = 20f // Miejsce na tytuł

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(data.keys.toList())
                setDrawGridLines(false)
                labelRotationAngle = 30f
                setCenterAxisLabels(false)
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(true)
            }

            animateY(1000)
            invalidate()
        }
    }

    fun createLineChart(context: Context, lineChart: LineChart, data: Map<String, Double>, title: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = LineDataSet(entries, title).apply {
            color = context.getColor(android.R.color.holo_blue_light)
            valueTextColor = context.getColor(android.R.color.black)
            lineWidth = 2f
            setDrawFilled(true)
            fillColor = context.getColor(android.R.color.holo_blue_light)
            setCircleColor(context.getColor(android.R.color.holo_blue_dark))
            circleRadius = 5f
        }
        val lineData = LineData(dataSet)

        lineChart.run {
            this.data = lineData
            description.isEnabled = false // Ukrycie domyślnego opisu
            extraTopOffset = 20f // Miejsce na tytuł

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(data.keys.toList())
                setDrawGridLines(false)
                labelRotationAngle = 30f
                setCenterAxisLabels(false)
            }
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(true)
            }

            animateX(1000)
            invalidate()
        }
    }
}
