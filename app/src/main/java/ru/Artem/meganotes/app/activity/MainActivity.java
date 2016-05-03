package ru.Artem.meganotes.app.activity;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import ru.Artem.meganotes.app.fragments.*;
import ru.Artem.meganotes.app.R;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

       if (savedInstanceState == null) {
           BaseNoteFragment baseNoteFragment = new BaseNoteFragment();
           setFragment(baseNoteFragment);
       }
    }

    private void setFragment(Fragment fragment) {
       getSupportFragmentManager()
               .beginTransaction()
               .replace(R.id.content_frame, fragment)
               .commit();
    }
}
