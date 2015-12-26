package com.sharkozp.galleryapplication;

import android.os.Bundle;

import com.sharkozp.galleryapplication.fragments.ImagesFragment;
import com.sharkozp.galleryapplication.system.Constants;

public class MainActivity extends GalleryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if state not saved open ImagesFragment, else will open current Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ImagesFragment.newInstance(), Constants.IMAGES_FRAGMENT_TAG).commit();
        }
    }
}
