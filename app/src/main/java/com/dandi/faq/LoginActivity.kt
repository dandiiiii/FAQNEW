package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dandi.faq.R
import com.example.faq.sharepreference.SharedPrefUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btLogin.setOnClickListener {
            SharedPrefUtil.saveBoolean("admin", false)
            val intent = Intent(this, VerifikasiNoTelponActivity::class.java)
            intent.putExtra("noTelp", etNotelp.text.toString())
            startActivity(intent)
            SharedPrefUtil.saveBoolean("login", true)
        }
        tvLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }
    }
}