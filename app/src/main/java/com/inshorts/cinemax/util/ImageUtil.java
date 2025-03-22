package com.inshorts.cinemax.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public String saveImageToInternalStorage(InputStream inputStream, String imageName, Context context) {
        File directory = new File(context.getFilesDir(), "images"); // Internal storage directory
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if not exists
        }

        File file = new File(directory, imageName + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            inputStream.close(); // Close input stream after writing
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath(); // Return file path
    }

    public static boolean imageExistsInInternalStorage(String imagePath) {
        File file = new File(imagePath); // Directly check the full file path
        return file.exists();
    }

    public static Bitmap loadImageFromInternalStorage(String imagePath) {
        File file = new File(imagePath); // Use full file path
        return decodeFile(file);
    }

    private static Bitmap decodeFile(File file) {
        if (!file.exists()) {
            return null; // Prevent errors if file doesn't exist
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
