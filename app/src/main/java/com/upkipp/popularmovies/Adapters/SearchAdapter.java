package com.upkipp.popularmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.upkipp.popularmovies.Models.MovieData;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.Utils.NetworkFunctions;
import com.upkipp.popularmovies.Utils.SearchPreferences;

import java.util.ArrayList;

public final class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder> {
    //singleton variables
    private static final Object LOCK = new Object();
    private static SearchAdapter sInstance;

    private static ArrayList<MovieData> mAdapterData;
    private final OnListItemClickListener mOnListItemClickListener;

    private SearchAdapter(OnListItemClickListener clickListener) {
        mOnListItemClickListener = clickListener;
        mAdapterData = new ArrayList<>();
    }

    public static SearchAdapter getInstance(@Nullable OnListItemClickListener clickListener) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new SearchAdapter(clickListener);
            }
        }
        return sInstance;
    }

    public interface OnListItemClickListener {
        void onItemClick(int position);
    }

    public class SearchAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final ImageView moviePosterImageView;

        private SearchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.moviePosterImageView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnListItemClickListener.onItemClick(position);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int resourceId = R.layout.search;

        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        return new SearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterViewHolder adapterViewHolder, int position) {
        Context context = adapterViewHolder.moviePosterImageView.getContext();
        //get current MovieData
        MovieData currentMovieData = mAdapterData.get(position);

        //load image with glide
        NetworkFunctions.loadImage(
                context,
                currentMovieData.getPosterPath(),
                adapterViewHolder.moviePosterImageView);
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

    public void addAdapterData(ArrayList<MovieData> movieDataList) {
        for(int count = 0 ; count<movieDataList.size(); count++) {
            MovieData currentMovieData = movieDataList.get(count);
            mAdapterData.add(currentMovieData);
            notifyDataSetChanged();
        }
//        mAdapterData.addAll(movieDataList);
//        notifyDataSetChanged();
        Log.i("INFO", "searchAdapter data updated");
    }

    public MovieData getMovieData(int index) {
        return mAdapterData.get(index);
    }

    //clears and updates adapterData
    public void clearData() {
        mAdapterData.clear();
        notifyDataSetChanged();
    }

}