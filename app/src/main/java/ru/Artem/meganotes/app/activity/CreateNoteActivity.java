package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import ru.Artem.meganotes.app.CustomGridView;
import ru.Artem.meganotes.app.adapters.GridViewAdapter;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.CustomImageMaker;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

/**
 * Created by Артем on 22.04.2016.
 */
public class CreateNoteActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener, CustomImageMaker.OnDeleteImageListener {

    private EditText mTitleNote;
    private EditText mContentNote;
    private ImageView mImageView;
    private LinearLayout mView;
    private RelativeLayout mLayoutForImages;
    private RelativeLayout lastDeletedElement;
    private List<String> imagePaths = new ArrayList<>();
    private int imageWidth;
    private GridViewAdapter mGridViewAdapter;

    private Uri mOutFilePath = null;
    private ComponentName mCallingActivity;

    private final int GALLERY_REQUEST = 10;
    private final int CAMERA_REQUEST = 11;
    private final String LOG_TAG = CreateNoteActivity.class.getName();
    private String sSavePath;
    public final static String CREATE_NOTE_KEY = "noteCreate";
    public static final int CREATE_NOTE_REQUEST = 1001;

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int placeForImages = display.getWidth() - 32; //32 = padding x2
        imageWidth = (placeForImages / 2) - 10;

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        mImageView = (ImageView) findViewById(R.id.imageView);
        CustomGridView customGridView = (CustomGridView) findViewById(R.id.gridView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mView = (LinearLayout) findViewById(R.id.layoutCreate);
        mLayoutForImages = (RelativeLayout) findViewById(R.id.LayoutForImages);

        mGridViewAdapter = new GridViewAdapter(this, imagePaths, R.drawable.ic_delete, imageWidth);

        customGridView.setExpanded(true);
        customGridView.setColumnWidth(imageWidth);
        customGridView.setAdapter(mGridViewAdapter);


        mCallingActivity = getCallingActivity();

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mCallingActivity.getClassName().equals(MainActivity.class.getName())) {
                getSupportActionBar().setTitle(R.string.new_note);
            } else {
                getSupportActionBar().setTitle(R.string.edit_note);//поменять на заголовок заметки
            }
        }
        sSavePath = getExternalFilesDir(null).getAbsolutePath();

        Log.d(LOG_TAG, "save path: " + sSavePath);

        if (DEBUG) {
            Log.d(LOG_TAG, "our screen width is: " + display.getWidth());
            Log.d(LOG_TAG, "our screen height is: " + display.getHeight());
            Log.d(LOG_TAG, "we have placeForImages value: " + placeForImages);
            Log.d(LOG_TAG, "our imageWidth is " + imageWidth);
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
                    imagePaths.add(ImgUtils.savePicture(bitmap, sSavePath));
                } catch (IOException e) {
                    Snackbar.make(mView, R.string.str_problems_save, Snackbar.LENGTH_LONG).show();
                }
            }
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
            Note newNote;
            try {
                newNote = helper.addNote(mTitleNote.getText().toString(), mContentNote.getText().toString(), date, imagePaths);
            } catch (SQLiteException e) {
                newNote = null;
                Snackbar.make(mView, R.string.cant_add_note_message, Snackbar.LENGTH_LONG).show();
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
        } //иначе выдвавать ошибку снекбаром о том что нельзя создать пустую заметку
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (DEBUG) {
            Log.d(LOG_TAG, "we have requestCode = " + requestCode);
            Log.d(LOG_TAG, "we have resultCode = " + resultCode);
        }
        if ((resultCode == RESULT_OK) && (requestCode == GALLERY_REQUEST)) {
            Uri selectedImage = data.getData();
            try {
                MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (IOException e) {
                Snackbar.make(mView, getString(R.string.str_problems_message), Snackbar.LENGTH_LONG).show();
            }
            if (DEBUG) {
                Log.d(LOG_TAG, "we have selectedImage is: " + selectedImage);
            }

            imagePaths.add(selectedImage.toString());
            mGridViewAdapter.notifyDataSetChanged();

            /*CustomImageMaker image = new CustomImageMaker(CreateNoteActivity.this,
                    "fileName?",
                    selectedImage.toString(),
                    false,
                    imageWidth,
                    imageWidth,
                    0); // пока что так, затем сделаю систему раздачи id для картинок
            mLayoutForImages.addView(image);*/
        }
        if ((resultCode == RESULT_OK) && (requestCode == CAMERA_REQUEST)) {
            Log.d(LOG_TAG, "we take from extras is: " + mOutFilePath);
            imagePaths.add(mOutFilePath.toString());
            mGridViewAdapter.notifyDataSetChanged();

            /*if (mOutFilePath != null) {
                CustomImageMaker image = new CustomImageMaker(CreateNoteActivity.this,
                        "fileName?",
                        mOutFilePath.toString(),
                        false,
                        imageWidth,
                        imageWidth,
                        0); // аналогично тому что выше
                mLayoutForImages.addView(image);
            }*/
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
                        mOutFilePath = ImgUtils.cameraRequest(CreateNoteActivity.this, CAMERA_REQUEST, sSavePath);
                    } catch (IOException e) {
                        mOutFilePath = null;
                        Log.d(LOG_TAG, e.getMessage());
                        Snackbar.make(mView, getString(R.string.str_problems_save), Snackbar.LENGTH_LONG).show();
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
        if (DEBUG) {
            Log.d(LOG_TAG, "we in interface method in another activity");
            Log.d(LOG_TAG, "we remover " + lastDeletedElement.getClass().getName() + " with index " + id);
        }
    }

    @Override
    public void returnLastDeletedElement() {
        mLayoutForImages.addView(lastDeletedElement);
    }
}
