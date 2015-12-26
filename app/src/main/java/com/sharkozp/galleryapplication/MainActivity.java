package com.sharkozp.galleryapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sharkozp.galleryapplication.fragments.ImagesFragment;
import com.sharkozp.galleryapplication.fragments.ItemFragment;
import com.sharkozp.galleryapplication.fragments.MainFragment;
import com.sharkozp.galleryapplication.system.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment fragment = ImagesFragment.newInstance();
        //if state not saved open ImagesFragment, else will open current Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, Constants.IMAGES_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if Up button pressed
            case android.R.id.home:
                upButtonAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        upButtonAction();
    }

    /**
     * If opened ItemFragment return to ImagesFragment
     */
    private void upButtonAction() {
        Fragment currentFragment = getFragmentManager().findFragmentByTag(Constants.ITEM_FRAGMENT_TAG);
        //return from ItemFragment to ImagesFragment
        if (currentFragment instanceof ItemFragment) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, ImagesFragment.newInstance()).commit();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }
}
