package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Артем on 03.05.2016.
 */
public class ImgUtils {
    private static final String LOG_TAG = ImgUtils.class.getName();

    public static File createImageFile() throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName + ".jpg");
    }

    public static Uri cameraRequest(Activity activity, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {

            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Не удалось создать файл изображения");
            }

            if (photoFile != null) {

                Uri mOutFilePath = Uri.fromFile(photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutFilePath);
                activity.startActivityForResult(captureIntent, requestCode);

                return mOutFilePath;
            }
        }
        return null;
    }

    public static void galleryRequest(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }
}
