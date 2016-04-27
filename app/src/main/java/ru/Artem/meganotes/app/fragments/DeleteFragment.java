package ru.Artem.meganotes.app.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import ru.Artem.meganotes.app.adapters.DeleteFragmentAdapter;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.dialogs.DeleteAllNotesDialog;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.pojo.HelpClass;
import ru.Artem.meganotes.app.R;

import java.util.List;

/**
 * Created by Артем on 16.04.2016.
 */
public class DeleteFragment extends Fragment {

    private List<ModelNote> mModelNotes;
    private HelpClass mHelpClass = new HelpClass();
    private DeleteFragmentAdapter mAdapter;
    private final String LOG_TAG = "myLogs";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());
        mModelNotes = dataBaseHelper.getNotes(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.doneDelete:
                final DeleteAllNotesDialog deleteAllNotesDialog = new DeleteAllNotesDialog();

                deleteAllNotesDialog.show(getFragmentManager(), DeleteAllNotesDialog.DIALOG_KEY);
                deleteAllNotesDialog.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                removeAll();
                                deleteAllNotesDialog.onDismiss(null);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                deleteAllNotesDialog.onDismiss(null);
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_delete, container, false);

        mAdapter = new DeleteFragmentAdapter(mModelNotes, getActivity().getApplicationContext());

        mHelpClass.initRecyclerView(new LinearLayoutManager(getActivity()),
                (RecyclerView) rootView.findViewById(R.id.recyclerViewDelete), mAdapter);

        return rootView;
    }

    public void removeAll() {
        int size = 0;
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());

        dataBaseHelper.deleteAll();

        if(!mModelNotes.isEmpty())
            size  = mModelNotes.size();
        mModelNotes.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }
}
