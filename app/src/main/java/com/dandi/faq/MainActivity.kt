package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.scales.Linear
import com.dandi.faq.adapter.MainAdapter
import com.dandi.faq.model.Like
import com.example.faq.Postingan
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var db: DatabaseReference
    var listPostingan: ArrayList<Postingan> = ArrayList()
    var listKey: ArrayList<String> = ArrayList()
    var listLike: ArrayList<Like> = ArrayList()
    var listComment: ArrayList<String> = ArrayList()
    lateinit var mainAdapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAddPertanyaan.setOnClickListener {
            startActivity(Intent(this, AddFAQActivity::class.java))
        }
        initFirebase()
    }

    private fun initFirebase() {
        db = FirebaseDatabase.getInstance().reference.child("Postingan")
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "${error}", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                listPostingan.clear()
                listKey.clear()
                listLike.clear()
                listComment.clear()
                for (i in snapshot.children) {
                    val key: String = i.key!!
                    val like = i.child("like").getValue(Like::class.java)
                    if (like != null) {
                        listLike.add(like!!)
                    }
                    listKey.add(key)
                    val postingan = i.getValue(Postingan::class.java)
                    listPostingan.add(postingan!!)
                }
                Log.d("LIST KEY", listKey.get(1))
                mainAdapter =
                    MainAdapter(listKey, listPostingan, this@MainActivity)
                val linearLayoutManager = LinearLayoutManager(this@MainActivity)
                linearLayoutManager.reverseLayout = true
                rvHome.layoutManager = linearLayoutManager
                rvHome.setHasFixedSize(true)
                rvHome.adapter = mainAdapter
                mainAdapter.notifyDataSetChanged()
            }

        })
    }
}