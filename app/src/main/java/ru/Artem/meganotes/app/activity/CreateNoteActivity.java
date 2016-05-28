package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    private final String LOG_TAG = CreateNoteActivity.class.getName();

    private final int GALLERY_REQUEST = 10;
    private final int CAMERA_REQUEST = 11;
    public final static String INTENT_EXTRA_CREATE_NOTE = "noteCreate";
    public final static String INTENT_EXTRA_EDIT_NOTE = "noteEdit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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
            saveNote();
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

    private void saveNote() {
        if (!mContentNote.getText().toString().isEmpty()) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());
            String date = DateUtils.getDate();
            String filePath = "null";

            if (mImageView.getDrawable() != null) {
                filePath = mOutFilePath.toString();
            }

            Intent intent = new Intent();

            if (mEditNote == null) {
                dataBaseHelper.addData(mTitleNote.getText().toString(),
                        mContentNote.getText().toString(), filePath,
                        date, date);
                ModelNote newNote = dataBaseHelper.getInsertedNote();

                intent.putExtra(INTENT_EXTRA_CREATE_NOTE, newNote);
            } else {//добавить изменение в БД
                mEditNote.setNameNote(mTitleNote.getText().toString());
                mEditNote.setContent(mContentNote.getText().toString());
                mEditNote.setLastUpdateNote(DateUtils.getDate());

                intent.putExtra(INTENT_EXTRA_EDIT_NOTE, mEditNote);
            }
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        saveNote();
    }
}
