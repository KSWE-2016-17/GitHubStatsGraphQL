package de.fhbielefeld.githubstatsgraphql.util

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
object ChartUtils {

    fun populateCommitChart(chart: BarChart, data: HashMap<String, Int>) {
        chart.data = data.toBarData()
        chart.setVisibleXRangeMaximum(5.toFloat())
        chart.animateY(800)
    }

    fun styleBarChart(chart: BarChart) {
        chart.description = null
        chart.legend.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.axisRight.isEnabled = false
    }

    fun HashMap<String, Int>.toBarData(): BarData {
        val list = flatMap { listOf(it).asIterable() }.sortedByDescending { it.value }
        var index = 0

        return BarData(BarDataSet(list.map {
            val result = it.toBarEntry(index)

            index++
            result
        }, null).apply {
            valueTextSize = 10f

            setColors(*ColorTemplate.MATERIAL_COLORS)
            setValueFormatter { value, entry, i, viewPortHandler ->
                list[entry.x.toInt()].key
            }
        })
    }

    fun Map.Entry<String, Int>.toBarEntry(position: Int): BarEntry {
        return BarEntry(position.toFloat(), value.toFloat())
    }

}