package com.sharkozp.galleryapplication.system;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sharkozp.galleryapplication.MainActivity;
import com.sharkozp.galleryapplication.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by oleksandr on 12/26/15.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private final MainActivity activity;
    private LinkedList<String> imagesPath;
    private int width = 0;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageAdapter(Context context, LinkedList<String> imagesPath) {
        this.context = context;
        this.activity = (MainActivity) context;
        this.imagesPath = imagesPath;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        File imgFile = new File(imagesPath.get(position));

        ImageView view = holder.imageView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clicked(position);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog(position);
                return false;
            }
        });

        //set width half of screen
        int halfSize = getWidth() / 2;
        Picasso.with(context).load(imgFile).resize(halfSize, halfSize).centerCrop().into(view);
    }

    // Return the size of your imagesPath (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    /**
     * Get Current width in pixels. Also check width if we save it
     *
     * @return width in pixels
     */
    private int getWidth() {
        if (width == 0) {
            Activity activity = (Activity) context;
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            int sizeInPixel = activity.getResources().getDimensionPixelSize(R.dimen.card_margin);
            width += sizeInPixel * 2;
        }
        return width;
    }

    private void createDialog(final int position) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        //  builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.choose_action);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
        arrayAdapter.add(context.getString(R.string.delete_action));
        builderSingle.setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                        builderInner.setMessage(R.string.delete_dialog_text);
                        builderInner.setTitle(R.string.delete_dialog_title);
                        builderInner.setPositiveButton(
                                android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity activity = (MainActivity) context;
                                        activity.removeImage(position);
                                        dialog.dismiss();
                                    }
                                });

                        builderInner.setNegativeButton(
                                android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AppCompatDialog builderInnerAlert = builderInner.create();
                        builderInnerAlert.show();
                    }
                });
        AppCompatDialog builderSingleAlert = builderSingle.create();
        builderSingleAlert.show();
    }
}