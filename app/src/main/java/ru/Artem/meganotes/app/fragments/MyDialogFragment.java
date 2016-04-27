package ru.Artem.meganotes.app.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import ru.Artem.meganotes.app.Adapters.FragmentDialogAdapter;
import ru.Artem.meganotes.app.Models.ModelTypeApp;
import ru.Artem.meganotes.app.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Артем on 09.04.2016.
 */

public class MyDialogFragment extends android.support.v4.app.DialogFragment implements AdapterView.OnItemClickListener{


    OnFragmentInteractionListener mCallback;
    CallBack callback;
    private ListView listView;
    private List<ModelTypeApp> listApp;
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_CAPTURE = 2;
    private Uri mOutFilePath = null;
    private String myKey = null;
    private FragmentDialogAdapter fragmentDialogAdapter;
    View myView;

    public MyDialogFragment() {
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFragmentInteractionListener {
        /** Called by HeadlinesFragment when a list item is selected */
        void getUriPath(String uri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle  = getArguments();
        if(bundle != null) {
            myKey = bundle.getString("dialogKey");
        }
        fragmentDialogAdapter = new FragmentDialogAdapter(getActivity().getApplicationContext(), initData());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_dialog, container, false);
        listView = (ListView) myView.findViewById(R.id.listApp);
        if (myKey.equals("camera/gallery"))
            getDialog().setTitle(getString(R.string.add_image));
        else
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        listView.setAdapter(fragmentDialogAdapter);
        listView.setOnItemClickListener(this);
        return myView;
    }

    private List<ModelTypeApp> initData() {
        listApp = new ArrayList<ModelTypeApp>();
        if (myKey.equals("del")) {
            listApp.add(new ModelTypeApp(getString(R.string.item_del), GoogleMaterial.Icon.gmd_delete));
        }
        else {
            listApp.add(new ModelTypeApp(getString(R.string.camera), GoogleMaterial.Icon.gmd_camera));
            listApp.add(new ModelTypeApp(getString(R.string.gallery), GoogleMaterial.Icon.gmd_photo));
        }
        return listApp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (myKey.equals("camera/gallery")) {
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            //Toast.makeText(this, "Не удалось создать файл изображения", Toast.LENGTH_LONG).show();
                        }
                        if (photoFile != null) {
                            mOutFilePath = Uri.fromFile(photoFile);
                            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutFilePath);
                            startActivityForResult(captureIntent, CAMERA_CAPTURE);
                        }
                    }
                }
                else {
                    callback = (CallBack) getFragmentManager().findFragmentById(R.id.content_frame);
                    if (callback != null) {
                        callback.del();
                        onDismiss(null);
                    }
                }
                break;
            case 1:
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mCallback.getUriPath(mOutFilePath.toString());
                    //Toast.makeText(getActivity().getApplicationContext(), "lalala", Toast.LENGTH_LONG).show();
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    mCallback.getUriPath(selectedImage.toString());
                }
                break;
        }

        onDismiss(null);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName + ".jpg");
    }

    public interface CallBack{
        void del();
    }

    public interface ShowDialog{
        void showDialog(MyDialogFragment myDialogFragment);
    }
}
