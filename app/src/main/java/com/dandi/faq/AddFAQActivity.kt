package com.dandi.faq

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.format.Time
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.faq.Postingan
import com.example.faq.sharepreference.SharedPrefUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_f_a_q.*
import kotlinx.android.synthetic.main.item_ambil_foto.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception

class AddFAQActivity : AppCompatActivity() {
    private var bitmapFinal: Bitmap? = null
    private val REQUEST_WRITE_STORAGE_REQUEST_CODE: Int = 3
    private val REQUEST_KAMERA: Int = 2
    private var mHighQualityImageUri: Uri? = null
    private val REQUEST_GALERI: Int = 1
    var listJenisPertanyaan: List<String> = listOf("Seminar", "Pembayaran", "Lainnya")
    var jenisPertanyaan: String = ""
    lateinit var alertDialog: AlertDialog
    lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_f_a_q)
        requestAppPermissions()
        val spAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listJenisPertanyaan)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPertanyaan.adapter = spAdapter
        spPertanyaan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                jenisPertanyaan = listJenisPertanyaan.get(p2)
            }

        }
        btSubmit.setOnClickListener {
            if (!etPertanyaan.text!!.isEmpty()) {
                db = FirebaseDatabase.getInstance().reference.child(
                    "Postingan/${System.currentTimeMillis()}"
                )
               if (bitmapFinal!=null){
                   uploadFoto(bitmapFinal,db)
               }
                else{
                   val postingan = Postingan(
                       SharedPrefUtil.getString("noTelp")!!,
                       etPertanyaan.text.toString(),
                       jenisPertanyaan,
                       "",
                       ""
                   )
                   db.setValue(postingan).addOnSuccessListener {
                       Toast.makeText(applicationContext, "Postingan Terkirim", Toast.LENGTH_SHORT)
                           .show()
                   }
                       .addOnFailureListener {
                           Toast.makeText(applicationContext, "Postingan Gagal", Toast.LENGTH_SHORT)
                               .show()
                       }
               }

            }
        }
        btCamera.setOnClickListener {
            intentCamera()
        }
    }

    fun intentCamera() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        var view: View
        view = layoutInflater.inflate(R.layout.item_ambil_foto, null)
        alertDialogBuilder.setView(view)
        view.layoutGaleri.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALERI)
        }
        view.layoutKamera.setOnClickListener {
            if (RequestCamera.checkPermission(applicationContext)) {
                intentKamera()
            } else {
                RequestCamera.requestPermission(this)
            }
        }
        alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun intentKamera() {
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
            // todo create directory
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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
        if (requestCode == REQUEST_GALERI && resultCode == Activity.RESULT_OK) {
            if (data!!.data != null) {
                val uri = data.data
                var inputStream: InputStream? = null
                try {
                    inputStream = this.contentResolver.openInputStream(uri!!)
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    val rotateBitmap: Bitmap =
                        ImageRotator.rotateImageIfRequired(bitmap, applicationContext, uri)
                    bitmapFinal = rotateBitmap
                    imguploadpreview.setImageBitmap(bitmapFinal)
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()

                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()

                }
                alertDialog.dismiss()
            }
        } else if (requestCode == REQUEST_KAMERA && resultCode == Activity.RESULT_OK) {
            alertDialog.dismiss()

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
                imguploadpreview.setImageBitmap(bitmapFinal)
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()

            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    internal fun uploadFoto(
        bitmap: Bitmap?,
        db: DatabaseReference
    ) {
        val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
        var storageReference: StorageReference = firebaseStorage.getReference()
        var imageRef: StorageReference =
            storageReference.child("postingan/images/" + System.currentTimeMillis() + ".jpg")
        val bao = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bao)
        val data = bao.toByteArray();
        val uploadTask: UploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                val postingan = Postingan(
                    SharedPrefUtil.getString("noTelp")!!,
                    etPertanyaan.text.toString(),
                    jenisPertanyaan,
                    "",
                    it.toString()
                )
                db.setValue(postingan).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Postingan Terkirim", Toast.LENGTH_SHORT)
                        .show()
                }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Postingan Gagal", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }

}
