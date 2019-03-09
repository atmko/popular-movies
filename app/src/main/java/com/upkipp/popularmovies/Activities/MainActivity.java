package com.upkipp.popularmovies.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.Models.MainViewModel;
import com.upkipp.popularmovies.Models.MovieData;
import com.upkipp.popularmovies.Utils.MovieDataParser;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.Adapters.SearchAdapter;
import com.upkipp.popularmovies.Utils.NetworkFunctions;
import com.upkipp.popularmovies.Utils.SearchPreferences;

import org.json.JSONException;

import java.util.List;

public final class MainActivity extends AppCompatActivity
        implements SearchAdapter.OnListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
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
            executeMovieSearch(true);//defaults to popular movies
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else {
            //if empty adapter, executeMovieSearch(true)
            //prevents overwriting data on rotate or when data exists
            if (searchAdapter.isEmpty()) {
                executeMovieSearch(true);//defaults to popular movies

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

                //do nothing if sortParamVal is favorites
                if (searchPreferences.getSortValue().equals(SearchPreferences.SORT_BY_FAVORITES)) {return;}

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
                //!emptyAdapter prevents unwanted page loads when clearing adapter data...
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

    private void executeMovieSearch(boolean newSearch) {
        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append

        if (newSearch) {
            searchAdapter.clearData();
            searchPreferences.setTargetPage(1);
        }

        //if sortParamVal is favorites setupViewModel()
        if (searchPreferences.getSortValue().equals(SearchPreferences.SORT_BY_FAVORITES)) {
            setupViewModel();
        } else {
            presetMovieSearch();
        }
    }


    void loadNextPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executeMovieSearch(false);
    }

    void loadPreviousPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executeMovieSearch(false);
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
        detailActivityIntent.putExtra(DetailActivity.TITLE_KEY, currentMovieData.getTitle());
        detailActivityIntent.putExtra(DetailActivity.VOTE_AVERAGE_KEY, currentMovieData.getVoteAverage());
        detailActivityIntent.putExtra(DetailActivity.RELEASE_DATE_KEY, currentMovieData.getReleaseDate());
        detailActivityIntent.putExtra(DetailActivity.OVERVIEW_KEY, currentMovieData.getOverview());

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
                //execute search
                executeMovieSearch(true);
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
                //execute search
                executeMovieSearch(true);
                return true;
            }
        });

        //get menu item
        MenuItem favoritesItem = menu.findItem(R.id.favorite_movies);
        //set click listener
        favoritesItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //SHOW FAVORITES
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_FAVORITES);
                //execute search
                executeMovieSearch(true);
                return true;
            }
        });

        return true;
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<MovieData>> favorites = viewModel.getMovies();
        favorites.observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> movieData) {
                Log.d(TAG, "Receiving favorites from LiveData in ViewModel");
                //prevents bug i.e clearing adapter for other sort values
                if (searchPreferences.getSortValue().equals(SearchPreferences.SORT_BY_FAVORITES)) {
                    searchAdapter.clearData();
                    //check null
                    if (movieData != null) {
                        searchAdapter.addAdapterData(movieData);
                    }
                }
            }
        });
    }

    private void presetMovieSearch() {

        //build AN request
        final ANRequest request = NetworkFunctions.buildSearchRequest();

        request.getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String returnedJSONString) {
                try {
                    //set current url string path
                    searchPreferences.setQueryUrlString(request.getUrl());

                    //parse and populate retrieved data
                    List<MovieData> movieDataList = MovieDataParser.parseData(returnedJSONString);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SearchPreferences.resetPreferences();
    }
}
