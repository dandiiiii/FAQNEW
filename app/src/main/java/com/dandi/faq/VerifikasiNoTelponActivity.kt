package com.dandi.faq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dandi.faq.LoginActivity
import com.dandi.faq.MainActivity
import com.dandi.faq.R
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.activity_verifikasi_no_telpon.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

class VerifikasiNoTelponActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    var mIdVerifikasi: String? = ""
    var noTelp: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifikasi_no_telpon)
        if (intent.getStringExtra("noTelp")!!
                .isEmpty() || intent.getStringExtra("noTelp") == null
        ) {
            Toast.makeText(applicationContext, "Nomor Telepon Kosong", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            var temp_noTelp = "${intent.getStringExtra("noTelp")!!}"
            if (temp_noTelp.substring(0, 1).equals("0") || temp_noTelp.substring(0, 1)
                    .equals("O") || temp_noTelp.substring(0, 1).equals("o")
            ) {
                noTelp = "+62${temp_noTelp.substring(1, temp_noTelp.length)}"
            } else {
                noTelp = "+${intent.getStringExtra("noTelp")!!}"
            }
            Log.d("NOTELPON", noTelp!!)
            kirimVerifikasi(noTelp!!)
            kirimUlang()
        }

        btVerifikasi.setOnClickListener {
            if (!etVerifikasi.text.toString().isEmpty()) {
                verifikasiKode(etVerifikasi.text.toString())
            }
        }
        initFirebase()
    }

    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun kirimVerifikasi(noTelp: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            noTelp, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallback
        )
    }

    private val mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            var kode = p0.smsCode.toString()
            try {
                etVerifikasi.setText(kode)
                verifikasiKode(kode)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(applicationContext, p0.toString(), Toast.LENGTH_SHORT).show()

        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            mIdVerifikasi = p0
        }

    }

    private fun verifikasiKode(kode: String) {
        try {
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(mIdVerifikasi!!, kode)
            login(credential)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(
            this
        ) {
            if (it.isSuccessful) {
                getToken()
                pushFirebase(it.result!!.user!!.uid)
                SharedPrefUtil.saveString("userName","${intent.getStringExtra(
                    "userName"
                )}")
                SharedPrefUtil.saveBoolean("login", true)
                SharedPrefUtil.saveString("id", it.result!!.user!!.uid)
                SharedPrefUtil.saveString("noTelp", this.intent.getStringExtra("noTelp")!!)
                val intent = Intent(this, SettingsUser::class.java)
                intent.putExtra("noTelp", this.intent.getStringExtra("noTelp"))
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Kode Salah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pushFirebase(uid: String) {
        val userFirebase = UserFirebase()
        userFirebase.nama = "Dandi"
        userFirebase.noTelp = this.intent.getStringExtra("noTelp")!!
        userFirebase.uid = uid
        if (!SharedPrefUtil.getBoolean("admin")) {
            FirebaseDatabase.getInstance().reference.child("User/${this.intent.getStringExtra("noTelp")}")
                .setValue(userFirebase)
        } else {
            FirebaseDatabase.getInstance().reference.child(
                "Admin/${this.intent.getStringExtra(
                    "${intent.getStringExtra(
                        "userName"
                    )}"
                )}"
            )
                .setValue(userFirebase)
        }
    }

    fun pushToken() {
        val map = HashMap<String, Any>()
        map.put("token", SharedPrefUtil.getString("token")!!)
        FirebaseDatabase.getInstance().reference.child("Tokens/${this.intent.getStringExtra("noTelp")}")
            .updateChildren(map)
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(object :
            OnSuccessListener<InstanceIdResult> {
            override fun onSuccess(p0: InstanceIdResult?) {
                SharedPrefUtil.saveString("token", p0!!.token)
                pushToken()
            }
        })
    }

    fun kirimUlang() {
        val delay = 1000
        var durasi = 60
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                durasi = durasi - 1
                tvTimer.setText(durasi.toString())
                if (durasi == 0) {
                    tvKirimUlang.setTextColor(resources.getColor(R.color.colorPrimary))
                    tvKirimUlang.isEnabled = true
                    tvKirimUlang.setOnClickListener {
                        kirimVerifikasi(noTelp!!)
                        Toast.makeText(applicationContext, "Mengirim Ulang...", Toast.LENGTH_SHORT)
                            .show()
                        durasi = 60
                        handler.postDelayed(this, 1000)
                    }
                } else {
                    tvKirimUlang.setTextColor(resources.getColor(R.color.abu))
                    tvKirimUlang.isEnabled = false
                    handler.postDelayed(this, delay.toLong())
                }
            }

        }, delay.toLong())
    }
}