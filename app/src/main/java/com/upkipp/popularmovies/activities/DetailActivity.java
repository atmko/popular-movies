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
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.adapters.ReviewAdapter;
import com.upkipp.popularmovies.adapters.VideoAdapter;
import com.upkipp.popularmovies.database.AppDatabase;
import com.upkipp.popularmovies.utils.SearchPreferences;
import com.upkipp.popularmovies.utils.network_utils.AppExecutors;
import com.upkipp.popularmovies.view_models.DetailViewModel;
import com.upkipp.popularmovies.view_models.DetailViewModelFactory;
import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.utils.MovieDataParser;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.databinding.ActivityDetailBinding;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public final class DetailActivity extends AppCompatActivity
        implements VideoAdapter.OnListItemClickListener, ReviewAdapter.OnListItemClickListener {

    //intent value keys
    public static final String ID_KEY = "id";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String BACKDROP_PATH_KEY = "backdrop_path";
    public static final String TITLE_KEY = "title";
    public static final String VOTE_AVERAGE_KEY = "vote_avrg";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String OVERVIEW_KEY = "overview";
    private static final String ERROR_TEXT = MovieData.ErrorValues.STRING_ERROR;
    //index limits to truncate long text (synopsis and reviews)
    private static final int OVERVIEW_CUT_OFF_INDEX = 262;
    public static final int REVIEW_CUT_OFF_INDEX = 85;
    //binding variable
    ActivityDetailBinding mBinding;
    //adapters
    private static VideoAdapter videoAdapter;
    private static ReviewAdapter reviewAdapter;
    //custom up nav button
//    private ImageButton upButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);
        //toolbar customization
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        //set action bar up(back) button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        //---------------------------------------------
        //get Intent from SearchActivity
        Intent intent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            if (intent != null && intent.hasExtra(ID_KEY)) {

                //get passed values from intent
                String movieId = intent.getStringExtra(ID_KEY);
                String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
                String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);
                String title = intent.getStringExtra(TITLE_KEY);
                String voteAverage = intent.getStringExtra(VOTE_AVERAGE_KEY);
                String releaseDate = intent.getStringExtra(RELEASE_DATE_KEY);
                String overview = intent.getStringExtra(OVERVIEW_KEY);

                //load images into ImageViews using glide
                NetworkFunctions
                        .loadImage(this, posterPath, mBinding.posterImageView);
//                NetworkFunctions
//                        .loadImage(this, backdropPath, backdropImageView);

                //load reviews and videos
                loadReviewsHelper(movieId);
                loadVideosHelper(movieId);

                //set text in TextViews
                mBinding.titleTextView.setText(title);
                mBinding.voteAverageTextView.setText(voteAverage);
                mBinding.releaseDateTextView.setText(releaseDate);
                //use tag to store full and original text to prevent loss after limitText()
                TextView overviewTextView = mBinding.detailScrollView.overviewTextView;
                overviewTextView.setTag(overview);
                overviewTextView.setText(limitText(overviewTextView.getTag().toString(), OVERVIEW_CUT_OFF_INDEX));

            } else {
                //notify error
                Snackbar.make(findViewById(R.id.topLayout),
                        "no data available", Snackbar.LENGTH_LONG).show();
            }

        } else {//use savedInstanceState to restore values
            String movieId = savedInstanceState.getString(ID_KEY, ERROR_TEXT);
            String posterPath = savedInstanceState.getString(POSTER_PATH_KEY, ERROR_TEXT);
            String backdropPath = savedInstanceState.getString(BACKDROP_PATH_KEY, ERROR_TEXT);
            String title = savedInstanceState.getString(TITLE_KEY, ERROR_TEXT);
            String voteAverage = savedInstanceState.getString(VOTE_AVERAGE_KEY, ERROR_TEXT);
            String releaseDate = savedInstanceState.getString(RELEASE_DATE_KEY, ERROR_TEXT);
            String overview = savedInstanceState.getString(OVERVIEW_KEY, ERROR_TEXT);

            //load images into ImageViews using glide
            NetworkFunctions
                    .loadImage(this, posterPath, mBinding.posterImageView);
//            NetworkFunctions
//                    .loadImage(this, backdropPath, backdropImageView);

            //load reviews and videos
            loadReviewsHelper(movieId);
            loadVideosHelper(movieId);

            //set text in TextViews
            mBinding.titleTextView.setText(title);
            mBinding.voteAverageTextView.setText(voteAverage);
            mBinding.releaseDateTextView.setText(releaseDate);
            //use tag to store full and original text to prevent loss after limitText()
            TextView overviewTextView = mBinding.detailScrollView.overviewTextView;
            overviewTextView.setTag(overview);
            overviewTextView.setText(limitText(overviewTextView.getTag().toString(),
                    OVERVIEW_CUT_OFF_INDEX));

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save TextView values
        outState.putString(TITLE_KEY, mBinding.titleTextView.getText().toString());
        outState.putString(VOTE_AVERAGE_KEY, mBinding.voteAverageTextView.getText().toString());
        outState.putString(RELEASE_DATE_KEY, mBinding.releaseDateTextView.getText().toString());
        outState.putString(OVERVIEW_KEY,
                mBinding.detailScrollView.overviewTextView.getTag().toString());

        //get values form intent
        Intent intent = getIntent();
        String movieId = intent.getStringExtra(ID_KEY);
        String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
        String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);

        //save id and path values
        outState.putString(ID_KEY, movieId);
        outState.putString(POSTER_PATH_KEY, posterPath);
        outState.putString(BACKDROP_PATH_KEY, backdropPath);

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
                                , OVERVIEW_CUT_OFF_INDEX));
            }
        });

        final String movieId = getIntent().getStringExtra(ID_KEY);
        AppDatabase database = AppDatabase.getInstance(this);

        //configure view model
        DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(database, movieId);
        DetailViewModel viewModel = ViewModelProviders.of(this, detailViewModelFactory)
                .get(DetailViewModel.class);
        final LiveData<MovieData> favoriteLiveData = viewModel.getMovie();

        favoriteLiveData.observe(this, new Observer<MovieData>() {
            @Override
            public void onChanged(@Nullable MovieData movieData) {
                //update favorite icon/button in real time
                if (movieData == null) {
                    mBinding.saveFavoriteImageView.setTag("add favorite");
                    mBinding.saveFavoriteImageView
                            .setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
                }else {
                    mBinding.saveFavoriteImageView.setTag("already favorite");
                    mBinding.saveFavoriteImageView
                            .setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
                }
            }
        });

        //configure favorite button
        mBinding.saveFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBinding.saveFavoriteImageView.getTag().toString().equals("add favorite")) {
                    addToFavorites();
                    Snackbar.make(findViewById(R.id.topLayout),
                            "added to favorites", Snackbar.LENGTH_LONG).show();
                } else if (mBinding.saveFavoriteImageView.getTag().toString().equals("already favorite")) {
                    deleteFavorite(favoriteLiveData.getValue());
                    Snackbar.make(findViewById(R.id.topLayout),
                            "removed from favorites", Snackbar.LENGTH_LONG).show();
                }
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
                        .setChooserTitle("Choose App")
                        .setText(NetworkFunctions.getMovieUrl(DetailActivity.this, movieId))
                        .startChooser();
            }
        });

//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavUtils.navigateUpFromSameTask(DetailActivity.this);
//            }
//        });

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
        videoAdapter = new VideoAdapter(this);
        reviewAdapter = new ReviewAdapter(this);
        //set adapter to RecyclerView
        mBinding.detailScrollView.videoRecyclerView.setAdapter(videoAdapter);
        mBinding.detailScrollView.reviewRecyclerView.setAdapter(reviewAdapter);

    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return layoutManager;
    }

    private void loadReviewsHelper(String movieId) {
        NetworkFunctions.loadReviews(movieId).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Map<String, String>> reviewList = MovieDataParser.parseReviews(response);
                    reviewAdapter.addAdapterData(reviewList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    private void loadVideosHelper(String movieId) {
        NetworkFunctions.loadVideos(movieId).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Map<String, String>> videoList = MovieDataParser.parseVideos(response);
                    videoAdapter.addAdapterData(videoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    //start video intent
    @Override
    public void onVideoItemClick(int position) {

        Map<String, String>  videoData = videoAdapter.getVideoData(position);
        String path = videoData.get("path");

        Uri fullVideoPath = Uri.parse(("https://www.youtube.com/watch?v=" + path));

        Intent videoIntent = new Intent(Intent.ACTION_VIEW, fullVideoPath);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(videoIntent);

        }
    }

    //start review intent
    @Override
    public void onReviewItemClick(int position) {

        Intent reviewIntent = new Intent(getApplicationContext(), ReviewActivity.class);
        String reviewAuthor = reviewAdapter.getReviewData(position).get(ReviewAdapter.REVIEW_AUTHOR_KEY);
        String reviewContent = reviewAdapter.getReviewData(position).get(ReviewAdapter.REVIEW_CONTENT_KEY);

        reviewIntent.putExtra(ReviewAdapter.REVIEW_AUTHOR_KEY, reviewAuthor);
        reviewIntent.putExtra(ReviewAdapter.REVIEW_CONTENT_KEY, reviewContent);

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
//        return true;

        return super.onOptionsItemSelected(item);
    }

    //limit long text
    private String limitText(String fullText, int cutOffIndex) {

        TextView showMoreTextView = mBinding.detailScrollView.showMoreTextView;

        if (fullText.length() > cutOffIndex && (showMoreTextView.getTag()).equals("shown")) {
            String reducedText = fullText.subSequence(0, cutOffIndex) + "...";

            showMoreTextView.setTag("hidden");
            showMoreTextView.setText("show more");

            return reducedText;

        } else {
            if (fullText.length() <= cutOffIndex)showMoreTextView.setVisibility(View.GONE);

            showMoreTextView.setTag("shown");
            showMoreTextView.setText("show less");

            return fullText;

        }

    }


    private void addToFavorites() {
        //get database instance
        final AppDatabase database = AppDatabase.getInstance(this);

        Intent intent = getIntent();

        //get values from intent
        String movieId = intent.getStringExtra(ID_KEY);
        String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
        String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);

        //create movie date object
        final MovieData movieData =
                new MovieData(movieId,
                        mBinding.voteAverageTextView.getText().toString(),
                        mBinding.titleTextView.getText().toString(),
                        posterPath,
                        backdropPath,
                        mBinding.detailScrollView.overviewTextView.getTag().toString(),
                        mBinding.releaseDateTextView.getText().toString()

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
