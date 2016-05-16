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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import java.util.List;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements EditText.OnEditorActionListener,
        AddImageDialog.OnItemListClickListener, DeleteImageDialog.OnClickListenerDelete {

    private String[] mWhere;
    private ImageView mImageView;

    private Uri mOutFilePath = null;
    private ModelNote mSelectNote;

    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    private final int EDIT_NOTE_TABLE = 0;
    private final int EDIT_IMAGE_TABLE = 1;
    public static final String DELETE_IMG = "null";
    private String sSavePath;
    private final String LOG_TAG = DetailedActivity.class.getName();
    public final static String EDIT_NOTE_KEY = "noteEdit";
    public static final int EDIT_NOTE_REQUEST = 1000;

    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSelectNote = getIntent().getParcelableExtra(EDIT_NOTE_KEY);
        mWhere = new String[] {String.valueOf(mSelectNote.getId())};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailed);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mSelectNote.getNameNote());
        }

        final TextView textView = (TextView) findViewById(R.id.textView);
        final EditText titleEdit = (EditText) findViewById(R.id.editTitle);
        final EditText contentEdit = (EditText) findViewById(R.id.editContent);

        textView.setText(mSelectNote.getLastUpdateNote());
        contentEdit.setText(mSelectNote.getContent());
        titleEdit.setText(mSelectNote.getNameNote());

        mImageView = (ImageView) findViewById(R.id.imageNote);
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

        titleEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String date = DateUtils.getDate();

                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                mSelectNote.setNameNote(v.getText().toString());
                mSelectNote.setLastUpdateNote(date);

                DataBaseHelper.editData(DataBaseHelper.TITLE_NOTES_COLUMN, mWhere, v.getText().toString(), date, EDIT_NOTE_TABLE);
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

                DataBaseHelper.editData(DataBaseHelper.CONTENT_COLUMN, mWhere, v.getText().toString(), date, EDIT_NOTE_TABLE);
                return true;
            }
        });


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
        sSavePath =  this.getFilesDir().toString();
    }

    private void setImg(final Uri pathImg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream;
                if (DEBUG)
                {
                    Log.d(LOG_TAG,"we in setIMg, and have in path is: "+pathImg.toString());
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
            final Bitmap image = (Bitmap) msg.obj; // на данный момент этот хендлер вставляет в одно ImageView, в другой задаче сделаем интерфейс и тогда добавление множества кастомных imageView в LinearLayout.
            mImageView.setImageBitmap(image);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(EDIT_NOTE_KEY, mSelectNote);
        setResult(EDIT_NOTE_REQUEST, intent);

        super.finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                mOutFilePath = data.getData();
            }
            setImg(mOutFilePath);
            mSelectNote.setPathImg(mOutFilePath.toString());
            DataBaseHelper.editData(DataBaseHelper.IMAGE_SOURCE_COLUMN, mWhere, mOutFilePath.toString(), DateUtils.getDate(), EDIT_IMAGE_TABLE);
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
                    mOutFilePath = ImgUtils.cameraRequest(DetailedActivity.this, CAMERA_REQUEST, LOG_TAG,sSavePath);
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

            mImageView.setImageBitmap(null);

            DataBaseHelper.deleteImage(mWhere[0]);
            mSelectNote.setPathImg(DELETE_IMG);
            mSelectNote.setLastUpdateNote(date);

            dialog.dismiss();
        }
    }
}
