package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.CustomImageMaker;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.GridLayoutUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

/**
 * Created by Артем on 22.04.2016.
 */
public class CreateNoteActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener, CustomImageMaker.OnDeleteImageListener {

    private EditText mTitleNote;
    private EditText mContentNote;
    private LinearLayout mRootLayoutActivity;
    private android.support.v7.widget.GridLayout mLayoutForImages;
    private RelativeLayout lastDeletedElement;
    private List<String> mImagePaths = new ArrayList<>();
    private int mImageWidth;
    private int mTempIdForImages;

    private Uri mOutFilePath = null;
    private ComponentName mCallingActivity;

    private final int GALLERY_REQUEST = 10;
    private final int CAMERA_REQUEST = 11;
    private final String LOG_TAG = CreateNoteActivity.class.getName();
    private String mSavePath;
    public final static String CREATE_NOTE_KEY = "noteCreate";
    public static final int CREATE_NOTE_REQUEST = 1001;

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        mImageWidth = display.getWidth() / 2;

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRootLayoutActivity = (LinearLayout) findViewById(R.id.layoutCreate);
        mLayoutForImages = (GridLayout) findViewById(R.id.LayoutForImages);

        mCallingActivity = getCallingActivity();

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mCallingActivity.getClassName().equals(MainActivity.class.getName())) {
                getSupportActionBar().setTitle(R.string.new_note);
            } else {
                getSupportActionBar().setTitle(R.string.edit_note);
            }
        }
        mSavePath = getExternalFilesDir(null).getAbsolutePath();
        mTempIdForImages = 0;
        if (DEBUG) {
            Log.d(LOG_TAG, "our screen width is: " + display.getWidth());
            Log.d(LOG_TAG, "our screen height is: " + display.getHeight());
            Log.d(LOG_TAG, "our mImageWidth is " + mImageWidth);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mCallingActivity.getClassName().equals(MainActivity.class.getName())) {
            getMenuInflater().inflate(R.menu.menu_create, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.upload_img) {

            final AddImageDialog addImageDialog = new AddImageDialog();

            addImageDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddImageDialog);
            addImageDialog.show(getSupportFragmentManager(), AddImageDialog.DIALOG_KEY);

        } else if (id == android.R.id.home) {
            finish();

        } else if (id == R.id.close_with_out_save) {
            mContentNote.setText("");
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        if (!mContentNote.getText().toString().isEmpty()) {

            String date = DateUtils.getDate();

            int imagesCount = mLayoutForImages.getChildCount();
            for (int i = 0; i < imagesCount; i++) {
                RelativeLayout customImageMaker = (RelativeLayout) mLayoutForImages.getChildAt(i);
                RelativeLayout layoutInCustomView = (RelativeLayout) customImageMaker.getChildAt(0);
                ImageView imageViewInCustomView = (ImageView) layoutInCustomView.getChildAt(0);
                if (DEBUG) {
                    Log.d(LOG_TAG, "we have in 1st child of CustomImageMaker" + layoutInCustomView.getClass());
                    Log.d(LOG_TAG, "we have in 2nd child of CustomImageMaker" + imageViewInCustomView.getClass());
                }
                Bitmap bitmap = ((BitmapDrawable) imageViewInCustomView.getDrawable()).getBitmap();
                try {
                    mImagePaths.add(ImgUtils.savePicture(bitmap, mSavePath));
                } catch (IOException e) {
                    Snackbar.make(mRootLayoutActivity, R.string.str_problems_save, Snackbar.LENGTH_LONG).show();
                }
            }
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
            Note newNote;
            try {
                newNote = helper.addNote(mTitleNote.getText().toString(), mContentNote.getText().toString(), date, mImagePaths);
            } catch (SQLiteException e) {
                newNote = null;
                Snackbar.make(mRootLayoutActivity, R.string.cant_add_note_message, Snackbar.LENGTH_LONG).show();
            }

            if (DEBUG) {
                Log.d(LOG_TAG, "what we have in newNote?");
                Log.d(LOG_TAG, "newNote name: " + newNote.getNameNote());
                Log.d(LOG_TAG, "newNote content: " + newNote.getContent());
                List<String> tmpList = newNote.getPathImg();
                Log.d(LOG_TAG, "In image newNote we have count: " + tmpList.size());
                Log.d(LOG_TAG, "content image newNote is:" + tmpList.get(0));
            }
            Intent intent = new Intent();
            intent.putExtra(CREATE_NOTE_KEY, newNote);
            setResult(CREATE_NOTE_REQUEST, intent);
        } else {
            Snackbar.make(mRootLayoutActivity,"Пожалуйста, введите текст",Snackbar.LENGTH_LONG).show();
        }
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int columnCount = 2;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) columnCount = 3;


        if ((resultCode == RESULT_OK) && (requestCode == GALLERY_REQUEST)) {
            Uri selectedImage = data.getData();
            try {
                MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                Snackbar.make(mRootLayoutActivity, getString(R.string.str_problems_message), Snackbar.LENGTH_LONG).show();
            }

            mImagePaths.add(selectedImage.toString());
            String name = ImgUtils.getFileNameByUri(selectedImage, this);

            CustomImageMaker image = new CustomImageMaker(CreateNoteActivity.this,
                    name,
                    selectedImage.toString(),
                    false,
                    mImageWidth,
                    mImageWidth,
                    mTempIdForImages);
            GridLayoutUtils.addViewToGrid(mLayoutForImages,image, mImageWidth, columnCount);
            mTempIdForImages++;
        }
        if ((resultCode == RESULT_OK) && (requestCode == CAMERA_REQUEST)) {
            mImagePaths.add(mOutFilePath.toString());
            String name = ImgUtils.getFileNameByUri(mOutFilePath, this);
            if (mOutFilePath != null) {
                CustomImageMaker image = new CustomImageMaker(CreateNoteActivity.this,
                        name,
                        mOutFilePath.toString(),
                        false,
                        mImageWidth,
                        mImageWidth,
                        mTempIdForImages);
                GridLayoutUtils.addViewToGrid(mLayoutForImages, image, mImageWidth, columnCount);
                mTempIdForImages++;
            }
        }
    }

    @Override
    public void onClick(DialogFragment dialogFragment, int position) {
        switch (position) {
            case 0:
                if (ActivityCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    try {
                        mOutFilePath = ImgUtils.cameraRequest(CreateNoteActivity.this, CAMERA_REQUEST, mSavePath);
                    } catch (IOException e) {
                        mOutFilePath = null;
                        Log.d(LOG_TAG, e.getMessage());
                        Snackbar.make(mRootLayoutActivity, getString(R.string.str_problems_save), Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    ImgUtils.galleryRequest(CreateNoteActivity.this, GALLERY_REQUEST);
                }
                break;
        }
        dialogFragment.dismiss();
    }

    @Override
    public void removeElementFromRootView(int id) {
        lastDeletedElement = (RelativeLayout) mLayoutForImages.getChildAt(id);
        mLayoutForImages.removeView(lastDeletedElement);
        mTempIdForImages--;

        int imagesCount = mLayoutForImages.getChildCount();
        for (int i = 0; i < imagesCount; i++) {
            CustomImageMaker customImageMaker = (CustomImageMaker) mLayoutForImages.getChildAt(i);
            customImageMaker.setIndex(i);
        }
    }

    @Override
    public void returnLastDeletedElement() {
        mLayoutForImages.addView(lastDeletedElement);
        mTempIdForImages++;

        int imagesCount = mLayoutForImages.getChildCount();
        for (int i = 0; i < imagesCount; i++) {
            CustomImageMaker customImageMaker = (CustomImageMaker) mLayoutForImages.getChildAt(i);
            customImageMaker.setIndex(i);
        }
    }
}
