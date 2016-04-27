package ru.Artem.meganotes.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 22.04.2016.
 */
public class DeleteNoteDialog extends DialogFragment {

    private static final String ARGS_KEY = "titleNote";
    private static final String LOG_TAG = "myLogs";
    private DialogInterface.OnClickListener mOnClickListener;

    public static DeleteNoteDialog newInstance(String titleNote) {
        DeleteNoteDialog deleteFragment = new DeleteNoteDialog();

        Log.d(LOG_TAG, titleNote);
        Bundle args = new Bundle();
        args.putString(ARGS_KEY, titleNote);
        deleteFragment.setArguments(args);

        return deleteFragment;
    }

    public void setOnClickListener(DialogInterface.OnClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        if (mOnClickListener == null)
            mOnClickListener = (DialogInterface.OnClickListener) getTargetFragment();
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.titleDeleteDialog)
                .setPositiveButton(R.string.buttonDel, mOnClickListener)
                .setNegativeButton(R.string.buttonCancel, mOnClickListener)
                .setMessage(getString(R.string.messageDeleteDialog) + " " + getArguments().getString(ARGS_KEY) + "?")
                .create();
    }
}