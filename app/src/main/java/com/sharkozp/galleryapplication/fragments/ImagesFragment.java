package com.sharkozp.galleryapplication.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharkozp.galleryapplication.MainActivity;
import com.sharkozp.galleryapplication.R;
import com.sharkozp.galleryapplication.system.AsyncResponse;
import com.sharkozp.galleryapplication.system.Constants;
import com.sharkozp.galleryapplication.system.ImageAdapter;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AsyncResponse {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyItems;
    private MainActivity activity;

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
        activity = (MainActivity) getActivity();
        LinkedList<String> images = activity.getImagesPath();
        LoadImageAdapter adapterTask = new LoadImageAdapter();
        adapterTask.execute(images);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.imageRecyclerView);
        emptyItems = (TextView) view.findViewById(R.id.emptyItems);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(Constants.RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        MainActivity activity = (MainActivity) getActivity();
        activity.loadImages(this);
    }

    @Override
    public void processFinish(LinkedList<String> images) {
        LoadImageAdapter adapterTask = new LoadImageAdapter();
        adapterTask.execute(images);
    }

    private class LoadImageAdapter extends AsyncTask<LinkedList<String>, Void, ImageAdapter> {

        @Override
        protected ImageAdapter doInBackground(LinkedList<String>... params) {
            return new ImageAdapter(getActivity(), params[0]);
        }

        @Override
        protected void onPostExecute(ImageAdapter imageAdapter) {
            super.onPostExecute(imageAdapter);
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(imageAdapter);
                if (imageAdapter.getItemCount() == 0) {
                    emptyItems.setVisibility(View.VISIBLE);
                } else {
                    emptyItems.setVisibility(View.GONE);
                }
            }
        }
    }

    // TODO Fix saved recycler position
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(Constants.RECYCLER_STATE, state);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(Constants.RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }
}