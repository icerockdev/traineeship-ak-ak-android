package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.Dialog;
import android.content.DialogInterface;
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
import ru.Artem.meganotes.app.dialogs.IncorrectDataDialog;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.GridLayoutUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

/**
 * Created by Артем on 22.04.2016.
 */

public class CreateNoteActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener,
        CustomImageMaker.OnDeleteImageListener, IncorrectDataDialog.OnInteractionActivity {

    private EditText mTitleNote;
    private EditText mContentNote;
    private LinearLayout mRootLayoutActivity;
    private android.support.v7.widget.GridLayout mLayoutForImages;
    private RelativeLayout mLastDeletedElement;
    private List<String> mDeletedPaths;
    private int mImageWidth;
    private int mTempIdForImages;
    private Note mEditNote;
    private int mColumnCount = 2;

    private Uri mOutFilePath = null;

    private final String LOG_TAG = CreateNoteActivity.class.getName();

    private final int GALLERY_REQUEST = 10;
    private final int CAMERA_REQUEST = 11;

    public final static String INTENT_RESULT_EXTRA_CREATE_NOTE = "noteCreate";
    public final static String INTENT_EXTRA_EDIT_NOTE = "noteEdit";
    private String mSavePath;

    private static final boolean DEBUG = false;

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
        mRootLayoutActivity = (LinearLayout) findViewById(R.id.layoutCreate);

        mColumnCount = getResources().getInteger(R.integer.columnCount);

        mLayoutForImages.setColumnCount(mColumnCount);

        mEditNote = getIntent().getParcelableExtra(INTENT_EXTRA_EDIT_NOTE);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        mTempIdForImages = 0;
        mSavePath = getExternalFilesDir(null).getAbsolutePath();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mEditNote == null) {
                getSupportActionBar().setTitle(R.string.new_note_title);
            } else {
                getSupportActionBar().setTitle(R.string.edit_note);
                mTitleNote.setText(mEditNote.getNameNote());
                mContentNote.setText(mEditNote.getContent());

                if (!mEditNote.getPathImg().isEmpty()) {
                    for (String imagePath : mEditNote.getPathImg()) {
                        GridLayoutUtils.addViewToGrid(mLayoutForImages,
                                CustomImageMaker.initCustomView(imagePath, false, mImageWidth, mTempIdForImages++, this),
                                mImageWidth);
                    }
                }
            }
        }
        mDeletedPaths = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_create, menu);

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
            saveNoteAndExit();
        } else if (id == R.id.close_with_out_save) {
            finish();
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                mOutFilePath = data.getData();

                try {
                    MediaStore.Images.Media.getBitmap(getContentResolver(), mOutFilePath);
                } catch (IOException e) {
                    Snackbar.make(mRootLayoutActivity, getString(R.string.str_problems_message), Snackbar.LENGTH_LONG).show();
                }
            }
            GridLayoutUtils.addViewToGrid(
                    mLayoutForImages,
                    CustomImageMaker.initCustomView(mOutFilePath.toString(), false, mImageWidth, mTempIdForImages++, this),
                    mImageWidth);
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
        mLastDeletedElement = (RelativeLayout) mLayoutForImages.getChildAt(id);
        mLayoutForImages.removeView(mLastDeletedElement);
        CustomImageMaker cutomImage = (CustomImageMaker) mLastDeletedElement;
        mDeletedPaths.add(cutomImage.getImagePath());
        mTempIdForImages--;
        syncIdImagesAndChilds();
    }

    @Override
    public void returnLastDeletedElement() {
        mLayoutForImages.addView(mLastDeletedElement);
        mTempIdForImages++;
        mDeletedPaths.remove(mDeletedPaths.size());
        syncIdImagesAndChilds();
    }

    private void syncIdImagesAndChilds() {
        int imagesCount = mLayoutForImages.getChildCount();
        for (int i = 0; i < imagesCount; i++) {
            CustomImageMaker customImageMaker = (CustomImageMaker) mLayoutForImages.getChildAt(i);
            customImageMaker.setIndex(i);
        }
    }

    private void saveNoteAndExit() {
        if (!mContentNote.getText().toString().isEmpty()) {
            List<String> mImagePaths = new ArrayList<>();
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
            String date = DateUtils.getDate();
            int imagesCount = mLayoutForImages.getChildCount();

            for (int i = 0; i < imagesCount; i++) {
                RelativeLayout customImageMaker = (RelativeLayout) mLayoutForImages.getChildAt(i);
                RelativeLayout rootLayoutInCustomView = (RelativeLayout) customImageMaker.getChildAt(0);
                ImageView imageViewInCustomView = (ImageView) rootLayoutInCustomView.getChildAt(0);
                LinearLayout llInCustomImage = (LinearLayout) rootLayoutInCustomView.getChildAt(1); //получили лейаут, который содержит текствью и кнопку
                TextView textViewWithName = (TextView) llInCustomImage.getChildAt(0); // вот тут получили текствью, которое отображает имя

                Bitmap bitmap = ((BitmapDrawable) imageViewInCustomView.getDrawable()).getBitmap();

                try {
                    mImagePaths.add(ImgUtils.savePicture(bitmap, mSavePath, textViewWithName.getText().toString()));
                } catch (IOException e) {
                    Snackbar.make(mRootLayoutActivity, R.string.str_problems_save, Snackbar.LENGTH_LONG).show();
                }
            }

            Intent intent = new Intent();

            if (mEditNote == null) {
                //если заметка новая, то попадём сюда, создадим её и отправим в результате
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

                intent.putExtra(INTENT_RESULT_EXTRA_CREATE_NOTE, newNote);
            } else {
                //если заметка старая, и просто редактировалась
                mEditNote.setNameNote(mTitleNote.getText().toString());
                mEditNote.setContent(mContentNote.getText().toString());
                mEditNote.setDateLastUpdateNote(date);
                mEditNote.setListPathImages(mImagePaths);
                helper.updateNote(mEditNote);

                intent.putExtra(INTENT_EXTRA_EDIT_NOTE, mEditNote);
            }

            setResult(RESULT_OK, intent);
            finish();
        } else if (mContentNote.getText().toString().isEmpty()
                && (mLayoutForImages.getChildCount() != 0 || !mTitleNote.getText().toString().isEmpty())) {
            IncorrectDataDialog incorrectDataDialog = new IncorrectDataDialog();
            incorrectDataDialog.show(getSupportFragmentManager().beginTransaction(), IncorrectDataDialog.DIALOG_KEY);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        saveNoteAndExit();
    }

    @Override
    public void callBack(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                finish();
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
        }
    }
}
