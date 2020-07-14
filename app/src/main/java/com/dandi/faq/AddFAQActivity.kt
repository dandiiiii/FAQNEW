package com.dandi.faq

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.faq.ListLike
import com.example.faq.Postingan
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_f_a_q.*

class AddFAQActivity : AppCompatActivity() {
    var listJenisPertanyaan: List<String> = listOf("Seminar", "Pembayaran", "Lainnya")
    var jenisPertanyaan: String = ""
    var imgUri: Uri? = null
    lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_f_a_q)
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listJenisPertanyaan)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPertanyaan.adapter = spAdapter
        spPertanyaan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                jenisPertanyaan = listJenisPertanyaan.get(p2)
            }

        }
        btSubmit.setOnClickListener {
            if (!etPertanyaan.text!!.isEmpty()) {
                db = FirebaseDatabase.getInstance().reference.child(
                    "Postingan/${SharedPrefUtil.getString("noTelp")}"
                )
                val postingan = Postingan(
                    SharedPrefUtil.getString("noTelp")!!,
                    etPertanyaan.text.toString(),
                    jenisPertanyaan,
                    "",
                    if (imgUri != null) {
                        imgUri.toString()
                    } else {
                        ""
                    }
                )
                db.setValue(postingan).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Postingan Terkirim", Toast.LENGTH_SHORT)
                        .show()
                }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Postingan Gagal", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }
}