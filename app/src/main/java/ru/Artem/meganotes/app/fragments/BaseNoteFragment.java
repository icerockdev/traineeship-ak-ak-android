package ru.Artem.meganotes.app.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import ru.Artem.meganotes.app.activity.CreateNoteActivity;
import ru.Artem.meganotes.app.activity.DetailedActivity;
import ru.Artem.meganotes.app.adapters.MainAdapter;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteAllNotesDialog;
import ru.Artem.meganotes.app.dialogs.DeleteNoteDialog;
import ru.Artem.meganotes.app.models.Note;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.R;
import ru.Artem.meganotes.app.utils.RecyclerViewUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BaseNoteFragment extends Fragment implements DeleteNoteDialog.OnInteractionFragment,
        DeleteAllNotesDialog.InteractionWithFragment {

    private List<Note> mNotesList;
    private MainAdapter mAdapter;
    private FloatingActionButton mCreateNoteFAB;
    private Note mDeleteNote;
    private static final boolean DEBUG = true;

    private final String LOG_TAG = BaseNoteFragment.class.getName();

    private final int CREATE_NOTE_REQUEST = 1003;
    private final int OPEN_NOTE_REQUEST = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity().getApplicationContext());

        mNotesList = dataBaseHelper.getAllNotesWithoutImages();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MainAdapter(mNotesList);

        mCreateNoteFAB = (FloatingActionButton) rootView.findViewById(R.id.createNote);
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
                startActivityForResult(intent, CREATE_NOTE_REQUEST);
            }
        });

        mAdapter.SetOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DetailedActivity.class);
                DataBaseHelper helper = DataBaseHelper.getInstance(getActivity().getApplicationContext());

                if (mNotesList.get(position).getPathImg() == null)
                    mNotesList.get(position).setListPathImages(helper.getImagesOfItNote(mNotesList.get(position)));
                mNotesList.get(position).setPositionInAdapter(position);

                intent.putExtra(DetailedActivity.INTENT_EXTRA_OPEN_NOTE, mNotesList.get(position));
                startActivityForResult(intent, OPEN_NOTE_REQUEST);
            }
        });

        mAdapter.SetOnItemLongClickListener(new MainAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                TextView nameNote = (TextView) view.findViewById(R.id.nameNote);
                DeleteNoteDialog deleteNoteDialog = DeleteNoteDialog.newInstance(nameNote.getText().toString());

                mDeleteNote = mNotesList.get(position);
                mDeleteNote.setPositionInAdapter(position);

                deleteNoteDialog.show(getChildFragmentManager().beginTransaction(), AddImageDialog.DIALOG_KEY);
            }
        });

        if (DEBUG) {
            List<Note> tempList = mNotesList;
            Collections.sort(tempList, new Note.ComparatorForName(false)); // сортировка от Я до А
            for (Note obj : tempList) {
                Log.d(LOG_TAG, "Sort list(Я-А): " + obj.getNameNote());
            }

            Collections.sort(tempList, new Note.ComparatorForName(true)); // сортировка от А до Я
            for (Note obj : tempList) {
                Log.d(LOG_TAG, "Sort list(А-Я): " + obj.getNameNote());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case OPEN_NOTE_REQUEST:
                    final Note editNote = data.getParcelableExtra(DetailedActivity.INTENT_EXTRA_OPEN_NOTE);

                    if (editNote.isDeletedNote()) {
                        final DataBaseHelper helper = DataBaseHelper.getInstance(getActivity().getApplicationContext());
                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mNotesList.remove(editNote.getPositionInAdapter());
                                mAdapter.notifyItemRemoved(editNote.getPositionInAdapter());
                                helper.deleteSelectNote(editNote);
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.snack_bar_message_delete, Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.snack_bar_button_undo, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mNotesList.add(editNote.getPositionInAdapter(), editNote);
                                                    mAdapter.notifyItemInserted(editNote.getPositionInAdapter());
                                                    helper.addNote(editNote.getNameNote(), editNote.getContent(),
                                                            editNote.getDateLastUpdateNote(), editNote.getPathImg());
                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                                            .show();
                                }
                            }
                        }, 500);

                        editNote.setDeletedNote(false);
                    } else {
                        mNotesList.set(editNote.getPositionInAdapter(), editNote);
                        mAdapter.notifyItemChanged(editNote.getPositionInAdapter(), editNote);
                    }
                    break;
                case CREATE_NOTE_REQUEST:
                    Note createNote = data.getParcelableExtra(CreateNoteActivity.INTENT_RESULT_EXTRA_CREATE_NOTE);
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
        if (item.getItemId() == R.id.delete_all_notes && !mNotesList.isEmpty()) {
            new DeleteAllNotesDialog().show(getChildFragmentManager().beginTransaction(), DeleteAllNotesDialog.DIALOG_KEY);

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
                dataBaseHelper.deleteSelectNote(mDeleteNote);

                mNotesList.remove(mDeleteNote.getPositionInAdapter());
                mAdapter.notifyItemRemoved(mDeleteNote.getPositionInAdapter());
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mAdapter.notifyItemRangeRemoved(0, mNotesList.size());
            mNotesList.clear();

            DataBaseHelper.getInstance(getActivity().getApplicationContext()).deleteAllNotesAndImages();
        }
    }
}