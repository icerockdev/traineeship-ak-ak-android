package ru.Artem.meganotes.app.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.Artem.meganotes.app.R;

/**
 * Created by Александр on 05.05.2016.
 */
public class ImageViewUtils extends ImageView {

    private ImageView mImageView;
    private TextView mTextView;
    private boolean mInflateFinish = false;
    private static final String LOG_TAG = ImageViewUtils.class.getName();


    public ImageViewUtils(Context context, ViewGroup viewGroup, String imagePath, String fileName){
        super(context);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.imageview_custom_style, viewGroup);

        mImageView = (ImageView)findViewById(R.id.imageInCustomIV);
        mTextView = (TextView)findViewById(R.id.textInCustomIV);

            Log.d(LOG_TAG,"we in inflateFinish?");
            if (mImageView == null) {
                Log.d(LOG_TAG, "mImageView have null");
            }
            if (mTextView == null) {
                Log.d(LOG_TAG, "mTextView have null");
            }
            //предполагается что сюда попадут исключительно валидные данные, поэтому не будет try catch.
            try {
                mImageView.setImageURI(Uri.parse(imagePath));
            } catch (Exception e) {
                Log.d(LOG_TAG, "we in catch image");
                //mImageView.setImageResource(R.drawable.android_games_mini);
            }

            try {
                mTextView.setText(fileName);
            } catch (Exception e) {
                Log.d(LOG_TAG, "we in catch text");
            }
    }
}
