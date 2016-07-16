package ru.Artem.meganotes.app.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 22.06.2016.
 */
public class DialogSelectSort extends DialogFragment {

    private static final String LOG_TAG = DialogSelectSort.class.getName();
    public static final String DIALOG_KEY = DialogSelectSort.class.getName();
    private static String KEY_SORT_BY_DATE = "key_date_sort";
    private OnClickListener mListener;
    private boolean mASC;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnClickListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " должен реализовывать интерфейс OnClickListener");
        }
    }

    public static DialogSelectSort newInstance(boolean sortByDate) {
        DialogSelectSort dialogSelectSort = new DialogSelectSort();

        Bundle args = new Bundle();
        args.putBoolean(KEY_SORT_BY_DATE, sortByDate);
        dialogSelectSort.setArguments(args);

        return dialogSelectSort;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.sort_dialog, container, false);

        Button buttonPositive = (Button) dialogView.findViewById(R.id.button_positive);
        Button buttonNegative = (Button) dialogView.findViewById(R.id.button_negative);
        RadioGroup radioGroupDate = (RadioGroup) dialogView.findViewById(R.id.radioGroupDate);
        RadioGroup radioGroupName = (RadioGroup) dialogView.findViewById(R.id.radioGroupName);
        RelativeLayout relativeLayout = (RelativeLayout) dialogView.findViewById(R.id.layout_button);

        radioGroupDate.setOnCheckedChangeListener(mChangeListener);
        radioGroupName.setOnCheckedChangeListener(mChangeListener);

        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonFromDialog(DialogSelectSort.this, DialogInterface.BUTTON_NEGATIVE, mASC);
            }
        });

        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickButtonFromDialog(DialogSelectSort.this, DialogInterface.BUTTON_POSITIVE, mASC);
            }
        });

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();

        if (getArguments().getBoolean(KEY_SORT_BY_DATE)) {
            radioGroupDate.setVisibility(View.VISIBLE);
            radioGroupName.setVisibility(View.INVISIBLE);
            layoutParams.addRule(RelativeLayout.BELOW, radioGroupDate.getId());
        } else {
            radioGroupDate.setVisibility(View.INVISIBLE);
            radioGroupName.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.BELOW, radioGroupName.getId());
        }

        relativeLayout.setLayoutParams(layoutParams);

        return dialogView;
    }

    private RadioGroup.OnCheckedChangeListener mChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mASC = checkedId == R.id.radioButtonDESCDate || checkedId == R.id.radioButtonASCName;
        }
    };

    public interface OnClickListener {
        void onClickButtonFromDialog(DialogSelectSort dialogSelectSort, int which, boolean direction);
    }
}
