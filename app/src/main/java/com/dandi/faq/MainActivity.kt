package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dandi.faq.adapter.PostinganAdapter
import com.dandi.faq.model.Like
import com.example.faq.Postingan
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var db: DatabaseReference
    var listPostingan: ArrayList<Postingan> = ArrayList()
    var listKey: ArrayList<String> = ArrayList()
    var listLike: ArrayList<Like> = ArrayList()
    var listComment: ArrayList<String> = ArrayList()
    lateinit var postinganAdapter: PostinganAdapter
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
                postinganAdapter =
                    PostinganAdapter(listKey, listPostingan, this@MainActivity)
                val linearLayoutManager = LinearLayoutManager(this@MainActivity)
                linearLayoutManager.reverseLayout = true
                linearLayoutManager.stackFromEnd = true
                rvHome.layoutManager = linearLayoutManager
                rvHome.setHasFixedSize(true)
                rvHome.adapter = postinganAdapter
                postinganAdapter.notifyDataSetChanged()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLogout) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            SharedPrefUtil.edit().clear().apply()
        } else if (item.itemId == R.id.menuProfil) {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        return true
    }
}