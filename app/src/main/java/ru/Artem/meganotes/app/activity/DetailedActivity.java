package ru.Artem.meganotes.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;

import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.utils.CustomImageMaker;
import ru.Artem.meganotes.app.utils.ImgUtils;
import ru.Artem.meganotes.app.utils.GridLayoutUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Артем on 13.04.2016.
 */

public class DetailedActivity extends AppCompatActivity {

    private String[] mWhere;
    private ImageView mImageView;
    private TextView mTxtContent;
    private GridLayout mLayoutForImages;
    private LinearLayout mLayout;

    private Uri mOutFilePath = null;
    private Note mSelectNote;

    private final int EDIT_NOTE_REQUEST = 1002;
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    private int mImageWidth;
    private int mTempIdForImages;
    private String mSavePath;
    public final static String INTENT_EXTRA_OPEN_NOTE = "noteOpen";

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

        final TextView textView = (TextView) findViewById(R.id.textView);
        final EditText titleEdit = (EditText) findViewById(R.id.editTitle);
        final EditText contentEdit = (EditText) findViewById(R.id.editContent); //вот тут странно, куда-то пропали либо id, либо айтемы, содержащие их.
        // не у меня, не у тебя этих айди нету в предыдущих версиях кода, я туплю откуда они могли взяться уже час почти.
        mLayoutForImages = (GridLayout) findViewById(R.id.detailedLayout);
        mLayout = (LinearLayout) findViewById(R.id.layout);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        mImageWidth = display.getWidth() / 2;
        mTempIdForImages = 0;

        textView.setText(mSelectNote.getDateLastUpdateNote());
        contentEdit.setText(mSelectNote.getContent());
        titleEdit.setText(mSelectNote.getNameNote());
        mTxtContent.setText(mSelectNote.getContent());

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
                    if (!mSelectNote.getPathImg().isEmpty()) {
                        setImg(Uri.parse(mSelectNote.getPathImg().get(mSelectNote.getPathImg().size() - 1)));
                    }
                }
            }

            helper.updateNote(mSelectNote);
        }
    }
}
