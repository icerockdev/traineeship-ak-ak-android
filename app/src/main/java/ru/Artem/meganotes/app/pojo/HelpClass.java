package ru.Artem.meganotes.app.pojo;

import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
/**
 * Created by Артем on 14.04.2016.
 */
public class HelpClass {

    public File createImageFile() throws IOException {
        String timeStamp = DateUtils.getDateCreateFile();

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName + ".jpg");
    }

    public RecyclerView initRecyclerView(LinearLayoutManager linearLayoutManager, RecyclerView recyclerView,
                                         RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return recyclerView;
    }
}