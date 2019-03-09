package com.upkipp.popularmovies.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.Adapters.ReviewAdapter;
import com.upkipp.popularmovies.Adapters.SearchAdapter;
import com.upkipp.popularmovies.Adapters.VideoAdapter;
import com.upkipp.popularmovies.Models.AppDatabase;
import com.upkipp.popularmovies.Models.AppExecutors;
import com.upkipp.popularmovies.Models.DetailViewModel;
import com.upkipp.popularmovies.Models.DetailViewModelFactory;
import com.upkipp.popularmovies.Models.MovieData;
import com.upkipp.popularmovies.Utils.MovieDataParser;
import com.upkipp.popularmovies.Utils.NetworkFunctions;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.Utils.SearchPreferences;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DetailActivity extends AppCompatActivity
        implements VideoAdapter.OnListItemClickListener, ReviewAdapter.OnListItemClickListener {

//    private int index;

//    private static String IS_FIRST_INIT_KEY = "isFirstInit";//checks if  first initialization

    public static final String ID_KEY = "id";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String BACKDROP_PATH_KEY = "backdrop_path";
    public static final String TITLE_KEY = "title";
    public static final String VOTE_AVERAGE_KEY = "vote_avrg";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String OVERVIEW_KEY = "overview";
    private static final String ERROR_TEXT = MovieData.ErrorValues.STRING_ERROR;

    private RecyclerView mVideoRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private static VideoAdapter videoAdapter;
    private static ReviewAdapter reviewAdapter;

    private TextView titleTextView;
    private TextView voteAverageTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;
    private TextView showMoreTextView;

//    private ImageView backdropImageView;
    private ImageView posterImageView;

    private ImageView saveFavoriteImageView;

//    private ImageButton upButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//
//        //set action bar up(back) button
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//
//        }
        //---------------------------------------------
        //get Intent

        Intent intent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            if (intent != null && intent.hasExtra(ID_KEY)) {
                //get passed index extra from intent

                String movieId = intent.getStringExtra(ID_KEY);
                String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
                String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);
                String title = intent.getStringExtra(TITLE_KEY);
                String voteAverage = intent.getStringExtra(VOTE_AVERAGE_KEY);
                String releaseDate = intent.getStringExtra(RELEASE_DATE_KEY);
                String overview = intent.getStringExtra(OVERVIEW_KEY);

                //load images into ImageViews using glide
                NetworkFunctions
                        .loadImage(this, posterPath, posterImageView);
//                NetworkFunctions
//                        .loadImage(this, backdropPath, backdropImageView);

                //load reviews and videos
                loadReviewsHelper(movieId);
                loadVideosHelper(movieId);

                //set text in TextViews
                titleTextView.setText(title);
                voteAverageTextView.setText(voteAverage);
                releaseDateTextView.setText(releaseDate);
                //use tag to store full and original text to prevent loss after limitText()
                overviewTextView.setTag(overview);
                overviewTextView.setText(limitText(overviewTextView.getTag().toString()));

            } else {
                Toast.makeText(this, "no data available", Toast.LENGTH_SHORT).show();
                finish();
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
                    .loadImage(this, posterPath, posterImageView);
//            NetworkFunctions
//                    .loadImage(this, backdropPath, backdropImageView);

            //load reviews and videos
            loadReviewsHelper(movieId);
            loadVideosHelper(movieId);

            //set text in TextViews
            titleTextView.setText(title);
            voteAverageTextView.setText(voteAverage);
            releaseDateTextView.setText(releaseDate);
            //use tag to store full and original text to prevent loss after limitText()
            overviewTextView.setTag(overview);
            overviewTextView
                    .setText(limitText(overviewTextView.getTag().toString()));

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save TextView values
        outState.putString(TITLE_KEY, titleTextView.getText().toString());
        outState.putString(VOTE_AVERAGE_KEY, voteAverageTextView.getText().toString());
        outState.putString(RELEASE_DATE_KEY, releaseDateTextView.getText().toString());
        outState.putString(OVERVIEW_KEY, overviewTextView.getText().toString());

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

    private void defineViews() {
        //define Views
        titleTextView = findViewById(R.id.titleTextView);
        voteAverageTextView = findViewById(R.id.voteAverageTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        overviewTextView = findViewById(R.id.overviewTextView);
        showMoreTextView = findViewById(R.id.showMoreTextView);

//        backdropImageView = findViewById(R.id.backdropImageView);
        posterImageView = findViewById(R.id.posterImageView);

//        upButton = findViewById(R.id.upButton);

        showMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overviewTextView.setText(limitText(overviewTextView.getTag().toString()));
            }
        });

        saveFavoriteImageView = findViewById(R.id.saveFavoriteImageView);
        String movieId = getIntent().getStringExtra(ID_KEY);
        AppDatabase database = AppDatabase.getInstance(this);

        DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(database, movieId);
        DetailViewModel viewModel = ViewModelProviders.of(this, detailViewModelFactory).get(DetailViewModel.class);
        final LiveData<MovieData> favoriteLiveData = viewModel.getMovie();

        favoriteLiveData.observe(this, new Observer<MovieData>() {
            @Override
            public void onChanged(@Nullable MovieData movieData) {
                //update favorite icon/button in real time
                if (movieData == null) {
                    saveFavoriteImageView.setTag("add favorite");
                    saveFavoriteImageView.setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
                }else {
                    saveFavoriteImageView.setTag("already favorite");
                    saveFavoriteImageView.setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
                }
            }
        });

        saveFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveFavoriteImageView.getTag().toString().equals("add favorite")) {
                    addToFavorites();
                } else if (saveFavoriteImageView.getTag().toString().equals("already favorite")) {
                    deleteFavorite(favoriteLiveData.getValue());
                }
            }
        });

//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavUtils.navigateUpFromSameTask(DetailActivity.this);
//            }
//        });

        //---configure RecyclerView
        mVideoRecyclerView = findViewById(R.id.videoRecyclerView);
        mVideoRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        mReviewRecyclerView.setHasFixedSize(true);
        //configureLayoutManager returns a LayoutManager
        mVideoRecyclerView.setLayoutManager(configureLayoutManager());
        mReviewRecyclerView.setLayoutManager(configureLayoutManager());
        //--------------
        mVideoRecyclerView.setFocusable(false);
        mReviewRecyclerView.setFocusable(false);

        //define video adapter
        videoAdapter = new VideoAdapter(this);
        reviewAdapter = new ReviewAdapter(this);
        //set adapter to RecyclerView
        mVideoRecyclerView.setAdapter(videoAdapter);
        mReviewRecyclerView.setAdapter(reviewAdapter);

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

    @Override
    public void onVideoItemClick(int position) {
        //start video intent

        Map<String, String>  videoData = videoAdapter.getVideoData(position);
        String path = videoData.get("path");

        Uri fullVideoPath = Uri.parse(("https://www.youtube.com/watch?v=" + path));

        Intent videoIntent = new Intent(Intent.ACTION_VIEW, fullVideoPath);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(videoIntent);

        }
    }

    @Override
    public void onReviewItemClick(int position) {
//        //start video intent
//
//        Map<String, String>  videoData = videoAdapter.getVideoData(position);
//        String path = videoData.get("path");
//
//        Uri fullVideoPath = Uri.parse(("https://www.youtube.com/watch?v=" + path));
//
//        Intent videoIntent = new Intent(Intent.ACTION_VIEW, fullVideoPath);
//        if (videoIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(videoIntent);
//
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;

//        return super.onOptionsItemSelected(item);
    }

    private String limitText(String fullText) {
        TextView showMoreTextView = findViewById(R.id.showMoreTextView);
        int cutOffIndex = 262;
        if (fullText.length() > cutOffIndex && ((String) showMoreTextView.getTag()).equals("shown")) {
            String reducedText = fullText.substring(0, cutOffIndex) + "...";

            showMoreTextView.setTag("hidden");
            showMoreTextView.setText("show more");

            return reducedText;

        } else {
            if (fullText.length() <= cutOffIndex) showMoreTextView.setVisibility(View.GONE);

            showMoreTextView.setTag("shown");
            showMoreTextView.setText("show less");

            return fullText;

        }

    }

    private void addToFavorites() {
        final AppDatabase database = AppDatabase.getInstance(this);

        Intent intent = getIntent();

        //get values form intent
        String movieId = intent.getStringExtra(ID_KEY);
        String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
        String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);

        final MovieData movieData =
                new MovieData(movieId,
                        voteAverageTextView.getText().toString(),
                        titleTextView.getText().toString(),
                        posterPath,
                        backdropPath,
                        overviewTextView.getTag().toString(),
                        releaseDateTextView.getText().toString()

                );

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.favoritesDao().addFavorite(movieData);

            }
        });
    }

    private void deleteFavorite(final MovieData movieData) {
        final AppDatabase database = AppDatabase.getInstance(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.favoritesDao().deleteFavorite(movieData);
            }
        });
    }

}
