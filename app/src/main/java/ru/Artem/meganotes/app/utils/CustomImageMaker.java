package ru.Artem.meganotes.app.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
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

    public CustomImageMaker(Context context, String text, String imagePath, boolean mode) {
        super(context);

        mImagePath = imagePath;
        mReadMode = mode;
        initElements();
        mImage.setImageURI(Uri.parse(mImagePath));
        mText.setText(text);
        if (mReadMode)
        {
            //TODO кнопка с изображенем инфо и соотв.функционалом
            mButton.setImageResource(R.drawable.ic_info_white_24dp);
        }
        else //если режим редактирования
        {
            //TODO кнопка с изображением удалить и соотв.функционалом
            mButton.setImageResource(R.drawable.ic_delete);
        }
    }

    private void initElements()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_imageview_style, this);
        mImage = (ImageView) findViewById(R.id.imageInCustomIV);
        mText = (TextView) findViewById(R.id.textInCustomIV);
        mButton = (ImageButton) findViewById(R.id.imageButtonInCustomIV);
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO реализацию обработчика сюда
                if (mReadMode) {
                    //TODO алертдиалог с одной кнопкой ОК, показывающий инфу о изображение
                }
                else {
                    //TODO фрагментдиалог, спрашивающий желаю ли я удалить файл изображения
                }
            }
        });
    }

    public void setTextForLabel(String text)
    {
        mText.setText(text);
    }
    public void setImage(String path)
    {
        mImage.setImageURI(Uri.parse(path));
        mImagePath = path;
    }
    public String getImagePath()
    {
        return mImagePath;
    }
    public void setObserveMode(boolean mode)
    {
        mReadMode = mode;
    }
    public boolean getObserveMode()
    {
        return mReadMode;
    }
}
