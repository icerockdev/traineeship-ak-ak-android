package ru.Artem.meganotes.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ru.Artem.meganotes.app.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Александр on 29.05.2016.
 */
public class CustomImageMaker extends RelativeLayout {

    private ImageView mImage;
    private TextView mText;
    private ImageButton mButton;
    private String mImagePath;
    private boolean mReadMode;
    private int mIndex;

    private OnDeleteImageListener onDeleteImageListener;
    private final String LOG_TAG = CustomImageMaker.class.getName();

    public CustomImageMaker(final Context context, final String text, final String imagePath, boolean mode, int width, int height, int index) {
        super(context);

        try {
            onDeleteImageListener = (OnDeleteImageListener) context;
        } catch (Exception e) {
            Log.d(LOG_TAG, "Класс должен реализовывать интерфейс" + OnDeleteImageListener.class.getName());
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_imageview_style, this);

        this.setLayoutParams(params);

        mImagePath = imagePath;
        mReadMode = mode;
        mIndex = index;
        mImage = (ImageView) findViewById(R.id.imageInCustomIV);
        mText = (TextView) findViewById(R.id.textInCustomIV);
        mButton = (ImageButton) findViewById(R.id.imageButtonInCustomIV);

        try {
            mImage.setImageBitmap(ImgUtils.scaleImg(Uri.parse(imagePath), context, width, height));
        } catch (IOException ex) {
            //TODO
        }

        if (mReadMode) {
            mButton.setImageResource(R.drawable.ic_info_white_24dp);
        } else {
            mButton.setImageResource(R.drawable.ic_delete);
        }

        mText.setText(text);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadMode) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    View viewDialog = inflater.inflate(R.layout.alert_dialog, null);
                    TextView textDialog = (TextView) viewDialog.findViewById(R.id.textInDialog);
                    ImageView imageDialog = (ImageView) viewDialog.findViewById(R.id.imageInDialog);

                    textDialog.setText(text);
                    imageDialog.setImageURI(Uri.parse(imagePath));

                    adb.setView(viewDialog);
                    adb.create();
                    adb.show();
                } else {
                    final View rootView = getRootView();
                    onDeleteImageListener.removeElementFromRootView(mIndex);
                    final Snackbar snackbar = Snackbar.make(rootView, R.string.imageDeleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onDeleteImageListener.returnLastDeletedElement();
                                    Snackbar.make(rootView, R.string.reUndo, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    snackbar.show();
                }
            }
        });
    }

    public interface OnDeleteImageListener {
        void removeElementFromRootView(int id);

        void returnLastDeletedElement();
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setTextForLabel(String text) {
        mText.setText(text);
    }

    public String getTextLabel() {
        return mText.getText().toString();
    }

    public static CustomImageMaker initCustomView(String path, boolean mode, int size, int id, Context context) throws IOException {
        String name = null;
                name = ImgUtils.getFileNameByUri(Uri.parse(path), context);
        return new CustomImageMaker(context, name, path, mode, size, size, id);
    }

    public void setImage(String path) {
        mImage.setImageURI(Uri.parse(path));
        mImagePath = path;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setObserveMode(boolean mode) {
        mReadMode = mode;
    }

    public boolean getObserveMode() {
        return mReadMode;
    }
}
