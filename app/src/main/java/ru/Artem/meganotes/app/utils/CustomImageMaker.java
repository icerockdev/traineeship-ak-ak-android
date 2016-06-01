package ru.Artem.meganotes.app.utils;

import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.Artem.meganotes.app.R;

/**
 * Created by Александр on 29.05.2016.
 */
public class CustomImageMaker extends RelativeLayout {

    private ImageView mImage;
    private TextView mText;
    private ImageButton mButton;
    private String mImagePath;
    private boolean mReadMode;

    private OnDeleteImageListener onDeleteImageListener;

    public CustomImageMaker(final Context context, String text, String imagePath, boolean mode, int width, int height, final int tempID) {
        super(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        this.setLayoutParams(params);
        mImagePath = imagePath;
        mReadMode = mode;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_imageview_style, this);
        mImage = (ImageView) findViewById(R.id.imageInCustomIV);
        mText = (TextView) findViewById(R.id.textInCustomIV);
        mImage.setImageURI(Uri.parse(mImagePath));
        mText.setText(text);
        mButton = (ImageButton) findViewById(R.id.imageButtonInCustomIV);
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
                    //TODO сообщить вызывающему коду что нужно удалить элемент из RelaTiveLayout
                    onDeleteImageListener.removeElementFromRootView(tempID);
                    final Snackbar snackbar = Snackbar
                            .make(getRootView(), "Изображение Удалено", Snackbar.LENGTH_LONG)
                            .setAction("Восстановить", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar.make(getRootView(),"Восстановим потом :)",Snackbar.LENGTH_SHORT).show();
                                    //TODO сообщить коду что нужно восстановить элемент в Relative
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
