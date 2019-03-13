package com.upkipp.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.MovieDataParser;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;

import java.util.ArrayList;
import java.util.Map;

public final class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {
    //singleton variables
//    private static final Object LOCK = new Object();
//    private static VideoAdapter sInstance;

    private ArrayList<Map<String, String>> mAdapterData;
    private final OnListItemClickListener mOnListItemClickListener;

    public VideoAdapter(OnListItemClickListener clickListener) {
        mOnListItemClickListener = clickListener;
        mAdapterData = new ArrayList<>();
    }

//    public static VideoAdapter getInstance(@Nullable OnListItemClickListener clickListener) {
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = new VideoAdapter(clickListener);
//            }
//        }
//        return sInstance;
//    }

    public interface OnListItemClickListener {
        void onVideoItemClick(int position);
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final ImageView videoImageView;

        private VideoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImageView = itemView.findViewById(R.id.videoImageView);

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
        String path = currentVideoData.get("path");

        String imagePath = MovieDataParser.createVideoImagePath(path);

        //load image with glide
        NetworkFunctions.loadImage(
                context,
                imagePath,
                adapterViewHolder.videoImageView);
    }

    @Override
    public int getItemCount() {
        if (mAdapterData == null) {
            return 0;
        } else {
            return mAdapterData.size();
        }
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

