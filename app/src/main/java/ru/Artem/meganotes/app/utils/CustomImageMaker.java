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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.Artem.meganotes.app.R;

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

    public CustomImageMaker(final Context context, String text, String imagePath, boolean mode, int width, int height, int index) {
        super(context);

        try {
                onDeleteImageListener = (OnDeleteImageListener) context;
            }
        catch (Exception e)
        {
            Log.d(LOG_TAG, "Класс должен реализовывать интерфейс" +OnDeleteImageListener.class.getName());
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


        mText.setText(text);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadMode) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle(R.string.drawer_menu_info);
                    adb.setMessage("инфа о изображение");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setNeutralButton(context.getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Dialog.BUTTON_NEUTRAL:
                                    dialog.cancel();
                                    break;
                            }
                        }
                    });
                    adb.create();
                    adb.show();
                } else {
                    final View rootView = getRootView();
                    onDeleteImageListener.removeElementFromRootView(mIndex);
                    final Snackbar snackbar = Snackbar.make(rootView, "Изображение Удалено", Snackbar.LENGTH_LONG)
                            .setAction("Восстановить", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onDeleteImageListener.returnLastDeletedElement();
                                    Snackbar.make(rootView, "Восстановлено", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    snackbar.show();
                }
            }
        });
    }

    public interface OnDeleteImageListener
    {
        void removeElementFromRootView(int id);
        void returnLastDeletedElement();
    }

    public void setIndex(int index)
    {
        mIndex = index;
    }

    public void setTextForLabel(String text) {
        mText.setText(text);
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
