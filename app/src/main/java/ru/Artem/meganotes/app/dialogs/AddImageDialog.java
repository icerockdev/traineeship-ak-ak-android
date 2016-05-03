package ru.Artem.meganotes.app.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import ru.Artem.meganotes.app.adapters.FragmentDialogAdapter;
import ru.Artem.meganotes.app.models.ModelTypeApp;
import ru.Artem.meganotes.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Артем on 09.04.2016.
 */

public class AddImageDialog extends android.support.v4.app.DialogFragment {

    public static final String DIALOG_KEY = "dialogAddImage";
    private FragmentDialogAdapter mFragmentDialogAdapter;
    private OnItemListClickListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnItemListClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " должен реализовывать интерфейс OnItemListClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentDialogAdapter = new FragmentDialogAdapter(getActivity().getApplicationContext(), initData());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.fragment_dialog, container, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.list);

        getDialog().setTitle(getString(R.string.add_image));

        listView.setAdapter(mFragmentDialogAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onClick(AddImageDialog.this, position);
            }
        });
        return dialogView;
    }

    private List<ModelTypeApp> initData() {
        List<ModelTypeApp> listApp;
        listApp = new ArrayList<>();
        listApp.add(new ModelTypeApp(getString(R.string.camera), GoogleMaterial.Icon.gmd_camera));
        listApp.add(new ModelTypeApp(getString(R.string.gallery), GoogleMaterial.Icon.gmd_photo));
        return listApp;
    }

    public interface OnItemListClickListener {
        void onClick(DialogFragment dialogFragment, int position);
    }
}
