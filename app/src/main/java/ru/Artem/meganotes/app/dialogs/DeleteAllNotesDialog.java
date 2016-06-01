package ru.Artem.meganotes.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 26.04.2016.
 */
public class DeleteAllNotesDialog extends DialogFragment {

    private DialogInterface.OnClickListener mOnClickListener;
    public static final String DIALOG_KEY = "dialogDeleteAllNotes";

    public void setOnClickListener(DialogInterface.OnClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        if (mOnClickListener == null)
            mOnClickListener = (DialogInterface.OnClickListener) getTargetFragment();
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.buttonDel, mOnClickListener)
                .setNegativeButton(R.string.buttonCancel, mOnClickListener)
                .setMessage(getString(R.string.messageDeleteAllNotesDialog))
                .create();
    }
}
