package ru.Artem.meganotes.app.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import ru.Artem.meganotes.app.activity.CreateNoteActivity;
import ru.Artem.meganotes.app.activity.DetailedActivity;
import ru.Artem.meganotes.app.adapters.MainAdapter;
import ru.Artem.meganotes.app.dialogs.AddImageDialog;
import ru.Artem.meganotes.app.dialogs.DeleteNoteDialog;
import ru.Artem.meganotes.app.models.ModelNote;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.pojo.HelpClass;
import ru.Artem.meganotes.app.R;

import java.util.List;

public class BaseNoteFragment extends Fragment {

    protected List<ModelNote> mNotesList;
    protected DataBaseHelper mDataBaseHelper;
    private OnActionBarListener mActionBarListener;
    private MainAdapter mAdapter;
    private FloatingActionButton mCreateNoteFAB;

    public final static String EDIT_NOTE_KEY = "noteEdit";
    public final static String CREATE_NOTE_KEY = "noteCreate";
    protected final String LOG_TAG = "myLogs";
    public static final int EDIT_NOTE_REQUEST = 1000;
    public static final int CREATE_NOTE_REQUEST = 1001;

    private HelpClass mHelpClass = new HelpClass();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnActionBarListener) {
            mActionBarListener = (OnActionBarListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDataBaseHelper = DataBaseHelper.getInstance(getActivity());
        mNotesList = mDataBaseHelper.getNotes(null);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MainAdapter(mNotesList, getActivity());

        mCreateNoteFAB = (FloatingActionButton)  rootView.findViewById(R.id.createNote);
        mHelpClass.initRecyclerView(new LinearLayoutManager(getActivity()),
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

                mNotesList.get(position).setPositionInAdapter(position);

                intent.putExtra(EDIT_NOTE_KEY, mNotesList.get(position));
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });

        mAdapter.SetOnItemLongClickListener(new MainAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                final TextView nameNote = (TextView) view.findViewById(R.id.nameNote);

                final DeleteNoteDialog deleteNoteDialog = DeleteNoteDialog.newInstance(nameNote.getText().toString());

                deleteNoteDialog.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                dataBaseHelper.onDeleteSelectedNote(new String[]
                                        {String.valueOf(mNotesList.get(position).getId())});

                                mNotesList.remove(position);
                                mAdapter.notifyItemRemoved(position);
                                deleteNoteDialog.onDismiss(null);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                deleteNoteDialog.onDismiss(null);
                                break;
                        }
                    }
                });
                deleteNoteDialog.show(getFragmentManager().beginTransaction(), AddImageDialog.DIALOG_KEY);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.goToDeleteFrag:
                mActionBarListener.onClickItemMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case EDIT_NOTE_REQUEST:
                    ModelNote editNote = data.getParcelableExtra(EDIT_NOTE_KEY);

                    mNotesList.set(editNote.getPositionInAdapter(), editNote);
                    mAdapter.notifyItemChanged(editNote.getPositionInAdapter(), editNote);
                    break;
                case CREATE_NOTE_REQUEST:
                    ModelNote createNote = data.getParcelableExtra(CREATE_NOTE_KEY);

                    mNotesList.add(createNote);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1); //вопрос про добавление в разных фрагментах
                    break;
            }
        }
    }

    public interface OnActionBarListener {
        void onClickItemMenu();
    }

    @Override
    public void onDestroyView() {
        mCreateNoteFAB = null;
        mAdapter = null;
        mNotesList = null;
        super.onDestroyView();
    }
}