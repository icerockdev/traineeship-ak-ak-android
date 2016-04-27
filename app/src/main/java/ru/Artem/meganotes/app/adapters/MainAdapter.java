package ru.Artem.meganotes.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.Artem.meganotes.app.activity.DetailedActivity;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Артем on 07.04.2016.
 */


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.NoteViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private OnLongItemClickListener mOnLongItemClickListener;
    private List<ModelNote> mNotesList;
    private Context mContext;

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private TextView nameNote;
        private TextView lastUpdateNote;
        private ImageView imgNote;

        public NoteViewHolder(View itemView) {

            super(itemView);

            nameNote = (TextView)itemView.findViewById(R.id.nameNote);
            lastUpdateNote = (TextView)itemView.findViewById(R.id.lastUpdateNote);
            imgNote = (ImageView)itemView.findViewById(R.id.imgNote);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(mOnItemClickListener != null) {
                mOnItemClickListener.OnItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(mOnLongItemClickListener != null) {
                mOnLongItemClickListener.onLongItemClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public MainAdapter(List<ModelNote> modelNotes, Context mainActivityContext){
        this.mNotesList = modelNotes;
        this.mContext = mainActivityContext;
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void SetOnItemLongClickListener(final OnLongItemClickListener onLongItemClickListener) {
        this.mOnLongItemClickListener = onLongItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new NoteViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, int i) {

        noteViewHolder.nameNote.setText(mNotesList.get(i).getNameNote());
        noteViewHolder.lastUpdateNote.setText(mNotesList.get(i).getLastUpdateNote());

        if(!mNotesList.get(i).getPathImg().isEmpty() && !mNotesList.get(i).getPathImg().equals(DetailedActivity.DELETE_IMG)) {
            fetchDrawableOnThread(noteViewHolder.imgNote, i);
        } else if (mNotesList.get(i).getPathImg().equals(DetailedActivity.DELETE_IMG)) { // плохое решение, спросить!
            noteViewHolder.imgNote.setImageDrawable(null);
        }
    }

    public void fetchDrawableOnThread (final ImageView imageView, final int i) {

        if (mNotesList.get(i).getBitmap() != null) {
            imageView.setImageBitmap(mNotesList.get(i).getBitmap());
        }

        final android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {

                final Bitmap image = (Bitmap) msg.obj;
                imageView.setImageBitmap(image);

            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mNotesList.get(i).setBitmap(scaleImg(i));
                    final Message message = handler.obtainMessage(1, mNotesList.get(i).getBitmap());
                    handler.sendMessage(message);
                } catch (IOException ex) {

                }
            }
        });
        thread.start();
    }

    public Bitmap scaleImg(int position) throws IOException {

        InputStream inputStream;
        InputStream inputStreamScale;
        inputStream = mContext.getContentResolver()
                .openInputStream(Uri.parse(mNotesList.get(position).getPathImg()));

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions);

        onlyBoundsOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, 128, 90);
        onlyBoundsOptions.inJustDecodeBounds = false;

        inputStreamScale = mContext.getContentResolver()
                .openInputStream(Uri.parse(mNotesList.get(position).getPathImg()));

        return BitmapFactory.decodeStream(inputStreamScale, null, onlyBoundsOptions);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        int tmp;

        if (width > height){
            tmp = height;
            height = width;
            width = tmp;
        }

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}