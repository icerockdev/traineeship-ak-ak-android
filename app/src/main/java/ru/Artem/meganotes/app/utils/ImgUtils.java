package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Created by Артем on 03.05.2016.
 */
public class ImgUtils {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("d.MM.yyyy k:mm", Locale.ROOT);

    private final static String LOG_TAG = ImgUtils.class.getName();

    private static final boolean DEBUG = true;
    private static String mCurrentPhotoPath;

    private static File createImageFile(String folderToSave) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        return new File(folderToSave, imageFileName);
    }

    public static String getFileNameByUri(Uri uri, Context context) {
        String tmp = uri.toString();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            tmp = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            cursor.close();
        }

        int index = tmp.lastIndexOf('/') + 1;
        char[] massive = new char[tmp.length() - index];
        tmp.getChars(index, tmp.length(), massive, 0);

        return new String(massive);
    }

    public static Uri cameraRequest(Context context, int requestCode, String folderToSave) throws IOException {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = createImageFile(folderToSave);
            if (photoFile != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                ((Activity) context).startActivityForResult(captureIntent, requestCode);

                return Uri.fromFile(photoFile);
            }
        }
        return null;
    }

    public static void galleryRequest(Context context, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ((Activity) context).startActivityForResult(photoPickerIntent, requestCode);
    }

    public static String savePicture(Bitmap bitmap, String folderToSave) throws IOException {
        String timeStamp = sDateFormat.toString();
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

        File file = new File(folderToSave, imageFileName);
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();
        return "file://" + file.getAbsolutePath();
    }
}
