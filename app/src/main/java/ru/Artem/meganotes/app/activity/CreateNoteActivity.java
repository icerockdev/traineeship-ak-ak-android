package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
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
import ru.Artem.meganotes.app.fragments.BaseNoteFragment;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.pojo.DateUtils;
import ru.Artem.meganotes.app.pojo.HelpClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Артем on 22.04.2016.
 */
public class CreateNoteActivity extends AppCompatActivity {

    private EditText mTitleNote;
    private EditText mContentNote;
    private Spinner mSpinner;
    private ImageView mImageView;
    private View mView;

    private Uri mOutFilePath = null;

    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_CAPTURE = 2;
    private final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        mTitleNote = (EditText) findViewById(R.id.editTitleNote);
        mContentNote = (EditText) findViewById(R.id.editContentNote);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mView = findViewById(R.id.layoutCreate);

        setSupportActionBar(toolbar);

        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add(getString(R.string.drawer_item_work));
        spinnerList.add(getString(R.string.drawer_item_home));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerList);
        mSpinner.setAdapter(spinnerAdapter);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_activity_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.uploadImg) {

            final AddImageDialog addImageDialog = new AddImageDialog();

            addImageDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddImageDialog);
            addImageDialog.show(getSupportFragmentManager(), AddImageDialog.DIALOG_KEY);

            addImageDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            if (ActivityCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                                    || ActivityCompat.checkSelfPermission(CreateNoteActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(CreateNoteActivity.this,
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);


                            } else {
                                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (captureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    HelpClass helpClass = new HelpClass();
                                    try {
                                        photoFile = helpClass.createImageFile();
                                    } catch (IOException ex) {
                                        Log.e(LOG_TAG, "Не удалось создать файл изображения");
                                    }
                                    if (photoFile != null) {
                                        mOutFilePath = Uri.fromFile(photoFile);
                                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutFilePath);
                                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                                    }
                                }
                            }
                            break;
                        case 1:
                            if (ActivityCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(CreateNoteActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                            } else {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                            }
                            break;
                    }
                    addImageDialog.onDismiss(null);
                }
            });

        } else if (id == android.R.id.home)  {

            onBackPressed();

        } else if (id == R.id.doneCreate) {

            if (!mTitleNote.getText().toString().isEmpty()
                    || !mContentNote.getText().toString().isEmpty()
                    || mImageView.getDrawable() != null) {

                DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
                String date = DateUtils.getDate();
                if (mImageView.getDrawable() == null) {
                    dataBaseHelper.addData(mTitleNote.getText().toString(),
                            mContentNote.getText().toString(), "null",
                            date, date, mSpinner.getSelectedItemPosition());
                } else {
                    dataBaseHelper.addData(mTitleNote.getText().toString(),
                            mContentNote.getText().toString(), mOutFilePath.toString(),
                            date, date, mSpinner.getSelectedItemPosition());
                }

                ModelNote newNote = dataBaseHelper.getInsertedNote();

                Intent intent = new Intent();
                intent.putExtra(BaseNoteFragment.CREATE_NOTE_KEY, newNote);
                setResult(BaseNoteFragment.CREATE_NOTE_REQUEST, intent);
                finish();
            } else {
                Snackbar.make(mView, getString(R.string.snackBarMessage), Snackbar.LENGTH_LONG).show();
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mImageView.setImageURI(mOutFilePath);
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    mOutFilePath = data.getData();
                    mImageView.setImageURI(mOutFilePath);
                }
                break;
        }
    }
}
