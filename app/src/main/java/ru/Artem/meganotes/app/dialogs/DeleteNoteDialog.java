package ru.Artem.meganotes.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
    private OnInteractionFragment mCallBack;

    public static DeleteNoteDialog newInstance(String titleNote) {
        DeleteNoteDialog deleteFragment = new DeleteNoteDialog();

        Log.d(LOG_TAG, titleNote);
        Bundle args = new Bundle();
        args.putString(ARGS_KEY, titleNote);
        deleteFragment.setArguments(args);

        return deleteFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (OnInteractionFragment) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnInteractionFragment");
        }
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
            if (mCallBack != null) mCallBack.callBack(dialog, which);
        }
    };

    public interface OnInteractionFragment {
        void callBack(DialogInterface dialog, int which);
    }
}