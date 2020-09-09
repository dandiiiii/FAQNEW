package com.dandi.faq.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dandi.faq.R
import com.dandi.faq.adapter.PostinganAdapter
import com.dandi.faq.model.Like
import com.example.faq.Postingan
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_postingan.view.*

class PostinganFragment : Fragment() {
    var listPostingan: ArrayList<Postingan> = ArrayList()
    var listKey: ArrayList<String> = ArrayList()
    var listLike: ArrayList<Like> = ArrayList()
    lateinit var postinganAdapter: PostinganAdapter
    var listComment: ArrayList<String> = ArrayList()
    private lateinit var db: DatabaseReference
    internal lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_postingan, container, false)
        initFirebase()
        return view
    }

    private fun initFirebase() {
        db = FirebaseDatabase.getInstance().reference.child("Postingan")
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "${error}", Toast.LENGTH_SHORT).show()
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
                    PostinganAdapter(listKey,
                        listPostingan,
                        context!!)
                val linearLayoutManager = LinearLayoutManager(context!!)
                linearLayoutManager.reverseLayout = true
                linearLayoutManager.stackFromEnd = true
                view.rvPostinganAdmin.layoutManager = linearLayoutManager
                view.rvPostinganAdmin.setHasFixedSize(true)
                view.rvPostinganAdmin.adapter = postinganAdapter
                postinganAdapter.notifyDataSetChanged()
            }

        })
    }

}