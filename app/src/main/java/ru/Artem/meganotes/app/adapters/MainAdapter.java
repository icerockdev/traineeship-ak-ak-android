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
import android.widget.RelativeLayout;
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

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private TextView mNameNote;
        private TextView mLastUpdateNote;
        private TextView mContentNote;
        public NoteViewHolder(View itemView) {

            super(itemView);

            mNameNote = (TextView) itemView.findViewById(R.id.nameNote);
            mLastUpdateNote = (TextView) itemView.findViewById(R.id.lastUpdateNote);
            mContentNote = (TextView) itemView.findViewById(R.id.content_note);

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

    public MainAdapter(List<ModelNote> modelNotes){
        this.mNotesList = modelNotes;
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
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) noteViewHolder.mContentNote.getLayoutParams();

        if (!mNotesList.get(i).getNameNote().isEmpty()) {
            noteViewHolder.mContentNote.setMaxLines(2);
            layoutParams.addRule(RelativeLayout.BELOW, noteViewHolder.mNameNote.getId());
        } else {
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            noteViewHolder.mContentNote.setMaxLines(3);
        }

        noteViewHolder.mContentNote.setLayoutParams(layoutParams);

        noteViewHolder.mNameNote.setText(mNotesList.get(i).getNameNote());
        noteViewHolder.mContentNote.setText(mNotesList.get(i).getContent());
        noteViewHolder.mLastUpdateNote.setText(mNotesList.get(i).getLastUpdateNote());
    }
}