package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dandi.faq.R
import com.dandi.faq.model.Admin
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login_admin.*

class LoginAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)
        btLoginAdmin.setOnClickListener {
            if (etUsername.text.toString().isEmpty()) {
                etUsername.setError("Tidak Boleh Kosong")

            } else if (etPassword.text.toString().isEmpty()) {
                etPassword.setError("Tidak Boleh Kosong")
            } else {
                FirebaseDatabase.getInstance().reference.child("Admin/${etUsername.text.toString()}")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Login Gagal", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                    val admin = snapshot.getValue(Admin::class.java)
                                Log.d("ADMIN PASSWORD",admin!!.password.toString())
                                    if (etPassword.text.toString().equals(admin.password)) {
                                        val intent = Intent(
                                            Intent(
                                                this@LoginAdminActivity,
                                                VerifikasiNoTelponActivity::class.java
                                            )
                                        )
                                        SharedPrefUtil.saveBoolean("admin",true)
                                        intent.putExtra("noTelp", admin!!.noTelp)
                                        startActivity(intent)
                                        Toast.makeText(
                                            applicationContext,
                                            "Berhasil Login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Password Salah",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                        }

                    })
            }
        }
    }
}