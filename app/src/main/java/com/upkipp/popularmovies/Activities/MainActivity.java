package com.upkipp.popularmovies.Activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.Models.MovieData;
import com.upkipp.popularmovies.Utils.MovieDataParser;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.Adapters.SearchAdapter;
import com.upkipp.popularmovies.Utils.NetworkFunctions;
import com.upkipp.popularmovies.Utils.SearchPreferences;

import org.json.JSONException;

import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity
        implements SearchAdapter.OnListItemClickListener {

    public static final int COLUMN_SPAN = 3;

    private SearchPreferences searchPreferences;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

//        ActionBar actionBar = getSupportActionBar();
//
//        //set action bar up(back) button
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//
//        }

        defineViews();

        if (savedInstanceState == null) {
            //execute search
            executePresetMovieSearch(true);//defaults to popular movies
        } else {
            //if empty adapter, executePresetMovieSearch(true)
            //prevents overwriting data on rotate or when data exists
            if (searchAdapter.getItemCount() == 0) {
                executePresetMovieSearch(true);//defaults to popular movies

            }

        }

    }

    private void defineViews() {
        //---configure recyclerVIew
        RecyclerView mSearchPosterRecyclerView = findViewById(R.id.searchPosterRecyclerView);
        mSearchPosterRecyclerView.setHasFixedSize(true);
        //configureLayoutManager returns a LayoutManager
        mSearchPosterRecyclerView.setLayoutManager(configureLayoutManager());

        //--configure load on scroll
        mSearchPosterRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
                    throws java.lang.NullPointerException {

                super.onScrolled(recyclerView, dx, dy);

                int currentPage = searchPreferences.getCurrentPage();
                int availablePages = searchPreferences.getTotalPages();

                int totalNumOfItems = searchAdapter.getItemCount();
                int lastItemIndex = totalNumOfItems - 1;

                //error caught in throw when invoking findLastCompletelyVisibleItemPosition()
                int lastShown = ((GridLayoutManager)recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();

                //lastVis == lastItemIndex makes sure we are at the end of list
                boolean lastItem = lastShown == lastItemIndex;
                boolean morePagesAvailable = currentPage < availablePages;
                //!emptyAdapter prevents unwanted page load when clearing adapter data...
                // ...because lastItem is considered true
                boolean emptyAdapter = searchAdapter.isEmpty();

                //if at lastItem && if morePagesAvailable && if adapter not empty
                if (lastItem && morePagesAvailable && !emptyAdapter) {
                    //load next page
                    loadNextPage(currentPage + 1);
                }
            }

        });

        //define search adapter and search preferences
        searchAdapter = SearchAdapter.getInstance(this);
        searchPreferences = SearchPreferences.getInstance();//takes api key and context

        //set adapter to RecyclerView
        mSearchPosterRecyclerView.setAdapter(searchAdapter);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    private void executePresetMovieSearch(boolean newSearch) {
        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append
        if (newSearch) {
            searchAdapter.clearData();
            searchPreferences.setTargetPage(1);
        }

        //build AN request
        final ANRequest request = NetworkFunctions.buildSearchRequest();

        request.getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String returnedJSONString) {
                try {
                    //set current url string path
                    searchPreferences.setQueryUrlString(request.getUrl());

                    //parse and populate retrieved data
                    ArrayList<MovieData> movieDataList = MovieDataParser.parseData(returnedJSONString);
                    searchAdapter.addAdapterData(movieDataList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                //notify error
                Toast.makeText(MainActivity.this, anError.getErrorDetail(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    void loadNextPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executePresetMovieSearch(false);
    }

    void loadPreviousPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executePresetMovieSearch(false);
    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_SPAN);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    public void onItemClick(int position) {
        //start detail activity
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        detailActivityIntent.putExtra("index", position);

        MovieData currentMovieData = searchAdapter.getMovieData(position);
        //paths needed to restore ImageViews on restore/rotate
        detailActivityIntent.putExtra(DetailActivity.ID_KEY, currentMovieData.getId());
        detailActivityIntent.putExtra(DetailActivity.BACKDROP_PATH_KEY, currentMovieData.getBackdropPath());
        detailActivityIntent.putExtra(DetailActivity.POSTER_PATH_KEY, currentMovieData.getPosterPath());
        startActivity(detailActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        //-------------sort by popularity / votes
        //get menu item
        MenuItem popularityItem = menu.findItem(R.id.popular_movies);
        //set click listener
        popularityItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //SHOW POPULAR
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_POPULAR);
                searchPreferences.setTargetPage(1);
                //execute search
                executePresetMovieSearch(true);
                return true;
            }
        });

        //get menu item
        MenuItem ratingItem = menu.findItem(R.id.top_rated_movies);
        //set click listener
        ratingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //SHOW TOP RATED
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_TOP_RATED);
                searchPreferences.setTargetPage(1);
                //execute search
                executePresetMovieSearch(true);
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SearchPreferences.resetPreferences();
    }
}
