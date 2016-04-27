package ru.Artem.meganotes.app.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 26.04.2016.
 */
public class DeleteImageDialog extends DialogFragment {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    public static final String DIALOG_KEY = "dialogDeleteImage";

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewDialog = inflater.inflate(R.layout.fragment_dialog, container, false);
        ListView listView = (ListView) viewDialog.findViewById(R.id.list);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[] {getString(R.string.item_del)});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(mOnItemClickListener);

        return viewDialog;
    }
}
