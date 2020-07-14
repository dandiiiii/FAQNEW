package com.dandi.faq.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dandi.faq.R
import com.example.faq.Postingan
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_list_pertanyaan.view.*

class MainAdapter(var listPostingan: List<Postingan>, var context: Context) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoProfil: CircleImageView = itemView.imgFotoUser
        val nama = itemView.tvNamaUser
        val pertanyaan = itemView.textPertanyaan
        val fotopertanyaan = itemView.imgPostingPertanyaan
        val btlike = itemView.imgLike
        val btcomment = itemView.imgComment
        val jmllike = itemView.tvJmlLike
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
        Glide.with(context).load(listPostingan.get(position).fotopostingan)
            .into(holder.fotopertanyaan)
        holder.pertanyaan.setText(listPostingan.get(position).pertanyaan)
    }
}