package com.sharkozp.galleryapplication;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.sharkozp.galleryapplication.system.AsyncResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleksandr on 12/26/15.
 * For more info contact us <a href="mailto:fintegro@gmail.com">Fintegro Inc.</a>
 */
public class GalleryActivity extends AppCompatActivity {
    private static List<String> imagesPath = null;

    public void loadImages(AsyncResponse delegate) {
        LoadImagesTask loadImagesTask = new LoadImagesTask(delegate);
        loadImagesTask.execute();
    }

    /**
     * Task for loading all images from devices in background
     */
    private class LoadImagesTask extends AsyncTask<Void, Void, List<String>> {
        public AsyncResponse delegate;

        public LoadImagesTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            //Stores all the images from the gallery in Cursor
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            //Total number of images
            //Create an array to store path to all the images
            List<String> imagesPath = new ArrayList<>();
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
        protected void onPostExecute(List<String> images) {
            super.onPostExecute(images);
            delegate.processFinish(images);
            imagesPath = images;
        }
    }

    public List<String> getImagesPath() {
        return imagesPath;
    }
}
