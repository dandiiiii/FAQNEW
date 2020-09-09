package com.dandi.faq.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.dandi.faq.R
import com.example.faq.Postingan
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_diagram.*


class DiagramFragment : Fragment() {
    lateinit var db: DatabaseReference
    var listJenisPertanyaan: List<String> = listOf("Seminar", "Pembayaran", "Lainnya")
    var listTotalPertanyaan: List<Int> = ArrayList()
    var listSeminar: ArrayList<Postingan> = ArrayList()
    var listPembayaran: ArrayList<Postingan> = ArrayList()
    var listLainnya: ArrayList<Postingan> = ArrayList()
    internal lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diagram, container, false)
        initFirebase()
        return view
    }

   private fun initFirebase() {
        db = FirebaseDatabase.getInstance().reference.child("Postingan")
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context!!, "$error", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                listSeminar.clear()
                listPembayaran.clear()
                listLainnya.clear()
                for (i in snapshot.children) {
                    val postingan = i.getValue(Postingan::class.java)
                    if (postingan!!.jenisPertanyaan.equals("Seminar")) {
                        listSeminar.add(postingan)
                        Log.d("SEMINAR", "LOADED")
                    } else if (postingan!!.jenisPertanyaan.equals("Pembayaran")) {
                        listPembayaran.add(postingan)
                    } else if (postingan!!.jenisPertanyaan.equals("Lainnya")) {
                        listLainnya.add(postingan)
                    }

                }
                listTotalPertanyaan =
                    listOf(listSeminar.size, listPembayaran.size, listLainnya.size)
                Log.d("SIZE PERTANYAAN", listTotalPertanyaan.size.toString())

                initChart(listTotalPertanyaan)
            }

        })
    }

    private fun initChart(listTotalPertanyaan: List<Int>) {
        val pie = AnyChart.pie()
        val dataEntries: ArrayList<DataEntry> = ArrayList()
        for (i in listJenisPertanyaan.indices) {
            dataEntries.add(ValueDataEntry(listJenisPertanyaan.get(i), listTotalPertanyaan.get(i)))
        }
        pie.data(dataEntries)
        pie.title("Diagram FAQ")
        anyChartPostingan.setChart(pie)
    }
}