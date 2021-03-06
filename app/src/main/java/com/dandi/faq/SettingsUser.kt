package com.dandi.faq

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.format.Time
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dandi.faq.model.Admin
import com.dandi.faq.model.User
import com.example.faq.sharepreference.SharedPrefUtil
import com.firebase.client.Firebase
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_settings_user.*
import kotlinx.android.synthetic.main.dialog_media.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class SettingsUser : AppCompatActivity() {
    private val REQUEST_WRITE_STORAGE_REQUEST_CODE: Int = 3
    var bitmapFinal: Bitmap? = null
    var mHighQualityImageUri: Uri? = null
    val REQUEST_KAMERA = 1
    val REQUEST_GALLERY = 2
    lateinit var appCompatDialog: AppCompatDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_user)
//        initProfile(
//            if (SharedPrefUtil.getBoolean("admin")) {
//                intent.getStringExtra("userName")
//            } else {
//                intent.getStringExtra("noTelp")
//            }
//        )
        getProfile()
        requestAppPermissions()
        Toast.makeText(applicationContext, intent.getStringExtra("noTelp"), Toast.LENGTH_SHORT)
            .show()
        RequestCamera.requestPermission(this)
        btUploadFotoSetting.setOnClickListener {
            appCompatDialog = AppCompatDialog(this)
            appCompatDialog.setContentView(R.layout.dialog_media)
            appCompatDialog.layoutKamera.setOnClickListener {
                intentKamera()
            }
            appCompatDialog.layoutGaleri.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_GALLERY)
            }
            appCompatDialog.show()
        }
        btDoneSetting.setOnClickListener {
            if (bitmapFinal != null) {
                if (!etNamaSetting.text.toString().isEmpty()) {
                    if (SharedPrefUtil.getBoolean("admin")) {
                        if (etNoTelpAdmin.text.toString().isEmpty()) {
                            etNoTelpAdmin.setError("Tidak boleh kosong")
                        } else {
                            uploadFoto(
                                bitmapFinal!!,
                                etNamaSetting.text.toString(),
                                etNoTelpAdmin.text.toString()
                            )
                        }
                    } else {
                        uploadFoto(bitmapFinal!!, etNamaSetting.text.toString(), "")
                    }
                } else {
                    Toast.makeText(applicationContext, "Harap memilih foto", Toast.LENGTH_SHORT)
                        .show()
                }
//                val map = HashMap<String, Any>()
//                map.put("nama", etNamaSetting.text.toString())
//                if (SharedPrefUtil.getBoolean("admin")) {
//                    FirebaseDatabase.getInstance().reference.child("Admin/${intent.getStringExtra("userName")}")
//                        .updateChildren(map)
//                    startActivity(Intent(this, MainAdminActivity::class.java))
//                    finish()
//                    SharedPrefUtil.saveString("namaAdmin", etNamaSetting.text.toString())
//                } else {
//                    FirebaseDatabase.getInstance().reference.child("User/${intent.getStringExtra("noTelp")}")
//                        .updateChildren(map)
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                }
            }
        }

    }

//    private fun initProfile(s: String?) {
//        var db: DatabaseReference
//        if (SharedPrefUtil.getBoolean("admin")) {
//            db = FirebaseDatabase.getInstance().reference.child("Admin/$s")
//        } else {
//            db = FirebaseDatabase.getInstance().reference.child("User/$s")
//        }
//        db.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue(User::class.java)
//                    Glide.with(applicationContext).load(user!!.fotoProfil).into(fotoPPUser)
//                    etNamaSetting.setText(user.nama)
//                }
//            }
//
//        })
//    }

    internal fun intentKamera() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        mHighQualityImageUri = generateTimeStampPhotoFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mHighQualityImageUri)
        startActivityForResult(intent, REQUEST_KAMERA)
    }

    private fun generateTimeStampPhotoFileUri(): Uri? {
        var photoFileUri: Uri? = null
        val code = applicationContext.packageManager.checkPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            applicationContext.packageName
        )
        if (code == PackageManager.PERMISSION_GRANTED) {
            val outputDir = getPhotoDirectory()
            if (outputDir != null) {
                val t = Time()
                t.setToNow()
                val photoFile = File(outputDir, System.currentTimeMillis().toString() + ".jpg")
                photoFileUri = Uri.fromFile(photoFile)
                return photoFileUri

            }

        } else {
            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
        }
        return photoFileUri
    }

    private fun getPhotoDirectory(): File? {
        var outputDir: File? = null
        val externalStorageStagte = Environment.getExternalStorageState()
        if (externalStorageStagte == Environment.MEDIA_MOUNTED) {
            val photoDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            outputDir = File(photoDir, getString(R.string.app_name))
            if (!outputDir.exists())
                if (!outputDir.mkdirs()) {
                    Toast.makeText(
                        this,
                        "Gagal Membuat Direktori " + outputDir.absolutePath,
                        Toast.LENGTH_SHORT
                    ).show()
                    outputDir = null
                }
        }
        return outputDir
    }

    private fun requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_WRITE_STORAGE_REQUEST_CODE
        )
    }

    private fun hasReadPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasWritePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_KAMERA && resultCode == Activity.RESULT_OK) {
            var inputStream: InputStream? = null
            try {
                inputStream = this.contentResolver.openInputStream(mHighQualityImageUri!!)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                val rotateBitmap: Bitmap = ImageRotator.rotateImageIfRequired(
                    bitmap,
                    applicationContext,
                    mHighQualityImageUri!!
                )
                bitmapFinal = rotateBitmap
//                uploadFoto(bitmapFinal!!)
                appCompatDialog.dismiss()
                fotoPPUser.setImageBitmap(bitmapFinal)

            } catch (e: FileNotFoundException) {

            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            var inputStream: InputStream? = null
            val uri = data!!.data
            try {
                inputStream = this.contentResolver.openInputStream(uri!!)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                val rotateBitmap: Bitmap = ImageRotator.rotateImageIfRequired(
                    bitmap,
                    applicationContext,
                    uri!!
                )
                bitmapFinal = rotateBitmap
//                uploadFoto(bitmapFinal!!)
                appCompatDialog.dismiss()
                fotoPPUser.setImageBitmap(bitmapFinal)

            } catch (e: FileNotFoundException) {

            }
        }
    }

    private fun uploadFoto(bitmap: Bitmap, nama: String, noTelp: String) {
        val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
        var storageReference: StorageReference = firebaseStorage.getReference()
        var imageRef: StorageReference =

            if (SharedPrefUtil.getBoolean("admin")) {
                storageReference.child("Admin/fotoProfil" + System.currentTimeMillis() + ".jpg")
            } else {
                storageReference.child("User/fotoProfil" + System.currentTimeMillis() + ".jpg")
            }
        val bao = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao)
        val data = bao.toByteArray();
        val uploadTask: UploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                if (SharedPrefUtil.getBoolean("admin")) {
                    val map = HashMap<String, Any>()
                    map.put("nama", etNamaSetting.text.toString())
                    map.put("fotoProfil", it.toString())
                    map.put("noTelp", it.toString())
                    FirebaseDatabase.getInstance().reference.child("Admin/${intent.getStringExtra("userName")}")
                        .updateChildren(map).addOnSuccessListener {
                            startActivity(Intent(this, MainAdminActivity::class.java))
                            finish()
                            SharedPrefUtil.saveString("namaAdmin", nama)
                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    val map = HashMap<String, Any>()
                    map.put("nama", etNamaSetting.text.toString())
                    map.put("fotoProfil", it.toString())
                    FirebaseDatabase.getInstance().reference.child("User/${intent.getStringExtra("noTelp")}")
                        .updateChildren(map).addOnSuccessListener {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                }
            }
        }
    }
    fun getProfile()  {
        if (SharedPrefUtil.getBoolean("admin")) {
            etNoTelpAdmin.visibility = View.VISIBLE
            FirebaseDatabase.getInstance().reference.child("Admin/${SharedPrefUtil.getString("userName")}")
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "${error}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(Admin::class.java)
                            if (user != null) {
                                etNamaSetting.setText(user!!.nama)
                                Glide.with(applicationContext).asBitmap()
                                    .load(user!!.fotoProfil)
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onLoadCleared(placeholder: Drawable?) {

                                        }

                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            bitmapFinal = resource
                                            fotoPPUser.setImageBitmap(resource)
                                        }

                                    })
                                etNoTelpAdmin.setText(user.noTelp)
                            }
                        }

                    }
                )
        } else {
            etNoTelpAdmin.visibility = View.GONE
            FirebaseDatabase.getInstance().reference.child("User/${SharedPrefUtil.getString("noTelp")}")
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "${error}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                etNamaSetting.setText(user!!.nama)
                                Glide.with(applicationContext).asBitmap()
                                    .load(user!!.fotoProfil)
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onLoadCleared(placeholder: Drawable?) {

                                        }

                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            fotoPPUser.setImageBitmap(resource)
                                            bitmapFinal = resource
                                        }

                                    })

                            }
                        }

                    }
                )
        }
    }

}