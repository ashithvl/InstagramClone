package com.blueangles.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.blueangles.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Ashith VL on 10/13/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imageUrls;

    public GridImageAdapter(Context mContext, int layoutResource, String mAppend, ArrayList<String> imageUrls) {
        super(mContext, layoutResource, imageUrls);
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imageUrls = imageUrls;
    }

    private static class GridViewHolder {
        SquareImageView gridProfileImageView;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final GridViewHolder gridViewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            gridViewHolder = new GridViewHolder();
            gridViewHolder.mProgressBar =  convertView.findViewById(R.id.grid_progress_bar);
            gridViewHolder.gridProfileImageView = convertView.findViewById(R.id.profile_grid_image);

            convertView.setTag(gridViewHolder);
        } else {
            gridViewHolder = (GridViewHolder) convertView.getTag();
        }

        String imageUrl = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(mAppend + imageUrl, gridViewHolder.gridProfileImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (gridViewHolder.mProgressBar != null) {
                    gridViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (gridViewHolder.mProgressBar != null) {
                    gridViewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (gridViewHolder.mProgressBar != null) {
                    gridViewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (gridViewHolder.mProgressBar != null) {
                    gridViewHolder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
}
