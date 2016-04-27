package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteImageDialog;
import ru.Artem.meganotes.app.fragments.BaseNoteFragment;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.pojo.DateUtils;
import ru.Artem.meganotes.app.pojo.HelpClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements EditText.OnEditorActionListener {

    private String[] mWhere;
    private ImageView mImageView;

    private Uri mOutFilePath = null;
    private ModelNote mSelectNote;

    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_CAPTURE = 2;
    public static final String DELETE_IMG = "null";
    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSelectNote = getIntent().getParcelableExtra(BaseNoteFragment.EDIT_NOTE_KEY);
        mWhere = new String[] {String.valueOf(mSelectNote.getId())};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailed);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextView textView = (TextView) findViewById(R.id.textView);
        final EditText titleEdit = (EditText) findViewById(R.id.editTitle);
        final EditText contentEdit = (EditText) findViewById(R.id.editContent);

        final  DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

        textView.setText(mSelectNote.getLastUpdateNote());
        contentEdit.setText(mSelectNote.getContent());
        titleEdit.setText(mSelectNote.getNameNote());

        titleEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String date = DateUtils.getDate();

                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                mSelectNote.setNameNote(v.getText().toString());
                mSelectNote.setLastUpdateNote(date);

                dataBaseHelper.editData(DataBaseHelper.TITLE_NOTES_COLUMN, mWhere, v.getText().toString(), date);
                return true;
            }
        });

        contentEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String date = DateUtils.getDate();

                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                mSelectNote.setLastUpdateNote(date);
                mSelectNote.setContent(v.getText().toString());

                dataBaseHelper.editData(DataBaseHelper.CONTENT_COLUMN, mWhere, v.getText().toString(), date);
                return true;
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageNote);
        setImg(Uri.parse(mSelectNote.getPathImg()));

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddImageDialog addImageDialog = new AddImageDialog();

                addImageDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddImageDialog);
                addImageDialog.show(getSupportFragmentManager(), AddImageDialog.DIALOG_KEY);

                addImageDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                if (ActivityCompat.checkSelfPermission(DetailedActivity.this, Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(DetailedActivity.this,
                                            new String[]{Manifest.permission.CAMERA}, 0);

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
                                if (ActivityCompat.checkSelfPermission(DetailedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(DetailedActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            }
        });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final DeleteImageDialog deleteImageDialog = new DeleteImageDialog();

                deleteImageDialog.show(getSupportFragmentManager(), DeleteImageDialog.DIALOG_KEY);

                deleteImageDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        deleteImageDialog.onDismiss(null);

                        switch (position) {
                            case 0:
                                String date = DateUtils.getDate();

                                mImageView.setImageBitmap(null);

                                dataBaseHelper.editData(DataBaseHelper.IMG_PATH_COLUMN, mWhere, DELETE_IMG, date);
                                mSelectNote.setPathImg(DELETE_IMG);
                                mSelectNote.setLastUpdateNote(date);
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Bitmap image = (Bitmap) msg.obj;
            mImageView.setImageBitmap(image);
        }
    };


    private void setImg(final Uri pathImg) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream;

                try {
                    inputStream = getContentResolver()
                            .openInputStream(pathImg);
                    final Message message = handler.obtainMessage(1,
                            BitmapFactory.decodeStream(inputStream, null, null));

                    handler.sendMessage(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BaseNoteFragment.EDIT_NOTE_KEY, mSelectNote);
        setResult(BaseNoteFragment.EDIT_NOTE_REQUEST, intent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setImg(mOutFilePath);

                    mSelectNote.setPathImg(mOutFilePath.toString());
                    dataBaseHelper.editData(DataBaseHelper.IMG_PATH_COLUMN, mWhere,
                            mOutFilePath.toString(), DateUtils.getDate());
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    mOutFilePath = data.getData();
                    setImg(mOutFilePath);

                    mSelectNote.setPathImg(mOutFilePath.toString());
                    dataBaseHelper.editData(DataBaseHelper.IMG_PATH_COLUMN, mWhere,
                            mOutFilePath.toString(), DateUtils.getDate());
                }
                break;
        }
    }
}
