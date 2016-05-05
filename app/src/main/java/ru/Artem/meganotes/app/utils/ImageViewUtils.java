package ru.Artem.meganotes.app.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import ru.Artem.meganotes.app.R;

/**
 * Created by Александр on 05.05.2016.
 */
public class ImageViewUtils extends ImageView {

    private ImageView mImageView;
    private TextView mTextView;

    public ImageViewUtils(Context context, String imagePath, String fileName){
        super(context);

        mImageView = (ImageView)findViewById(R.id.imageInCustomIV);
        mTextView = (TextView)findViewById(R.id.textInCustomIV);

        //реализацию превращения.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater.inflate(R.layout.imageview_custom_style); понять куда вставить данную штуку
        //предполагается что сюда попадут исключительно валидные данные, поэтому нет try catch.
        mImageView.setImageURI(Uri.parse(imagePath));
        mTextView.setText(fileName);
    }
}
