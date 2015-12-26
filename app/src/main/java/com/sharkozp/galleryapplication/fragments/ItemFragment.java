package com.sharkozp.galleryapplication.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sharkozp.galleryapplication.MainActivity;
import com.sharkozp.galleryapplication.R;
import com.sharkozp.galleryapplication.system.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends MainFragment {
    private int itemId;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId Selected position of Image
     * @return A new instance of fragment ItemFragment.
     */
    public static ItemFragment newInstance(int itemId) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.SELECTED_ITEM, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getInt(Constants.SELECTED_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();

        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        ImageView itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
        File imgFile = new File(getImagesPath().get(itemId));
        Picasso.with(getActivity()).load(imgFile).into(itemImageView);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.SELECTED_ITEM, itemId);
    }
}
