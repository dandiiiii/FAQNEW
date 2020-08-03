package com.dandi.faq

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dandi.faq.model.User
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        FirebaseDatabase.getInstance().reference.child("User/${SharedPrefUtil.getString("noTelp")}")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, "${error}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        tvNamaProfile.setText(user!!.nama)
                        tvNoTelpProfile.setText(user!!.noTelp)
                        Glide.with(applicationContext).load(user!!.fotoProfil).into(imgProfile)
                    }

                }
            )
    }
}