package ru.Artem.meganotes.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;


import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.CustomImageMaker;
import ru.Artem.meganotes.app.utils.GridLayoutUtils;

import java.util.List;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity implements CustomImageMaker.OnDeleteImageListener {

    private TextView mTxtContent;
    private GridLayout mLayoutForImages;
    private RelativeLayout lastDeletedElement;
    private int mColumnCount = 2;

    private Note mSelectNote;

    private final int EDIT_NOTE_REQUEST = 1002;
    private int mImageWidth;
    private int mTempIdForImages;
    public final static String INTENT_EXTRA_OPEN_NOTE = "noteOpen";

    private final String LOG_TAG = DetailedActivity.class.getName();
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed);

        mSelectNote = getIntent().getParcelableExtra(INTENT_EXTRA_OPEN_NOTE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailed);
        mTxtContent = (TextView) findViewById(R.id.txtContent);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mSelectNote.getNameNote());
        }

        mTxtContent = (TextView) findViewById(R.id.txtContent);

        mLayoutForImages = (GridLayout) findViewById(R.id.detailedLayout);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) mColumnCount = 3;

        mLayoutForImages.setColumnCount(mColumnCount);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        mImageWidth = display.getWidth() / 2;
        mTempIdForImages = 0;

        mTxtContent.setText(mSelectNote.getContent());

        List<String> tempList = mSelectNote.getPathImg();

        if (!tempList.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "we have not empty List");
                for (String item : tempList) {
                    Log.d(LOG_TAG, "path: " + item);
                }
            }
            for(String image : tempList) {
                setImg(Uri.parse(image));
            }
        }
    }

    private void setImg(final Uri pathImg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    Log.d(LOG_TAG, "we in setIMg, and have in path is: " + pathImg.toString());
                }
                try {
                    final Message message = mHandler.obtainMessage(1,
                            CustomImageMaker.initCustomView(
                                    pathImg.toString(), false, mImageWidth,
                                    mTempIdForImages++, DetailedActivity.this)
                    );
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
            GridLayoutUtils.addViewToGrid(mLayoutForImages, (CustomImageMaker) msg.obj, mImageWidth, mColumnCount);
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

        if (resultCode == RESULT_OK && requestCode == EDIT_NOTE_REQUEST) {
            mSelectNote = data.getParcelableExtra(CreateNoteActivity.INTENT_EXTRA_EDIT_NOTE);

            if (mSelectNote != null) {
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(mSelectNote.getNameNote());

                mTxtContent.setText(mSelectNote.getContent());

                if (!mSelectNote.getPathImg().isEmpty()) {
                    setImg(Uri.parse(mSelectNote.getPathImg().get(mSelectNote.getPathImg().size() - 1)));
                }
            }
        }
    }

    @Override
    public void removeElementFromRootView(int id) {
        lastDeletedElement = (RelativeLayout) mLayoutForImages.getChildAt(id);
        mLayoutForImages.removeView(lastDeletedElement);
        mTempIdForImages--;

        int imagesCount = mLayoutForImages.getChildCount();
        for (int i = 0; i < imagesCount; i++) {
            CustomImageMaker customImageMaker = (CustomImageMaker) mLayoutForImages.getChildAt(i);
            customImageMaker.setIndex(i);
        }
    }

    @Override
    public void returnLastDeletedElement() {
            mLayoutForImages.addView(lastDeletedElement);
            mTempIdForImages++;
            int imagesCount = mLayoutForImages.getChildCount();
            for (int i = 0; i < imagesCount; i++) {
                CustomImageMaker customImageMaker = (CustomImageMaker) mLayoutForImages.getChildAt(i);
                customImageMaker.setIndex(i);
            }
    }
}
