package com.upkipp.popularmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.Utils.MovieDataParser;
import com.upkipp.popularmovies.Utils.NetworkFunctions;

import java.util.ArrayList;
import java.util.Map;

public final class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    //singleton variables
//    private static final Object LOCK = new Object();
//    private static ReviewAdapter sInstance;

    private ArrayList<Map<String, String>> mAdapterData;
    private final OnListItemClickListener mOnListItemClickListener;

    public ReviewAdapter(OnListItemClickListener clickListener) {
        mOnListItemClickListener = clickListener;
        mAdapterData = new ArrayList<>();
    }

//    public static ReviewAdapter getInstance(@Nullable OnListItemClickListener clickListener) {
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = new ReviewAdapter(clickListener);
//            }
//        }
//        return sInstance;
//    }

    public interface OnListItemClickListener {
        void onReviewItemClick(int position);
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final TextView authorTextVIew;
        final TextView contentTextView;

        private ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextVIew = itemView.findViewById(R.id.authorTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnListItemClickListener.onReviewItemClick(position);
        }
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int resourceId = R.layout.reviews;

        View view = layoutInflater.inflate(resourceId, viewGroup, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder adapterViewHolder, int position) {
        //get current reviewData
        Map<String, String> currentReviewData = mAdapterData.get(position);
        String author = currentReviewData.get("author");
        String content = currentReviewData.get("content");

        adapterViewHolder.authorTextVIew.setText(author);
        adapterViewHolder.contentTextView.setText(content);

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
            Map<String, String> currentReviewData = movieDataList.get(count);
            mAdapterData.add(currentReviewData);
            notifyDataSetChanged();
        }
//        mAdapterData.addAll(movieDataList);
//        notifyDataSetChanged();
        Log.i("INFO", "reviewAdapter data updated");
    }

    public Map<String, String> getReviewData(int index) {
        return mAdapterData.get(index);
    }

    //clears and updates adapterData
    public void clearData() {
        mAdapterData.clear();
        notifyDataSetChanged();
    }

}

