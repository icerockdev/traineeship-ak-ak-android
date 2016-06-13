package ru.Artem.meganotes.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 26.04.2016.
 */
public class DeleteAllNotesDialog extends DialogFragment {

    public static final String DIALOG_KEY = "dialogDeleteAllNotes";
    private InteractionWithFragment mCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (InteractionWithFragment) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс InteractionWithFragment");
        }
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mCallBack != null)
                mCallBack.onClick(dialog, which);
        }
    };

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

    public interface InteractionWithFragment {
        void onClick(DialogInterface dialogInterface, int which);
    }
}
