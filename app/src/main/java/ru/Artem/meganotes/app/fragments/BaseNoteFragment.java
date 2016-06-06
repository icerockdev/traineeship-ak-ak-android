package ru.Artem.meganotes.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.TextView;
import ru.Artem.meganotes.app.activity.CreateNoteActivity;
import ru.Artem.meganotes.app.activity.DetailedActivity;
import ru.Artem.meganotes.app.adapters.MainAdapter;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteNoteDialog;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.utils.RecyclerViewUtils;

import java.util.List;

public class BaseNoteFragment extends Fragment implements DeleteNoteDialog.OnInteractionFragment {

    private List<ModelNote> mNotesList;
    private MainAdapter mAdapter;
    private FloatingActionButton mCreateNoteFAB;
    private ModelNote mDeleteNote;

    private final String LOG_TAG = BaseNoteFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity().getApplicationContext());

        mNotesList = dataBaseHelper.getNotes();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MainAdapter(mNotesList);

        mCreateNoteFAB = (FloatingActionButton)  rootView.findViewById(R.id.createNote);
        RecyclerViewUtils.initRecyclerView(new LinearLayoutManager(getActivity()),
                (RecyclerView) rootView.findViewById(R.id.recyclerView), mAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCreateNoteFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                startActivityForResult(intent, CreateNoteActivity.CREATE_NOTE_REQUEST);
            }
        });

        mAdapter.SetOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DetailedActivity.class);

                mNotesList.get(position).setPositionInAdapter(position);

                intent.putExtra(DetailedActivity.EDIT_NOTE_KEY, mNotesList.get(position));
                startActivityForResult(intent, DetailedActivity.EDIT_NOTE_REQUEST);
            }
        });

        mAdapter.SetOnItemLongClickListener(new MainAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                TextView nameNote = (TextView) view.findViewById(R.id.nameNote);
                DeleteNoteDialog deleteNoteDialog = DeleteNoteDialog.newInstance(nameNote.getText().toString());

                mDeleteNote = mNotesList.get(position);
                mDeleteNote.setPositionInAdapter(position);

                deleteNoteDialog.setTargetFragment(BaseNoteFragment.this, 1);
                deleteNoteDialog.show(getFragmentManager().beginTransaction(), AddImageDialog.DIALOG_KEY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case DetailedActivity.EDIT_NOTE_REQUEST:
                    ModelNote editNote = data.getParcelableExtra(DetailedActivity.EDIT_NOTE_KEY);

                    mNotesList.set(editNote.getPositionInAdapter(), editNote);
                    mAdapter.notifyItemChanged(editNote.getPositionInAdapter(), editNote);

                    break;
                case CreateNoteActivity.CREATE_NOTE_REQUEST:
                    ModelNote createNote = data.getParcelableExtra(CreateNoteActivity.CREATE_NOTE_KEY);

                    mNotesList.add(createNote);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);

                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_notes) {
            mAdapter.notifyItemRangeRemoved(0, mNotesList.size());
            mNotesList.clear();

            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity().getApplicationContext());
            dataBaseHelper.deleteAll();

            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        mCreateNoteFAB = null;

        super.onDestroyView();
    }

    @Override
    public void callBack(DialogInterface dialog, int which) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());

        if (mDeleteNote != null) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                dataBaseHelper.onDeleteSelectedNote(new String[]
                        {String.valueOf(mDeleteNote.getId())});

                mNotesList.remove(mDeleteNote.getPositionInAdapter());
                mAdapter.notifyItemRemoved(mDeleteNote.getPositionInAdapter());
            }
        }
    }
}