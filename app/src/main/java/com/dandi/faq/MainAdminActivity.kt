package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SharedMemory
import androidx.fragment.app.Fragment
import com.dandi.faq.fragment.DiagramFragment
import com.dandi.faq.fragment.PostinganFragment
import com.dandi.faq.fragment.ProfileFragment
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_main.*

class MainAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        gotoFragment(PostinganFragment())
        btNavAdmin.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.menuDashboardAdmin) {
                gotoFragment(PostinganFragment())
                return@setOnNavigationItemSelectedListener true
            } else if (it.itemId == R.id.menuProfileAdmin) {
                gotoFragment(ProfileFragment())
                return@setOnNavigationItemSelectedListener true
            } else if (it.itemId == R.id.menuLogoutAdmin) {
                SharedPrefUtil.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                FirebaseAuth.getInstance().signOut()
                return@setOnNavigationItemSelectedListener true
            } else if (it.itemId == R.id.menuChartAdmin) {
                gotoFragment(DiagramFragment())
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun gotoFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameAdmin, fragment).commit()
    }

}