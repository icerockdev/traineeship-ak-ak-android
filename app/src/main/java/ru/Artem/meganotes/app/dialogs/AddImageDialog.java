package ru.Artem.meganotes.app.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private FragmentDialogAdapter mFragmentDialogAdapter;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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
        listView.setOnItemClickListener(mOnItemClickListener);
        return dialogView;
    }

    private List<ModelTypeApp> initData() {
        List<ModelTypeApp> listApp;
        listApp = new ArrayList<ModelTypeApp>();
        listApp.add(new ModelTypeApp(getString(R.string.camera), GoogleMaterial.Icon.gmd_camera));
        listApp.add(new ModelTypeApp(getString(R.string.gallery), GoogleMaterial.Icon.gmd_photo));
        return listApp;
    }
}
