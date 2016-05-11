package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

/**
 * Created by Артем on 22.04.2016.
 */
public class CreateNoteActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener {

    private EditText mTitleNote;
    private EditText mContentNote;
    private ImageView mImageView;
    private ModelNote mEditNote;

    private Uri mOutFilePath = null;
    private ComponentName mCallingActivity;

    private final int GALLERY_REQUEST = 10;
    private final int CAMERA_REQUEST = 11;
    private final String LOG_TAG = CreateNoteActivity.class.getName();
    public final static String CREATE_NOTE_KEY = "noteCreate";
    public static final int CREATE_NOTE_REQUEST = 1002;
    public final static String EDIT_NOTE_KEY = "noteEdit";
    public static final int EDIT_NOTE_REQUEST = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mCallingActivity = getCallingActivity();
        mEditNote = getIntent().getParcelableExtra(EDIT_NOTE_KEY);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mCallingActivity.getClassName().equals(MainActivity.class.getName())) {
                getSupportActionBar().setTitle(R.string.new_note);
            } else {
                getSupportActionBar().setTitle(mEditNote.getNameNote());
            }
        }

        if (mEditNote != null) {
            mTitleNote.setText(mEditNote.getNameNote());
            mContentNote.setText(mEditNote.getContent());

            mContentNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        mEditNote.setContent(mContentNote.getText().toString());
                    }
                }
            });

            mTitleNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        mEditNote.setNameNote(mTitleNote.getText().toString());
                    }
                }
            });

        }
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

            finish();

        } else if (id == R.id.close_with_out_save) {
            mContentNote.setText("");
            mTitleNote.setText("");

            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        if (!mContentNote.getText().toString().isEmpty()) {

            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());
            String date = DateUtils.getDate();
            String filePath = "null";

            if (mImageView.getDrawable() != null) {
                filePath = mOutFilePath.toString();
            }

            Intent intent = new Intent();

            if (mCallingActivity.getClassName().equals(MainActivity.class.getName())) {
                dataBaseHelper.addData(mTitleNote.getText().toString(),
                        mContentNote.getText().toString(), filePath,
                        date, date);
                ModelNote newNote = dataBaseHelper.getInsertedNote();

                intent.putExtra(CREATE_NOTE_KEY, newNote);
                setResult(CREATE_NOTE_REQUEST, intent);
            } //иначе dataBaseHelper.edit(...);
        }

        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                mOutFilePath = data.getData();
            }

            mImageView.setImageURI(mOutFilePath);
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
                    mOutFilePath = ImgUtils.cameraRequest(CreateNoteActivity.this, CAMERA_REQUEST, LOG_TAG);
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
}
