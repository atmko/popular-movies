/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.MovieDataParser;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;

import java.util.ArrayList;
import java.util.Map;

public final class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private final ArrayList<Map<String, String>> mAdapterData;
    private final OnListItemClickListener mOnListItemClickListener;

    public VideoAdapter(OnListItemClickListener clickListener) {
        mOnListItemClickListener = clickListener;
        mAdapterData = new ArrayList<>();
    }

    public interface OnListItemClickListener {
        void onVideoItemClick(int position);
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final ImageView videoImageView;
        final TextView videoTypeTextView;

        private VideoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImageView = itemView.findViewById(R.id.videoImageView);
            videoTypeTextView = itemView.findViewById(R.id.videoTypeTextView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnListItemClickListener.onVideoItemClick(position);
        }
    }

    @NonNull
    @Override
    public VideoAdapter.VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int resourceId = R.layout.videos;

        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder adapterViewHolder, int position) {
        Context context = adapterViewHolder.videoImageView.getContext();
        //get current videoData
        Map<String, String> currentVideoData = mAdapterData.get(position);
        String path = currentVideoData.get(ApiConstants.VIDEO_PATH_KEY);

        String imagePath = MovieDataParser.createVideoImagePath(path);

        //load image with glide
        NetworkFunctions.loadImage(
                context,
                imagePath,
                adapterViewHolder.videoImageView);

        adapterViewHolder.videoTypeTextView
                .setText(currentVideoData.get(ApiConstants.VIDEO_TYPE_KEY));
    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addAdapterData(ArrayList<Map<String, String>> movieDataList) {
        for(int count = 0 ; count<movieDataList.size(); count++) {
            Map<String, String> currentVideoData = movieDataList.get(count);
            mAdapterData.add(currentVideoData);
            notifyDataSetChanged();
        }
//        mAdapterData.addAll(movieDataList);
//        notifyDataSetChanged();
    }

    public Map<String, String> getVideoData(int index) {
        return mAdapterData.get(index);
    }

    //clears and updates adapterData
    public void clearData() {
        mAdapterData.clear();
        notifyDataSetChanged();
    }

}

