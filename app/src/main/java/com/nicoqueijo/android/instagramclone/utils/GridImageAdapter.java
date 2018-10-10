package com.nicoqueijo.android.instagramclone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nicoqueijo.android.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar progressBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.progressBar = convertView.findViewById(R.id.gridImageProgressBar);
            viewHolder.image = convertView.findViewById(R.id.gridImageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }
}
