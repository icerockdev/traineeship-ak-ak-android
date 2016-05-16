package ru.Artem.meganotes.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.Artem.meganotes.app.activity.CreateNoteActivity;

/**
 * Created by Артем on 03.05.2016.
 */
public class ImgUtils {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("d.MM.yyyy k:m", Locale.ROOT);

    private static final boolean DEBUG = true;

    public static File createImageFile(String folderToSave) throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();
        String imageFileName = "JPEG_" + timeStamp + "_";
        return new File(folderToSave, imageFileName + ".jpg");
    }

    public static Uri cameraRequest(Context context, int requestCode, String LOG_TAG, String folderToSave) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {

            File photoFile = null;

            try {
                photoFile = createImageFile(folderToSave);
                if (DEBUG) Log.d(LOG_TAG,"we have in photoFile path: "+photoFile.getPath());
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Не удалось создать файл изображения");
            }

            if (photoFile != null) {
                Uri mOutFilePath = Uri.fromFile(photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutFilePath);
                ((Activity) context).startActivityForResult(captureIntent, requestCode);

                return mOutFilePath;
            }
        }
        return null;
    }

    public static void galleryRequest(Context context, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ((Activity) context).startActivityForResult(photoPickerIntent, requestCode);
    }

    public static String savePicture(ImageView iv, String folderToSave, LinearLayout mLayout, Context context)
    {
        OutputStream fOut = null;
        String timeStamp = sDateFormat.toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        String newPath = null;
        try {
            File file = new File(folderToSave, imageFileName +".jpg");
            fOut = new FileOutputStream(file);

            iv.buildDrawingCache();
            Bitmap bitmap = iv.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            newPath = file.getAbsolutePath();
        }
        catch (Exception e)
        {
            Snackbar snackbar = Snackbar
                    .make(mLayout, "Not enough space in the internal memory", Snackbar.LENGTH_LONG);
            snackbar.show();
            return e.getMessage();
        }
        return "file://"+newPath; // возвращает новый путь, который необходимо передать в базу данных для сохранения
    }
}
