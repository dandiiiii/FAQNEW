package com.dandi.faq.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dandi.faq.R
import com.dandi.faq.model.User
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    internal lateinit var view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false)
        initFirebase()
        return view
    }
   fun initFirebase(){
       FirebaseDatabase.getInstance().reference.child("Admin/${SharedPrefUtil.getString("userName")}").addValueEventListener(
           object : ValueEventListener {
               override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(context,"${error}", Toast.LENGTH_SHORT).show()
               }

               override fun onDataChange(snapshot: DataSnapshot) {
                   val user = snapshot.getValue(User::class.java)
                   view.tvNamaProfileAdmin.setText(user!!.nama)
                   view.tvNoTelpProfileAdmin.setText(user!!.noTelp)
                   Glide.with(context!!).load(user!!.fotoProfil).into(view.imgProfileAdmin)
               }

           }
       )
    }
}