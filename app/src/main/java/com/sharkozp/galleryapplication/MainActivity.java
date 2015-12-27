package com.sharkozp.galleryapplication;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.sharkozp.galleryapplication.fragments.ImagesFragment;
import com.sharkozp.galleryapplication.fragments.ItemFragment;
import com.sharkozp.galleryapplication.system.AsyncResponse;
import com.sharkozp.galleryapplication.system.Constants;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private static LinkedList<String> imagesPath = null;
    private boolean isSlider = false;
    private boolean fromSave = false;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 500;
    private final Handler mHideHandler = new Handler();

    private final Runnable mShowPartRunnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private LinkedList<String> images;
    private int savedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadImages(this);
        //if state not saved open ImagesFragment, else will open current Fragment
        if (savedInstanceState != null) {
            isSlider = savedInstanceState.getBoolean("showSlider");
            savedPosition = savedInstanceState.getInt("item");
            fromSave = true;
        }
    }

    private void initPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.imagePager);

        //The pager adapter, which provides the pages to the view pager widget.
        ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new RotateUpTransformer());
        mPager.setAdapter(mPagerAdapter);
        showSlider();
        delayedHide(UI_ANIMATION_DELAY);
    }

    public void clicked(int position) {
        initPager();
        mPager.setCurrentItem(position);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void delayedShow(int delayMillis) {
        mHideHandler.postDelayed(mShowPartRunnable, delayMillis);
    }

    @Override
    public void processFinish(LinkedList<String> images) {
        this.images = images;
        if (isSlider) {
            initPager();
            mPager.setCurrentItem(savedPosition);
        } else {
            if (!fromSave) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.anim_in, R.anim.anim_out);
                fragmentTransaction.replace(R.id.fragment_container, ImagesFragment.newInstance(), Constants.ACTIVE_FRAGMENT_TAG).commit();
            }
        }
    }

    /**
     * A simple pager adapter that represents ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ItemFragment.newInstance(position, images.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSlider) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragment_container, ImagesFragment.newInstance(), Constants.ACTIVE_FRAGMENT_TAG).commit();
            hideSlider();
            delayedShow(UI_ANIMATION_DELAY);
        } else {
            super.onBackPressed();
        }
    }

    public void hideSlider() {
        mPager.setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        isSlider = false;
    }

    private void showSlider() {
        mPager.setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        isSlider = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showSlider", isSlider);
        if (mPager != null) {
            outState.putInt("item", mPager.getCurrentItem());
        }
    }

    /**
     * REmove image by position from list and from device
     *
     * @param position
     */
    public void removeImage(int position) {
        String path = imagesPath.remove(position);
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?", new String[]{path});

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imageRecyclerView);

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, imagesPath.size());
        recyclerView.invalidate();

        if (adapter.getItemCount() == 0) {
            findViewById(R.id.emptyItems).setVisibility(View.VISIBLE);
        }
    }

    public void loadImages(AsyncResponse delegate) {
        LoadImagesTask loadImagesTask = new LoadImagesTask(delegate);
        loadImagesTask.execute();
    }

    /**
     * Task for loading all images from devices in background
     */
    private class LoadImagesTask extends AsyncTask<Void, Void, LinkedList<String>> {
        public AsyncResponse delegate;

        public LoadImagesTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LinkedList<String> doInBackground(Void... params) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            //Stores all the images from the gallery in Cursor
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            //Create an array to store path to all the images
            LinkedList<String> imagesPath = new LinkedList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    //Store the path of the image
                    imagesPath.add(cursor.getString(dataColumnIndex));
                }
                cursor.close();
            }
            return imagesPath;
        }

        @Override
        protected void onPostExecute(LinkedList<String> images) {
            super.onPostExecute(images);
            delegate.processFinish(images);
            imagesPath = images;
        }
    }

    public LinkedList<String> getImagesPath() {
        return imagesPath;
    }
}