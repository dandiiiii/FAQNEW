package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dandi.faq.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btLogin.setOnClickListener {
            val intent = Intent(this, VerifikasiNoTelponActivity::class.java)
            intent.putExtra("noTelp", etNotelp.text.toString())
            startActivity(intent)
        }
    }
}