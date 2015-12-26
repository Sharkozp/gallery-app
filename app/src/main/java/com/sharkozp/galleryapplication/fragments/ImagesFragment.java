package com.sharkozp.galleryapplication.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sharkozp.galleryapplication.GalleryActivity;
import com.sharkozp.galleryapplication.R;
import com.sharkozp.galleryapplication.system.AsyncResponse;
import com.sharkozp.galleryapplication.system.Constants;
import com.sharkozp.galleryapplication.system.ImageAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends MainFragment implements AsyncResponse {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    public ImagesFragment() {
        // Required empty public constructor
    }

    public static ImagesFragment newInstance() {
        return new ImagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load all images
        GalleryActivity activity = (GalleryActivity) getActivity();
        activity.loadImages(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.imageRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return view;
    }

    @Override
    public void processFinish(List<String> images) {
        LoadImageAdapter adapterTask = new LoadImageAdapter();
        adapterTask.execute(images);
    }

    private class LoadImageAdapter extends AsyncTask<List<String>, Void, ImageAdapter> {

        @Override
        protected ImageAdapter doInBackground(List<String>... params) {
            return new ImageAdapter(getActivity(), params[0]);
        }

        @Override
        protected void onPostExecute(ImageAdapter imageAdapter) {
            super.onPostExecute(imageAdapter);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(imageAdapter);
            }
        }
    }

    /* TODO Fix saved recycler position
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(Constants.RECYCLER_STATE, state);
    }*/

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(Constants.RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }
}