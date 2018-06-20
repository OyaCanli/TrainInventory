package com.canli.oya.traininventory.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class BitmapUtils {

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();

        return File.createTempFile(
                imageFileName,  //prefix
                ".jpg",  //suffix
                storageDir      //directory
        );
    }

    public static void deleteImageFile(Context context, String imagePath) {
        // Get the file
        File imageFile = new File(imagePath);
        // Delete the image
        boolean deleted = imageFile.delete();
        // If there is an error deleting the file, show a Toast
        if (!deleted) {
            Toast.makeText(context, "Error during delete", Toast.LENGTH_SHORT).show();
        }
    }
}
