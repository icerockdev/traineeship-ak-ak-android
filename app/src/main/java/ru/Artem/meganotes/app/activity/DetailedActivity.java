package ru.Artem.meganotes.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.CustomImageMaker;
import ru.Artem.meganotes.app.utils.DateUtils;
import ru.Artem.meganotes.app.utils.ImgUtils;
import ru.Artem.meganotes.app.utils.GridLayoutUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements EditText.OnEditorActionListener,
        AddImageDialog.OnItemListClickListener {

    private String[] mWhere;
    private LinearLayout mLayout;
    private GridLayout mLayoutForImages;

    private Uri mOutFilePath = null;
    private Note mSelectNote;

    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    private int mImageWidth;
    private int mTempIdForImages;
    public static final String DELETE_IMG = "null";
    private String mSavePath;
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
        mWhere = new String[]{String.valueOf(mSelectNote.getId())};

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
        mLayoutForImages = (GridLayout) findViewById(R.id.detailedLayout);
        mLayout = (LinearLayout) findViewById(R.id.layout);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        mImageWidth = display.getWidth() / 2;
        mTempIdForImages = 0;

        textView.setText(mSelectNote.getDateLastUpdateNote());
        contentEdit.setText(mSelectNote.getContent());
        titleEdit.setText(mSelectNote.getNameNote());

        List<String> tempList = mSelectNote.getPathImg();
        if (!tempList.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "we have not empty List");
                for (String item : tempList) {
                    Log.d(LOG_TAG, "path: " + item);
                }
            }
            for(int i=0; i<tempList.size();i++) {
                setImg(Uri.parse(tempList.get(i)));
            }
        }

        titleEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String date = DateUtils.getDate();

                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                mSelectNote.setNameNote(v.getText().toString());
                mSelectNote.setDateLastUpdateNote(date);
                DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
                helper.updateNote(mSelectNote);
                return true;
            }
        });

        contentEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String date = DateUtils.getDate();

                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                mSelectNote.setDateLastUpdateNote(date);
                mSelectNote.setContent(v.getText().toString());
                DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
                helper.updateNote(mSelectNote);
                return true;
            }
        });
        mSavePath = this.getFilesDir().toString();
    }

    private void setImg(final Uri pathImg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    Log.d(LOG_TAG, "we in setIMg, and have in path is: " + pathImg.toString());
                }
                try {
                    String name = ImgUtils.getFileNameByUri(pathImg, getBaseContext());
                    CustomImageMaker image = new CustomImageMaker(DetailedActivity.this,
                            name,
                            pathImg.toString(),
                            true,
                            mImageWidth,
                            mImageWidth,
                            mTempIdForImages);
                    final Message message = mHandler.obtainMessage(1, image);
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
            int columnCount = 2;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) columnCount = 3;
            GridLayoutUtils.addViewToGrid(mLayoutForImages, (CustomImageMaker)msg.obj, mImageWidth, columnCount);
            mTempIdForImages++;
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
            DataBaseHelper helper = DataBaseHelper.getInstance(getApplicationContext());
            helper.updateNote(mSelectNote);
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
                    try {
                        mOutFilePath = ImgUtils.cameraRequest(DetailedActivity.this, CAMERA_REQUEST, getExternalFilesDir(null).getAbsolutePath());
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

}
