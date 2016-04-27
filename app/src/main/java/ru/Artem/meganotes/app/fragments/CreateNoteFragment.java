package ru.Artem.meganotes.app.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import ru.Artem.meganotes.app.Activity.MainActivity;
import ru.Artem.meganotes.app.DataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Артем on 07.04.2016.
 */

public class CreateNoteFragment extends Fragment {

    private EditText titleNote;
    private EditText contentNote;
    protected Toolbar toolbar;
    protected View myView;
    private ImageView imageView;
    private static Spinner spinner;
    public CreateNoteFragment() {
    }

    public static void addData(String titleNote, String contentNote, String imgPath,
                               String createDateNote, String lastUpdateDate, int category,
                               Resources res, Context context) {
        try {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context.getApplicationContext());
            SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DataBaseHelper.TITLE_NOTES_COLUMN, titleNote);
            values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            values.put(DataBaseHelper.IMG_PATH_COLUMN, imgPath);
            values.put(DataBaseHelper.CREATE_DATE_COLUMN, createDateNote);
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);
            switch (category) {
                case 0:
                    values.put(DataBaseHelper.CATEGORY_COLUMN, res.getString(R.string.drawer_item_work));
                    break;
                case 1:
                    values.put(DataBaseHelper.CATEGORY_COLUMN, res.getString(R.string.drawer_item_home));
                    break;
            }
            sqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE, null, values);
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(), "Ошибка при добавлении в базу", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        spinner.setVisibility(View.INVISIBLE);
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_create, container, false);

        titleNote = (EditText) myView.findViewById(R.id.editTitleNote);
        contentNote = (EditText) myView.findViewById(R.id.editContentNote);
        imageView = (ImageView) myView.findViewById(R.id.imageView);
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("");
        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_fragment, menu);
        //menu.setGroupVisible(R.id.groupMainFragment, false);
    }

    public static String getDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        return simpleDateFormat.format(date);
    }

    public void setImg(String pathUri) {
        imageView = (ImageView) getView().findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(pathUri));
    }
}
