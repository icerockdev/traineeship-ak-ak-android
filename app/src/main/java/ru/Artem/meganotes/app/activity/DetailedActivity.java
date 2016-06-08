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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements AddImageDialog.OnItemListClickListener,
        DeleteImageDialog.OnClickListenerDelete {

    private String[] mWhere;
    private ImageView mImageView;
    private TextView mTxtContent;

    private Uri mOutFilePath = null;
    private ModelNote mSelectNote;

    private final int EDIT_NOTE_REQUEST = 1002;
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;

    public final static String INTENT_EXTRA_OPEN_NOTE = "noteOpen";


    private final String LOG_TAG = DetailedActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed);

        mSelectNote = getIntent().getParcelableExtra(INTENT_EXTRA_OPEN_NOTE);
        mWhere = new String[] {String.valueOf(mSelectNote.getId())};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailed);

        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mSelectNote.getNameNote());
        }

        mTxtContent = (TextView) findViewById(R.id.txtContent);

        mTxtContent.setText(mSelectNote.getContent());

        mImageView = (ImageView) findViewById(R.id.imageNote);
        setImg(Uri.parse(mSelectNote.getPathImg()));

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
    }

    final Handler mHandler = new Handler() {
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
                    final Message message = mHandler.obtainMessage(1,
                            BitmapFactory.decodeStream(inputStream, null, null));

                    mHandler.sendMessage(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

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

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
                if (requestCode == GALLERY_REQUEST) mOutFilePath = data.getData();

                setImg(mOutFilePath);
                mSelectNote.setPathImg(mOutFilePath.toString());
                dataBaseHelper.editData(DataBaseHelper.IMG_PATH_COLUMN, mWhere,
                        mOutFilePath.toString(), DateUtils.getDate());
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                mSelectNote = data.getParcelableExtra(CreateNoteActivity.INTENT_EXTRA_EDIT_NOTE);

                if (mSelectNote != null && getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mSelectNote.getNameNote());
                    mTxtContent.setText(mSelectNote.getContent());
                }
            }
        }
    }

    @Override
    public void onClick(DialogFragment dialogFragment, int position) {
        switch (position) {
            case 0:
                if (ActivityCompat.checkSelfPermission(DetailedActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DetailedActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 0);

                } else {
                    mOutFilePath = ImgUtils.cameraRequest(DetailedActivity.this, CAMERA_REQUEST);
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
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());
            String date = DateUtils.getDate();

            mImageView.setImageBitmap(null);

            dataBaseHelper.editData(DataBaseHelper.IMG_PATH_COLUMN, mWhere, "null", date);
            mSelectNote.setPathImg("null");
            mSelectNote.setLastUpdateNote(date);

            dialog.dismiss();
        }
    }
}