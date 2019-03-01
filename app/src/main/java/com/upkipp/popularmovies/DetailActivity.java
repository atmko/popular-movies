package com.upkipp.popularmovies;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.upkipp.popularmovies.Models.MovieData;

public final class DetailActivity extends AppCompatActivity {

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();

        //set action bar up(back) button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        //define Views
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView voteAverageTextView = findViewById(R.id.voteAverageTextView);
        TextView releaseDateTextView = findViewById(R.id.releaseDateTextView);
        TextView overviewTextView = findViewById(R.id.overviewTextView);

        ImageView backdropImageView = findViewById(R.id.backdropImageView);
        ImageView posterImageView = findViewById(R.id.posterImageView);

        //get Intent
        Intent retrievedIntent = getIntent();

        //check if index is available in intent extra
        if (retrievedIntent != null && retrievedIntent.hasExtra("index")) {
            index = retrievedIntent.getIntExtra("index", 0);

        } else {
            Toast.makeText(this, "no data available", Toast.LENGTH_SHORT).show();
            finish();
        }

        //get selected movie using index
        MovieData currentMovieData = MainActivity.searchAdapter.getMovieData(index);
        //load images into ImageViews using glide
        NetworkFunctions
                .loadImage(this, currentMovieData.getBackdropPath(), backdropImageView);
        NetworkFunctions
                .loadImage(this, currentMovieData.getPosterPath(), posterImageView);

        //set text in TextViews
        titleTextView.setText(currentMovieData.getTitle());
        voteAverageTextView.setText(String.valueOf(currentMovieData.getVoteAverage()));
        releaseDateTextView.setText(currentMovieData.getReleaseDate());
        overviewTextView.setText(currentMovieData.getOverview());

    }

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
}
