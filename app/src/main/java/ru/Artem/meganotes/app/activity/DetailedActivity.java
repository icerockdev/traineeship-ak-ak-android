package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener,
        DeleteImageDialog.OnClickListenerDelete {

    private String[] mWhere;
    private ImageView mImageView;
    private TextView mTxtContent;
    private LinearLayout mLayout;

    private Uri mOutFilePath = null;
    private Note mSelectNote;

    private final int EDIT_NOTE_REQUEST = 1002;
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;

    public final static String INTENT_EXTRA_OPEN_NOTE = "noteOpen";

    private final int EDIT_NOTE_TABLE = 0;
    private final int EDIT_IMAGE_TABLE = 1;
    public static final String DELETE_IMG = "null";
    private String mSavePath;

    private final String LOG_TAG = DetailedActivity.class.getName();

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed);

        mSelectNote = getIntent().getParcelableExtra(INTENT_EXTRA_OPEN_NOTE);
        mWhere = new String[] {String.valueOf(mSelectNote.getId())};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailed);
        mLayout = (LinearLayout) findViewById(R.id.layout);
        mImageView = (ImageView) findViewById(R.id.imageNote);
        mTxtContent = (TextView) findViewById(R.id.txtContent);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mSelectNote.getNameNote());
        }

       mTxtContent.setText(mSelectNote.getContent());

        List<String> tempList = mSelectNote.getPathImg();

        if (!tempList.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "we have not emptry List");
                for (String item : tempList) {
                    Log.d(LOG_TAG, "path: " + item);
                }
            }
            setImg(Uri.parse(tempList.get(0))); // здесь нужно будет внести изменения при
            // множественном добавление, вместо 0 будет переменная из цикла для заполнения
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddImageDialog addImageDialog = new AddImageDialog();

                addImageDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AddImageDialog);
                addImageDialog.show(getSupportFragmentManager(), AddImageDialog.DIALOG_KEY);
            }
        });
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteImageDialog deleteImageDialog = new DeleteImageDialog();

                deleteImageDialog.show(getSupportFragmentManager(), DeleteImageDialog.DIALOG_KEY);
                return true;
            }
        });
        mSavePath = this.getFilesDir().toString();
    }

    private void setImg(final Uri pathImg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream;
                if (DEBUG) {
                    Log.d(LOG_TAG, "we in setIMg, and have in path is: " + pathImg.toString());
                }
                try {
                    inputStream = getContentResolver().openInputStream(pathImg);
                    final Message message = mHandler.obtainMessage(1,
                            BitmapFactory.decodeStream(inputStream, null, null));
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Error is: " + e.getMessage());
                }
            }
        });
        thread.start();
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Bitmap image = (Bitmap) msg.obj;
            mImageView.setImageBitmap(image);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_detailed, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                initSaveIntent();

                finish();
                return true;
            case R.id.edit_note:
                Intent intent = new Intent(this, CreateNoteActivity.class);

                intent.putExtra(CreateNoteActivity.INTENT_EXTRA_EDIT_NOTE, mSelectNote);
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
                return true;
            case R.id.delete_note:
                mSelectNote.setDeletedNote(true);
                initSaveIntent();

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSaveIntent() {
        Intent intent = new Intent();

        intent.putExtra(INTENT_EXTRA_OPEN_NOTE, mSelectNote);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        initSaveIntent();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
                if (requestCode == GALLERY_REQUEST) mOutFilePath = data.getData();
                setImg(mOutFilePath);

                mSelectNote.setPathImg(mOutFilePath.toString());
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                mSelectNote = data.getParcelableExtra(CreateNoteActivity.INTENT_EXTRA_EDIT_NOTE);

                if (mSelectNote != null) {
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle(mSelectNote.getNameNote());

                    mTxtContent.setText(mSelectNote.getContent());
                    setImg(Uri.parse(mSelectNote.getPathImg().get(mSelectNote.getPathImg().size() - 1)));
                }
            }

            helper.updateNote(mSelectNote);
        }
    }

    @Override
    public void onClick(DialogFragment dialogFragment, int position) {
        switch (position) {
            case 0:
                if (ActivityCompat.checkSelfPermission(DetailedActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DetailedActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    try {
                        mOutFilePath = ImgUtils.cameraRequest(DetailedActivity.this, CAMERA_REQUEST, mSavePath);
                    } catch (IOException e) {
                        mOutFilePath = null;
                        Snackbar.make(mLayout, getString(R.string.str_problems_message), Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(DetailedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailedActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    ImgUtils.galleryRequest(DetailedActivity.this, GALLERY_REQUEST);
                }
                break;
        }
        dialogFragment.dismiss();
    }

    @Override
    public void onDeleteImage(DialogFragment dialog, int position) {
        if (position == 0) {
            String date = DateUtils.getDate();
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());

            mImageView.setImageBitmap(null);
            helper.deleteImage(mWhere[0]);

            mSelectNote.setPathImg(DELETE_IMG);
            mSelectNote.setDateLastUpdateNote(date);
            helper.updateNote(mSelectNote);

            dialog.dismiss();
        }
    }
}