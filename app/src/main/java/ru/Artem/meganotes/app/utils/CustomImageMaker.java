package ru.Artem.meganotes.app.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
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

    public CustomImageMaker(Context context, String text, String imagePath, boolean mode, int width, int height) {
        super(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        this.setLayoutParams(params);
        mImagePath = imagePath;
        mReadMode = mode;
        initElements(context);
        mImage.setImageURI(Uri.parse(mImagePath));
        mText.setText(text);
        if (mReadMode) {
            //TODO кнопка с изображенем инфо и соотв.функционалом
            mButton.setImageResource(R.drawable.ic_info_white_24dp);
        } else //если режим редактирования
        {
            //TODO кнопка с изображением удалить и соотв.функционалом
            mButton.setImageResource(R.drawable.ic_delete);
        }
    }

    private void initElements(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_imageview_style, this);
        mImage = (ImageView) findViewById(R.id.imageInCustomIV);
        mText = (TextView) findViewById(R.id.textInCustomIV);
        mButton = (ImageButton) findViewById(R.id.imageButtonInCustomIV);
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO реализацию обработчика сюда
                if (mReadMode) {
                    //Log.d("CreateNoteActivity", "we in click listener imageButton");
                    AlertDialog.Builder adb = new AlertDialog.Builder(context.getApplicationContext()); // сюда надо передать какой-то контекст для того что бы билдер знал где показываться
                    adb.setTitle(R.string.drawer_menu_info);
                    adb.setMessage("инфа о изображение");
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.setNeutralButton(context.getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case Dialog.BUTTON_NEUTRAL:
                                    break;
                            }
                        }
                    });
                    adb.create();
                    adb.show();
                } else {
                    Log.d("CreateNoteActivity", "we in click listener imageButton");
                    //TODO снекбар с кнопкой ундо, который сообщает что удалена картинка, но позволяет вернуть её обратно
                }
            }
        });
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
