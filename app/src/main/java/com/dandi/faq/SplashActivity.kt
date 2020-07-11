package com.dandi.faq

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dandi.faq.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(object : Runnable{
            override fun run() {
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
            }

        },3000)
    }
}