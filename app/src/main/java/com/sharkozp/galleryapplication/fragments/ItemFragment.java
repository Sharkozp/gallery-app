package com.sharkozp.galleryapplication.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
public class ItemFragment extends Fragment implements View.OnClickListener {
    private int imagePosition;
    private String imagePath;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ItemFragment.
     */
    public static ItemFragment newInstance(int position, String path) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECTED_ITEM, position);
        bundle.putString(Constants.SELECTED_PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePosition = getArguments().getInt(Constants.SELECTED_ITEM);
            imagePath = getArguments().getString(Constants.SELECTED_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        view.findViewById(R.id.deleteAction).setOnClickListener(this);
        ImageView itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
        File imgFile = new File(imagePath);
        Picasso.with(getActivity()).load(imgFile).into(itemImageView);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.SELECTED_ITEM, imagePosition);
        outState.putString(Constants.SELECTED_PATH, imagePath);
    }

    @Override
    public void onClick(View v) {
        MainActivity activity = (MainActivity) getActivity();
        activity.removeImage(imagePosition);
        activity.hideSlider();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, ImagesFragment.newInstance()).commit();
    }
}
