package com.dandi.faq.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dandi.faq.R
import com.dandi.faq.model.Like
import com.dandi.faq.model.User
import com.example.faq.Postingan
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_list_pertanyaan.view.*

class PostinganAdapter(
    var listKey: ArrayList<String>,
    var listPostingan: List<Postingan>,
    var context: Context
) :
    RecyclerView.Adapter<PostinganAdapter.ViewHolder>() {
    var listlike: ArrayList<Like> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoProfil: CircleImageView = itemView.imgFotoUser
        val nama = itemView.tvNamaUser
        val pertanyaan = itemView.textPertanyaan
        val fotopertanyaan = itemView.imgPostingPertanyaan
        val btlike = itemView.imgLike
        val btcomment = itemView.imgComment
        val jmllike = itemView.tvJmlLike
        val jenisPertanyaan = itemView.tvJenisPertanyaan
        val tanggal = itemView.tvTanggal
        val komentar = itemView.tvKomentar
        val layoutComment = itemView.layoutKomentar
        val etKomentar = itemView.etComment
        val btBalasComment = itemView.btComment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_pertanyaan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listPostingan.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listPostingan.get(position).fotopostingan.isEmpty() || listPostingan.get(position).fotopostingan == null) {
            holder.fotopertanyaan.visibility = View.GONE
        } else {
            Glide.with(context).load(listPostingan.get(position).fotopostingan)
                .into(holder.fotopertanyaan)
            holder.fotopertanyaan.visibility = View.VISIBLE
        }

        holder.pertanyaan.setText(listPostingan.get(position).pertanyaan)
        FirebaseDatabase.getInstance().reference.child("User/${listPostingan.get(position)!!.idUser}")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "${error}", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    holder.nama.setText(user!!.nama)
                    Glide.with(context.applicationContext).load(user!!.fotoProfil).into(holder.fotoProfil)
                }

            })
        holder.jmllike.setText("0")
        holder.jenisPertanyaan.setText(listPostingan.get(position).jenisPertanyaan)
        holder.btlike.setOnClickListener {
            val map = HashMap<String, Any>()
            map.put("isLike", true)
            FirebaseDatabase.getInstance().reference.child(
                "Postingan/${listKey.get(position)}/like/${SharedPrefUtil.getString(
                    "noTelp"
                )}"
            ).updateChildren(map)
        }
        if (listPostingan.get(position).commentAdmin.isEmpty()){
            holder.komentar.setText("Belum dibalas Admin")
        }
        else{
            holder.komentar.setText("Dibalas oleh ${listPostingan.get(position).commentAdmin}")
        }
        holder.tanggal.setText(listPostingan.get(position).tanggal)
        FirebaseDatabase.getInstance().reference.child("Postingan/${listKey.get(position)}/like")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        holder.jmllike.setText("0")
                        Toast.makeText(context, "${error}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        listlike.clear()
                        for (i in snapshot.children) {
                            val like = i.getValue(Like::class.java)
                            if (like != null) {
                                listlike.add(like!!)
                                holder.jmllike.setText(listlike.size.toString())
                            } else {
                                holder.jmllike.setText("0")
                            }

                        }
                    }

                }
            )

        holder.btcomment.setOnClickListener {
            if (SharedPrefUtil.getBoolean("admin")) {
                holder.layoutComment.visibility = View.VISIBLE
                holder.btBalasComment.setOnClickListener {
                    if (!holder.etKomentar.text.isEmpty()) {
                        val map = HashMap<String, Any>()
                        map.put(
                            "commentAdmin",
                            "${SharedPrefUtil.getString("namaAdmin")} (Admin) : ${holder.etKomentar.text.toString()}"
                        )
                        FirebaseDatabase.getInstance().reference.child(
                            "Postingan/${listKey.get(
                                position
                            )}"
                        )
                            .updateChildren(map)
                        holder.layoutComment.visibility = View.GONE
                    }
                }
            }
        }
        //        hol
    }
}