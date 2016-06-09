package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by Артем on 03.05.2016.
 */
public class ImgUtils {

    private static final String LOG_TAG = ImgUtils.class.getName();
    private static final boolean DEBUG = true;

    public static File createImageFile(String folderToSave) throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);
        return new File(folderToSave, imageFileName);
    }

    public static Uri cameraRequest(Activity activity, int requestCode, String folderToSave) throws IOException {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createImageFile(folderToSave);

            if (DEBUG) Log.d(LOG_TAG, "we have in photoFile path: " + photoFile.getPath());

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            activity.startActivityForResult(captureIntent, requestCode);

            return Uri.fromFile(photoFile);
        }
        return null;
    }

    public static void galleryRequest(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    public static String savePicture(Bitmap bitmap, String folderToSave) throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

        File file = new File(folderToSave, imageFileName);
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();
        return "file://" + file.getAbsolutePath();
    }
}
