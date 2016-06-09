package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.dialogs.IncorrectDataDialog;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

/**
 * Created by Артем on 22.04.2016.
 */
public class CreateNoteActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener,
        IncorrectDataDialog.OnInteractionActivity {

    private EditText mTitleNote;
    private EditText mContentNote;
    private ImageView mImageView;
    private Note mEditNote;
    private LinearLayout mRootLayoutActivity;
    private List<String> mImagePaths;

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

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRootLayoutActivity = (LinearLayout) findViewById(R.id.layoutCreate);

        mEditNote = getIntent().getParcelableExtra(INTENT_EXTRA_EDIT_NOTE);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mEditNote == null) {
                getSupportActionBar().setTitle(R.string.new_note_title);
            } else {
                getSupportActionBar().setTitle(R.string.edit_note_title);
                mTitleNote.setText(mEditNote.getNameNote());
                mContentNote.setText(mEditNote.getContent());
            }
        }
        mSavePath = this.getFilesDir().toString();
        mImagePaths = new ArrayList<>();
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
            if (requestCode == GALLERY_REQUEST) mOutFilePath = data.getData();

            mImageView.setImageURI(mOutFilePath);
        }
    }

    @Override
    public void onClick (DialogFragment dialogFragment,int position){
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

    private void saveNoteAndExit() {
        if (!mContentNote.getText().toString().isEmpty()) {
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
            String date = DateUtils.getDate();

            if (mImageView.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                try {
                    mImagePaths.add(ImgUtils.savePicture(bitmap, mSavePath));
                } catch (IOException e) {
                    Snackbar.make(mRootLayoutActivity, R.string.str_problems_save, Snackbar.LENGTH_LONG).show();
                }
            }

            Intent intent = new Intent();

            if (mEditNote == null) {
                Note newNote;

                try {
                    newNote = helper.addNote(mTitleNote.getText().toString(), mContentNote.getText().toString(), date, mImagePaths);
                } catch (SQLiteException e) {
                    newNote = null;
                    Snackbar.make(mRootLayoutActivity, R.string.cant_add_note_message,Snackbar.LENGTH_LONG).show();
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
                mEditNote.setNameNote(mTitleNote.getText().toString());
                mEditNote.setContent(mContentNote.getText().toString());
                mEditNote.setDateLastUpdateNote(DateUtils.getDate());

                for (String obj : mImagePaths) {
                    mEditNote.setPathImg(obj);
                }

                helper.updateNote(mEditNote);

                intent.putExtra(INTENT_EXTRA_EDIT_NOTE, mEditNote);
            }

            setResult(RESULT_OK, intent);
            finish();
        } else if (mContentNote.getText().toString().isEmpty()
                && (mImageView.getDrawable() != null || !mTitleNote.getText().toString().isEmpty())) {
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
