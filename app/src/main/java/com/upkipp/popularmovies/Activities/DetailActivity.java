package com.upkipp.popularmovies.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
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
    private static final String TITLE_KEY = "title";
    private static final String VOTE_AVRG_KEY = "vote_avrg";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String OVERVIEW_KEY = "overview";
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

    private ImageView backdropImageView;
    private ImageView posterImageView;

    private ImageButton upButton;

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

        Intent retrievedIntent = getIntent();
        defineViews();

        if (savedInstanceState == null) {
            if (retrievedIntent != null && retrievedIntent.hasExtra("index")) {
                //get passed index extra from intent
                int index = retrievedIntent.getIntExtra("index", 0);
                SearchAdapter searchAdapter = SearchAdapter.getInstance(null);
                //get selected movie using index
                final MovieData currentMovieData = searchAdapter.getMovieData(index);

                String movieId = currentMovieData.getId();
                String posterPath = currentMovieData.getPosterPath();
                String backdropPath = currentMovieData.getBackdropPath();

                //load images into ImageViews using glide
                NetworkFunctions
                        .loadImage(this, posterPath, posterImageView);
                NetworkFunctions
                        .loadImage(this, backdropPath, backdropImageView);

                //load reviews and videos
                loadReviewsHelper(movieId);
                loadVideosHelper(movieId);

                //set text in TextViews
                titleTextView.setText(currentMovieData.getTitle());
                voteAverageTextView.setText(String.valueOf(currentMovieData.getVoteAverage()));
                releaseDateTextView.setText(currentMovieData.getReleaseDate());
                //use tag to store full and original text to prevent loss after limitText()
                overviewTextView.setTag(currentMovieData.getOverview());
                overviewTextView.setText(limitText(overviewTextView.getTag().toString()));

            } else {
                Toast.makeText(this, "no data available", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {//use savedInstanceState to restore values
            String movieId = savedInstanceState.getString(ID_KEY, ERROR_TEXT);
            String posterPath = savedInstanceState.getString(POSTER_PATH_KEY, ERROR_TEXT);
            String backdropPath = savedInstanceState.getString(BACKDROP_PATH_KEY, ERROR_TEXT);

            //load images into ImageViews using glide
            NetworkFunctions
                    .loadImage(this, posterPath, posterImageView);
            NetworkFunctions
                    .loadImage(this, backdropPath, backdropImageView);

            //load reviews and videos
            loadReviewsHelper(movieId);
            loadVideosHelper(movieId);

            //set text in TextViews
            titleTextView.setText(savedInstanceState.getString(TITLE_KEY, ERROR_TEXT));
            voteAverageTextView.setText(savedInstanceState.getString(VOTE_AVRG_KEY, ERROR_TEXT));
            releaseDateTextView.setText(savedInstanceState.getString(RELEASE_DATE_KEY, ERROR_TEXT));
            //use tag to store full and original text to prevent loss after limitText()
            overviewTextView.setTag(savedInstanceState.getString(OVERVIEW_KEY, ERROR_TEXT));
            overviewTextView
                    .setText(limitText(overviewTextView.getTag().toString()));

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save TextView values
        outState.putString(TITLE_KEY, titleTextView.getText().toString());
        outState.putString(VOTE_AVRG_KEY, voteAverageTextView.getText().toString());
        outState.putString(RELEASE_DATE_KEY, releaseDateTextView.getText().toString());
        outState.putString(OVERVIEW_KEY, overviewTextView.getText().toString());

        //get values form intent
        Intent intent = getIntent();
        String movieID = intent.getStringExtra(ID_KEY);
        String posterPath = intent.getStringExtra(POSTER_PATH_KEY);
        String backdropPath = intent.getStringExtra(BACKDROP_PATH_KEY);

        //save id and path values
        outState.putString(ID_KEY, movieID);
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

        backdropImageView = findViewById(R.id.backdropImageView);
        posterImageView = findViewById(R.id.posterImageView);

        upButton = findViewById(R.id.upButton);

        showMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overviewTextView.setText(limitText(overviewTextView.getTag().toString()));
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
