package com.canli.oya.traininventoryroom.utils

import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object BitmapUtils {

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.externalCacheDir

        return File.createTempFile(
                imageFileName, //prefix
                ".jpg", //suffix
                storageDir      //directory
        )
    }

    fun deleteImageFile(context: Context, imagePath: String) {
        // Get the file
        val imageFile = File(imagePath)
        // Delete the image
        val deleted = imageFile.delete()
        // If there is an error deleting the file, show a Toast
        if (!deleted) {
            Toast.makeText(context, "Error during delete", Toast.LENGTH_SHORT).show()
        }
    }
}
