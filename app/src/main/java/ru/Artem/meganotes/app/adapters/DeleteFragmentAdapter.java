package ru.Artem.meganotes.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.R;

import java.util.List;

/**
 * Created by Артем on 16.04.2016.
 */
public class DeleteFragmentAdapter extends RecyclerView.Adapter<DeleteFragmentAdapter.DeleteNoteViewHolder> {

    private List<ModelNote> mListNote;
    private MainAdapter mAdapter;

    public class DeleteNoteViewHolder extends RecyclerView.ViewHolder {

        private TextView lastUpdateNoteDel;
        private ImageView imgNoteDel;
        private TextView nameNoteDel;

        public DeleteNoteViewHolder(View itemView) {
            super(itemView);
            nameNoteDel = (TextView) itemView.findViewById(R.id.nameNoteDel);
            lastUpdateNoteDel = (TextView) itemView.findViewById(R.id.lastUpdateNoteDel);
            imgNoteDel = (ImageView) itemView.findViewById(R.id.imgNoteDel);
        }
    }

    public DeleteFragmentAdapter(List<ModelNote> modelNotes, Context mainActivityContext) {
        this.mListNote = modelNotes;
        this.mAdapter = new MainAdapter(modelNotes, mainActivityContext);
    }

    @Override
    public DeleteNoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_delete, viewGroup, false);

        return new DeleteNoteViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(final DeleteNoteViewHolder holder, final int position) {
        holder.lastUpdateNoteDel.setText(mListNote.get(position).getLastUpdateNote());
        holder.nameNoteDel.setText(mListNote.get(position).getNameNote());
        if(!mListNote.get(position).getPathImg().isEmpty()) {
            mAdapter.fetchDrawableOnThread(holder.imgNoteDel, position);
        }
    }

    @Override
    public int getItemCount() {
        return mListNote.size();
    }
}
