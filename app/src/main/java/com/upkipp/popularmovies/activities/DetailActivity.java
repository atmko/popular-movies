/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.adapters.ReviewAdapter;
import com.upkipp.popularmovies.adapters.VideoAdapter;
import com.upkipp.popularmovies.databases.AppDatabase;
import com.upkipp.popularmovies.utils.SearchPreferences;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;
import com.upkipp.popularmovies.utils.network_utils.AppExecutors;
import com.upkipp.popularmovies.view_models.DetailViewModel;
import com.upkipp.popularmovies.view_models.DetailViewModelFactory;
import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.utils.MovieDataParser;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.databinding.ActivityDetailBinding;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Map;

public final class DetailActivity extends AppCompatActivity
        implements VideoAdapter.OnListItemClickListener, ReviewAdapter.OnListItemClickListener {

    //intent value keys
    public static final String POSITION_KEY = "index";
    //index limits to truncate long text (synopsis and reviews)
    private final int overviewCutOffIndex = 262;
    public static final int REVIEW_CUT_OFF_INDEX = 85;

    //binding variable
    private ActivityDetailBinding mBinding;

    private SearchPreferences searchPreferences;
    private MovieData selectedMovieData;

    //adapters
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        customizeActionBar();

        //get Intent from SearchActivity
        Intent intent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            //if there's movie data
            if (intent != null && intent.hasExtra(SearchActivity.SELECTED_MOVIE_KEY)) {
                //get passed movie data from intent
                searchPreferences =
                        Parcels.unwrap(intent.getParcelableExtra(SearchActivity.SEARCH_PREFERENCES_KEY));
                selectedMovieData =
                        Parcels.unwrap(intent.getParcelableExtra(SearchActivity.SELECTED_MOVIE_KEY));

                setDataToViews();

            } else {//if no movie data
                //notify error
                Snackbar.make(findViewById(R.id.topLayout),
                        getString(R.string.detail_error_no_data_available), Snackbar.LENGTH_LONG).show();

                //close activity
                finish();
            }

        } else {//use savedInstanceState to restore values
            searchPreferences = Parcels.unwrap
                    (savedInstanceState.getParcelable(SearchActivity.SEARCH_PREFERENCES_KEY));
            selectedMovieData = Parcels.unwrap
                    (savedInstanceState.getParcelable(SearchActivity.SELECTED_MOVIE_KEY));

            assert selectedMovieData != null;

            setDataToViews();

        }

        setupViewModelObservation();

    }

    private void customizeActionBar() {
        //toolbar customization
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        //set action bar up(back) button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    private void setDataToViews() {
        //load images into ImageViews using glide
        NetworkFunctions
                .loadImage(this,
                        selectedMovieData.getPosterPath(),
                        mBinding.posterImageView);

        //load reviews and videos
        loadReviewsHelper(selectedMovieData.getId());
        loadVideosHelper(selectedMovieData.getId());

        //set text in TextViews
        mBinding.titleTextView.setText(selectedMovieData.getTitle());
        mBinding.voteAverageTextView.setText(selectedMovieData.getVoteAverage());
        mBinding.releaseDateTextView.setText(selectedMovieData.getReleaseDate());
        //use tag to store full and original text to prevent loss after limitText()
        TextView overviewTextView = mBinding.detailScrollView.overviewTextView;
        overviewTextView.setTag(selectedMovieData.getOverview());
        overviewTextView.setText(
                limitText(overviewTextView.getTag().toString(), overviewCutOffIndex));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save MovieData object
        outState.putParcelable(SearchActivity.SEARCH_PREFERENCES_KEY, Parcels.wrap(searchPreferences));
        outState.putParcelable(SearchActivity.SELECTED_MOVIE_KEY, Parcels.wrap(selectedMovieData));
    }

    //define Views
    private void defineViews() {
        //configure show more button
        mBinding.detailScrollView.showMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView overviewTextView = mBinding.detailScrollView.overviewTextView;
                overviewTextView
                        .setText(limitText(overviewTextView.getTag().toString()
                                , overviewCutOffIndex));
            }
        });

        //configure share button
        mBinding.shareLinkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder
                        /* The from method specifies the Context from which this share is coming from */
                        .from(DetailActivity.this)
                        .setType("text/plain")
                        .setChooserTitle(getString(R.string.detail_share_title))
                        .setText(NetworkFunctions
                                .getMovieUrl(DetailActivity.this, selectedMovieData.getId()))
                        .startChooser();
            }
        });

        //---configure RecyclerView
        mBinding.detailScrollView.videoRecyclerView.setHasFixedSize(true);
        mBinding.detailScrollView.reviewRecyclerView.setHasFixedSize(true);
        //configureLayoutManager returns a LayoutManager
        mBinding.detailScrollView.videoRecyclerView.setLayoutManager(configureLayoutManager());
        mBinding.detailScrollView.reviewRecyclerView.setLayoutManager(configureLayoutManager());
        //--------------
        //disable focus
        mBinding.detailScrollView.videoRecyclerView.setFocusable(false);
        mBinding.detailScrollView.reviewRecyclerView.setFocusable(false);

        //define video adapter
        mVideoAdapter = new VideoAdapter(this);
        mReviewAdapter = new ReviewAdapter(this);
        //set adapter to RecyclerView
        mBinding.detailScrollView.videoRecyclerView.setAdapter(mVideoAdapter);
        mBinding.detailScrollView.reviewRecyclerView.setAdapter(mReviewAdapter);

    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return layoutManager;
    }

    private void setupViewModelObservation() {
        AppDatabase database = AppDatabase.getInstance(this);

        //configure view model
        DetailViewModelFactory detailViewModelFactory =
                new DetailViewModelFactory(database, selectedMovieData.getId());
        DetailViewModel viewModel = ViewModelProviders.of(this, detailViewModelFactory)
                .get(DetailViewModel.class);
        final LiveData<MovieData> favoriteLiveData = viewModel.getMovie();

        favoriteLiveData.observe(this, new Observer<MovieData>() {
            @Override
            public void onChanged(@Nullable MovieData movieData) {
                //update favorite icon/button in real time
                if (movieData == null) {
                    mBinding.saveFavoriteImageView.setTag(getString(R.string.detail_add_favorite));
                    mBinding.saveFavoriteImageView
                            .setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
                }else {
                    mBinding.saveFavoriteImageView.setTag(getString(R.string.detail_remove_favorite));
                    mBinding.saveFavoriteImageView
                            .setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
                }
            }
        });

        //configure favorite button
        mBinding.saveFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBinding.saveFavoriteImageView.getTag().toString().equals(getString(R.string.detail_add_favorite))) {
                    addToFavorites();
                    Snackbar.make(findViewById(R.id.topLayout),
                            getString(R.string.detail_add_favorite_message), Snackbar.LENGTH_LONG).show();
                } else if (mBinding.saveFavoriteImageView.getTag().toString().equals(getString(R.string.detail_remove_favorite))) {
                    deleteFavorite(favoriteLiveData.getValue());
                    Snackbar.make(findViewById(R.id.topLayout),
                            getString(R.string.detail_remove_favorite_message), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadReviewsHelper(String movieId) {
        NetworkFunctions.loadReviews(movieId, searchPreferences).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                ArrayList<Map<String, String>> reviewList = MovieDataParser.parseReviews(response);
                mReviewAdapter.addAdapterData(reviewList);
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    private void loadVideosHelper(String movieId) {
        NetworkFunctions.loadVideos(movieId, searchPreferences).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                ArrayList<Map<String, String>> videoList = MovieDataParser.parseVideos(response);
                mVideoAdapter.addAdapterData(videoList);
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    //start video intent
    @Override
    public void onVideoItemClick(int position) {

        Map<String, String>  videoData = mVideoAdapter.getVideoData(position);
        String path = videoData.get(ApiConstants.VIDEO_PATH_KEY);

        Uri fullVideoPath = Uri.parse((ApiConstants.YOUTUBE_INTENT_BASE_URL + path));

        Intent videoIntent = new Intent(Intent.ACTION_VIEW, fullVideoPath);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(videoIntent);

        }
    }

    //start review intent
    @Override
    public void onReviewItemClick(int position) {

        Intent reviewIntent = new Intent(getApplicationContext(), ReviewActivity.class);
        String reviewAuthor = mReviewAdapter.getReviewData(position).get(ApiConstants.REVIEW_AUTHOR_KEY);
        String reviewContent = mReviewAdapter.getReviewData(position).get(ApiConstants.REVIEW_CONTENT_KEY);

        reviewIntent.putExtra(ApiConstants.REVIEW_AUTHOR_KEY, reviewAuthor);
        reviewIntent.putExtra(ApiConstants.REVIEW_CONTENT_KEY, reviewContent);

        startActivity(reviewIntent);

    }

    //configure options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //limit long text
    private String limitText(String fullText, int cutOffIndex) {

        TextView showMoreTextView = mBinding.detailScrollView.showMoreTextView;

        if (fullText.length() > cutOffIndex && (showMoreTextView.getText()).equals(getString(R.string.detail_show_less_text))) {
            String reducedText = fullText.subSequence(0, cutOffIndex) + "...";

            showMoreTextView.setText(getString(R.string.detail_show_more_text));

            return reducedText;

        } else {
            if (fullText.length() <= cutOffIndex)showMoreTextView.setVisibility(View.GONE);

            showMoreTextView.setText(getString(R.string.detail_show_less_text));

            return fullText;

        }

    }

    private void addToFavorites() {
        //get database instance
        final AppDatabase database = AppDatabase.getInstance(this);

        //create movie date object
        final MovieData movieData =
                new MovieData(selectedMovieData.getId(),
                        selectedMovieData.getVoteAverage(),
                        selectedMovieData.getTitle(),
                        selectedMovieData.getPosterPath(),
                        selectedMovieData.getBackdropPath(),
                        selectedMovieData.getOverview(),
                       selectedMovieData.getReleaseDate()

                );

        //view model not needed for single background database action
        //executor used instead
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.favoritesDao().addFavorite(movieData);

            }
        });
    }

    private void deleteFavorite(final MovieData movieData) {
        //get database instance
        final AppDatabase database = AppDatabase.getInstance(this);
        //view model not needed for single background database action
        //executor used instead
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.favoritesDao().deleteFavorite(movieData);
            }
        });
    }
}
