package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.Artem.meganotes.app.R;


/**
 * Created by Артем on 03.05.2016.
 */
public class ImgUtils {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("d.MM.yyyy k:mm", Locale.ROOT);

    private final static String GALLERY_CONST = "media";

    private final static String LOG_TAG = ImgUtils.class.getName();

    private static final boolean DEBUG = true;


    private static File createImageFile(String folderToSave) throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

        return new File(folderToSave, imageFileName);
    }

    public static String getFileNameByUri(Uri uri, Context context) throws FileNotFoundException {
        String tmp = uri.toString();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (DEBUG) Log.d(LOG_TAG,"we have string version of tmp: "+tmp);
        if (DEBUG) Log.d(LOG_TAG,"we have in cursor is "+cursor);
        if (DEBUG) Log.d(LOG_TAG,"we have authority? "+uri.getAuthority());
        if (uri.getAuthority().equals(GALLERY_CONST))
        {
            if (cursor != null) {
                cursor.moveToFirst();
                tmp = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                if (DEBUG) Log.d(LOG_TAG, "we have in tmp after changing " + tmp);
                cursor.close();
            }
            if (DEBUG) Log.d(LOG_TAG, "we have problems with " + tmp);
            int index = tmp.lastIndexOf('/') + 1;
            char[] massive = new char[tmp.length() - index];
            tmp.getChars(index, tmp.length(), massive, 0);

            return new String(massive);
        }
        else{
            InputStream is;
            is = context.getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            String timeStamp = DateUtils.getDateCreateFile();
            String imageFileName = String.format("JPEG_%s.jpg", timeStamp);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageFileName;
        }
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

    public static void galleryRequest(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    public static String savePicture(Bitmap bitmap, String folderToSave, String name) throws IOException {
        File file = new File(folderToSave, name);
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

    public static int getCustomImageWidth(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth() / 2;
    }
    public static void initLayout(Context context, GridLayout initLayout)
    {
        int columnCount = context.getResources().getInteger(R.integer.columnCount);
        initLayout.setColumnCount(columnCount);
    }
}
