package com.dandi.faq

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore

import java.io.IOException

object ImageRotator {

    @Throws(IOException::class)
    fun rotateImageIfRequired(img: Bitmap, context: Context, selectedImage: Uri): Bitmap {

        if (selectedImage.scheme == "content") {
            val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
            val c = context.contentResolver.query(selectedImage, projection, null, null, null)
            if (c!!.moveToFirst()) {
                val rotation = c.getInt(0)
                c.close()
                return rotateImage(img, rotation)
            }
            return img
        } else {
            val ei = ExifInterface(selectedImage.path!!)
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(img, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(img, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(img, 270)
                else -> return img
            }
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

}
