package com.example.faq.sharepreference

import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    companion object {

        @get:Synchronized
        var context: Context? = null
            private set
    }
}
