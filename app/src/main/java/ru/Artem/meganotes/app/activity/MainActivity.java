package ru.Artem.meganotes.app.activity;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import ru.Artem.meganotes.app.fragments.*;
import ru.Artem.meganotes.app.pojo.ListDrawer;
import ru.Artem.meganotes.app.R;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;


public class MainActivity extends AppCompatActivity implements BaseNoteFragment.OnActionBarListener {

    private Drawer mDrawer;
    private Toolbar mToolbar;
    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        createDrawer();

       if (savedInstanceState != null) {
           Fragment fragment;
           fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
           setFragment(fragment);
        } else {
           BaseNoteFragment baseNoteFragment = new BaseNoteFragment();
           setFragment(baseNoteFragment);
       }
    }

    private void createDrawer() {
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_all).withIcon(GoogleMaterial.Icon.gmd_note),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(GoogleMaterial.Icon.gmd_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_work).withIcon(GoogleMaterial.Icon.gmd_work),
                        new SectionDrawerItem().withName(R.string.drawer_menu_info),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_info).withIcon(GoogleMaterial.Icon.gmd_info))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        Fragment fragment = null;

                        switch (i) {
                            case ListDrawer.mAllPos:
                                fragment = new BaseNoteFragment();
                                break;
                            case ListDrawer.mHomePos:
                                fragment = new HomeFragment();
                                break;
                            case ListDrawer.mWorkPos:
                                fragment = new WorkFragment();
                                break;
                            case ListDrawer.mAboutPos:
                                break;
                        }
                        setFragment(fragment);

                        mDrawer.closeDrawer();

                        return false;
                    }
                })
                .build();
    }




    @Override
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void setFragment(Fragment fragment) {
       getSupportFragmentManager()
               .beginTransaction()
               .replace(R.id.content_frame, fragment)
               .commit();
    }

    @Override
    public void onClickItemMenu() {
        setFragment(new DeleteFragment());
    }
}
