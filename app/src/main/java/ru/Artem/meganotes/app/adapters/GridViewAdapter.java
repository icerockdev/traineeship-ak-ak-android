package ru.Artem.meganotes.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.Artem.meganotes.app.R;

import java.util.List;

/**
 * Created by Артем on 04.06.2016.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mListImage;
    private int mResourceIdDrawable;
    private int mSizeView;

    public GridViewAdapter(Context context, List<String> listImage, int idRes, int sizeView) {
        this.mContext = context;
        this.mListImage = listImage;
        this.mResourceIdDrawable = idRes;
        this.mSizeView = sizeView;
    }

    static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ImageButton imageButton;
    }

    @Override
    public int getCount() {
        return mListImage.size();
    }

    @Override
    public String getItem(int position) {
        return mListImage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.custom_imageview_style, parent, false);

            holder = new ViewHolder();
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(mSizeView, mSizeView));
            holder.textView = (TextView) view.findViewById(R.id.textInCustomIV);
            holder.imageView = (ImageView) view.findViewById(R.id.imageInCustomIV);
            holder.imageButton = (ImageButton) view.findViewById(R.id.imageButtonInCustomIV);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imageView.setImageURI(Uri.parse(mListImage.get(position)));
        holder.imageButton.setImageResource(mResourceIdDrawable);
        holder.textView.setText("1234");

        return view;
    }
}
