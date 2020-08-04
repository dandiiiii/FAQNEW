package com.dandi.faq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_grapic.*

class GrapicActivity : AppCompatActivity() {
    val month = listOf("Seminar","Pembayaran","Lainnya")
    val earning = listOf(500,800,2000)
    lateinit var db:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grapic)
        setupPieChart()

    }

    fun setupPieChart() {

        val pie = AnyChart.pie()
        val dataEntries:ArrayList<DataEntry> = ArrayList()
        for (i in month.indices){
            dataEntries.add( ValueDataEntry(month.get(i),earning.get(i)))
        }
        pie.data(dataEntries)
        pie.title("Chart FAQ")
        any_chart_view.setChart(pie)
    }
}