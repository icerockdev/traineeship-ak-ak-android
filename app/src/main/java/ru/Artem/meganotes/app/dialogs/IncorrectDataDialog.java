package ru.Artem.meganotes.app.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 01.06.2016.
 */
public class IncorrectDataDialog extends DialogFragment {
    private static final String LOG_TAG = IncorrectDataDialog.class.getName();
    public static final String DIALOG_KEY = "incorrectDataDialog";

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Drawable iconDialog = new IconicsDrawable(getActivity())
                .icon(GoogleMaterial.Icon.gmd_info)
                .colorRes(R.color.material_primary_icon)
                .sizeDp(24);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.attention)
                .setIcon(iconDialog)
                .setPositiveButton(R.string.button_continue, mOnClick)
                .setNegativeButton(R.string.buttonCancel, mOnClick)
                .setMessage(getString(R.string.incorrect_data_dialog_message))
                .create();
    }

    private DialogInterface.OnClickListener mOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            OnInteractionActivity callBack = (OnInteractionActivity) getActivity();
            if (callBack != null)
                callBack.callBack(dialog, which);
        }
    };

    public interface OnInteractionActivity {
        void callBack(DialogInterface dialog, int which);
    }
}
