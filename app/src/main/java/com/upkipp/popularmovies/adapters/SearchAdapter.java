/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;

import java.util.ArrayList;
import java.util.List;

public final class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder> {
    private final List<MovieData> mAdapterData;
    private final OnListItemClickListener mOnListItemClickListener;
    private final boolean mUseFirstLayout;

    //layout ids
    private final int FIRST_LAYOUT_ID = 2;
    @SuppressWarnings("FieldCanBeLocal")
    private final int STANDARD_LAYOUT_ID = 3;

    public SearchAdapter(OnListItemClickListener clickListener) {
        mOnListItemClickListener = clickListener;
        Context context = (Context) clickListener;
        mUseFirstLayout = context.getResources().getBoolean(R.bool.use_first_result_layout);
        mAdapterData = new ArrayList<>();
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
    public SearchAdapter.SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        int resourceId;

        if (viewType == FIRST_LAYOUT_ID) {
            resourceId = R.layout.first_search_result;

        } else {
            resourceId = R.layout.search;
        }

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

    @Override
    public int getItemViewType(int position) {
        //if using first layout and this is the first item
        if (mUseFirstLayout && position == 0) {
            return FIRST_LAYOUT_ID;

        } else {
            return STANDARD_LAYOUT_ID;
        }

    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addAdapterData(List<MovieData> movieDataList) {
        //TODO 1//<<<<<FOR FUTURE IMPLEMENTATION>>>>>
//        int currentPage = SearchPreferences.getInstance().getCurrentPage();
//        int pageAdjustment = currentPage - 1;
//
//        int listSize = movieDataList.size();
//
//        //get positionCount starting at 1, i.e: non zero index
//        int itemIndex = (pageAdjustment * listSize);
//        Log.d("POS1", String.valueOf(itemIndex));
//
//        int rowIndex = Math.abs(itemIndex/SearchActivity.COLUMN_SPAN);
//        Log.d("POS2", String.valueOf(rowIndex));
//
//        int colIndex = itemIndex - (rowIndex * SearchActivity.COLUMN_SPAN);
//        Log.d("POS3", String.valueOf(colIndex));
//
//        MovieData previousIndex = null;
//
//        try {
//            previousIndex = getMovieData(itemIndex - 1);
//
//        } catch (IndexOutOfBoundsException e) {
//
//        }
//
//        if (colIndex != 0 && previousIndex == null) {
//                int dummyCount = colIndex;
//                MovieData dummyData = new MovieData(0, 0, false, 0.0,
//                        "", 0.0, null, "", null,
//                        null, "", false, "", "");
//
//                for (int i = 1; i <= dummyCount; i++) {
//                    movieDataList.add(0, dummyData);
//                }
//            }
        //<<<<<FOR FUTURE IMPLEMENTATION>>>>>

//        for(int count = 0 ; count < listSize; count++) {

            //<<<<<FOR FUTURE IMPLEMENTATION>>>>>
            //index in list with respect to page
//            int rawIndex = currentPage * count;
            //<<<<<FOR FUTURE IMPLEMENTATION>>>>>

//            MovieData currentMovieData = movieDataList.get(count);
//            mAdapterData.add(currentMovieData);
//            notifyDataSetChanged();
//        }
        mAdapterData.addAll(movieDataList);
        notifyDataSetChanged();
    }

    public MovieData getMovieData(int index) {
        return mAdapterData.get(index);
    }

    public List<MovieData> getMovieDataList() {
        return mAdapterData;
    }

    //clears and updates adapterData
    public void clearData() {
        mAdapterData.clear();
        notifyDataSetChanged();
    }

}
