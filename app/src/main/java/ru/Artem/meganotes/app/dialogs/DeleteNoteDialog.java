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
    private static final String LOG_TAG = DeleteNoteDialog.class.getName();

    public static DeleteNoteDialog newInstance(String titleNote) {
        DeleteNoteDialog deleteFragment = new DeleteNoteDialog();

        Log.d(LOG_TAG, titleNote);
        Bundle args = new Bundle();
        args.putString(ARGS_KEY, titleNote);
        deleteFragment.setArguments(args);

        return deleteFragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.buttonDel, mOnClick)
                .setNegativeButton(R.string.buttonCancel, mOnClick)
                .setMessage(getString(R.string.messageDeleteDialog) + " " + getArguments().getString(ARGS_KEY) + "?")
                .create();
    }

    private DialogInterface.OnClickListener mOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            OnInteractionFragment callBack = (OnInteractionFragment) getParentFragment();
            if (callBack != null)
                callBack.callBack(dialog, which);
        }
    };

    public interface OnInteractionFragment {
        void callBack(DialogInterface dialog, int which);
    }
}