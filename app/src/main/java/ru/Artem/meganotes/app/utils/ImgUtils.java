package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
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
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

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

    public static Uri cameraRequest(Activity activity, int requestCode, String folderToSave) throws IOException {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = createImageFile(folderToSave);
            if (photoFile != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                activity.startActivityForResult(captureIntent, requestCode);

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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

        File file = new File(folderToSave, imageFileName);
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();
        return "file://" + file.getAbsolutePath();
    }

    public static Bitmap scaleImg(Uri uri, Context context, int width, int height) throws IOException {

        InputStream inputStream;
        InputStream inputStreamScale;
        inputStream = context.getContentResolver()
                .openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions);

        onlyBoundsOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, width, height);
        onlyBoundsOptions.inJustDecodeBounds = false;

        inputStreamScale = context.getContentResolver()
                .openInputStream(uri);

        return BitmapFactory.decodeStream(inputStreamScale, null, onlyBoundsOptions);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        int tmp;

        if (width > height){
            tmp = height;
            height = width;
            width = tmp;
        }

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
