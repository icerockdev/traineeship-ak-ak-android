package ru.Artem.meganotes.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;
import ru.Artem.meganotes.app.pojo.HelpClass;
import ru.Artem.meganotes.app.R;

/**
 * Created by Артем on 14.04.2016.
 */
public class HomeFragment extends BaseNoteFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesList = mDataBaseHelper.getNotes(new String[] {String.valueOf(1)});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
