/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.upkipp.popularmovies.view_models.SearchViewModel;
import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.utils.MovieDataParser;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.adapters.SearchAdapter;
import com.upkipp.popularmovies.utils.network_utils.NetworkFunctions;
import com.upkipp.popularmovies.utils.SearchPreferences;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

public final class SearchActivity extends AppCompatActivity
        implements SearchAdapter.OnListItemClickListener {

    private final String TAG = SearchActivity.class.getSimpleName();
    public static final String SELECTED_MOVIE_KEY = "selected_movie";
    public static final String SEARCH_PREFERENCES_KEY = "search_preferences";
    public static final String MOVIE_DATA_LIST_KEY = "movie_data_list";
    private static final String QUERY_STRING_KEY = "query_string";
    private static final String SHOW_SEARCH_BOX_KEY = "show_search_box";

    private SearchPreferences searchPreferences;
    private SearchAdapter searchAdapter;

    //search box
    private EditText searchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        customizeActionBar();

        defineViews();

        if (savedInstanceState == null) {
            //define searchPreferences
            searchPreferences = new SearchPreferences
                    (SearchPreferences.SORT_BY_POPULAR, SearchPreferences.ENG_US);

            //execute search
            executeMovieSearch(true);

        } else {
            //get saved searchPreferences
            searchPreferences = Parcels.unwrap
                    (savedInstanceState.getParcelable(SEARCH_PREFERENCES_KEY));

            //restore saved values
            boolean showSearchBox = savedInstanceState.getBoolean(SHOW_SEARCH_BOX_KEY);
            String queryString = savedInstanceState.getString(QUERY_STRING_KEY, "");

            //show search bi=ox if true
            if (showSearchBox) {
                searchTextView.setVisibility(View.VISIBLE);
                searchTextView.setText(queryString);
            }

            //if sort value is favorites
            if (searchPreferences.getSortValue().equals(SearchPreferences.SORT_BY_FAVORITES)) {
                setupViewModel();

            }else {
                //get saved adapter movie list
                List<MovieData> restoredMovieDataList = Parcels.unwrap
                        (savedInstanceState.getParcelable(MOVIE_DATA_LIST_KEY));

                //set adapter data
                searchAdapter.addAdapterData(restoredMovieDataList);
            }
        }
    }

    private void customizeActionBar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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
                if (searchPreferences.getSortValue().equals(SearchPreferences.SORT_BY_FAVORITES)) {
                    return;
                }

                int currentPage = searchPreferences.getCurrentPage();
                int availablePages = searchPreferences.getTotalPages();

                int totalNumOfItems = searchAdapter.getItemCount();
                int lastItemIndex = totalNumOfItems - 1;

                @SuppressWarnings("ConstantConditions")
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
        searchAdapter = new SearchAdapter(this);

        //set adapter to RecyclerView
        mSearchPosterRecyclerView.setAdapter(searchAdapter);

        //define search box
        searchTextView = findViewById(R.id.search_text_view);

        //configure search box action event
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //set sort parameter
                //note: setting parameter here is preferred instead of "on show search button" clicked
                searchPreferences.setSortParameter(SearchPreferences.SEARCH);

                hideSoftKeyboard(searchTextView);

                //set focus to top layout(away from search box)
                findViewById(R.id.topLayout).requestFocus();

                executeMovieSearch(true);
                return true;
            }
        });
    }

    private void executeMovieSearch(boolean newSearch) {
        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append

        if (newSearch) {
            searchAdapter.clearData();
            searchPreferences.setTargetPage(1);
        }

        //if sort value is "search"
        if (searchPreferences.getSortValue().equals(SearchPreferences.SEARCH)) {
            querySearch(searchTextView.getText().toString());
        } else {
            presetMovieSearch();
        }
    }

    private void loadNextPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executeMovieSearch(false);
    }

    void loadPreviousPage(int newTargetPage) {
        searchPreferences.setTargetPage(newTargetPage);
        executeMovieSearch(false);
    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.search_column_span));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    public void onItemClick(int position) {
        //start detail activity
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.POSITION_KEY, position);

        MovieData currentMovieData = searchAdapter.getMovieData(position);
        Parcelable parceledMovie = Parcels.wrap(currentMovieData);
        Parcelable parceledPreferences = Parcels.wrap(searchPreferences);

        detailIntent.putExtra(SELECTED_MOVIE_KEY, parceledMovie);
        detailIntent.putExtra(SEARCH_PREFERENCES_KEY, parceledPreferences);

        startActivity(detailIntent);
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
                //hide search box
                searchTextView.setVisibility(View.GONE);
                //clear text
                searchTextView.setText("");

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
                //hide search box
                searchTextView.setVisibility(View.GONE);
                //clear text
                searchTextView.setText("");

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
                //hide search box
                searchTextView.setVisibility(View.GONE);
                //clear text
                searchTextView.setText("");

                //SHOW FAVORITES
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_FAVORITES);
                //setup view model
                setupViewModel();
                return true;
            }
        });

        //get menu item
        MenuItem searchItem = menu.findItem(R.id.search);
        //set click listener
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //show search box
                searchTextView.setVisibility(View.VISIBLE);
                //show keyboard
                showSoftKeyboard(searchTextView);

                return true;
            }
        });

        return true;
    }

    private void setupViewModel() {
        SearchViewModel viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
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
                        if (movieData.size() == 0){
                            //show empty favorites message
                            Snackbar.make(findViewById(R.id.topLayout),
                                    getString(R.string.search_no_favorites),
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            searchAdapter.addAdapterData(movieData);
                        }
                    }
                }
            }
        });
    }

    public void querySearch(String queryString) {

        //build AN request
        final ANRequest request
                = NetworkFunctions.buildSearchMovieRequest(searchPreferences, queryString);

        request.getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String returnedJSONString) {
                try {
                    //set current url string path
                    searchPreferences.setQueryUrlString(request.getUrl());

                    //parse and populate retrieved data
                    List<MovieData> movieDataList = MovieDataParser.parseData(returnedJSONString,
                            SearchActivity.this, searchPreferences);

                    searchAdapter.addAdapterData(movieDataList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                //notify error
                Snackbar.make(findViewById(R.id.topLayout),
                        anError.getErrorDetail(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void presetMovieSearch() {

        //build AN request
        final ANRequest request = NetworkFunctions.buildPresetMovieSearchRequest(searchPreferences);

        request.getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String returnedJSONString) {
                try {
                    //set current url string path
                    searchPreferences.setQueryUrlString(request.getUrl());

                    //parse and populate retrieved data
                    List<MovieData> movieDataList = MovieDataParser.parseData(returnedJSONString,
                            SearchActivity.this, searchPreferences);

                    searchAdapter.addAdapterData(movieDataList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                //notify error
                Snackbar.make(findViewById(R.id.topLayout),
                        anError.getErrorDetail(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SEARCH_PREFERENCES_KEY, Parcels.wrap(searchPreferences));
        outState.putParcelable(MOVIE_DATA_LIST_KEY, Parcels.wrap(searchAdapter.getMovieDataList()));

        //save query string
        outState.putString(QUERY_STRING_KEY, searchTextView.getText().toString());

        //save keyboard visibility state
        if (searchTextView.getVisibility() == View.VISIBLE) {
            outState.putBoolean(SHOW_SEARCH_BOX_KEY, true);

        } else {
            outState.putBoolean(SHOW_SEARCH_BOX_KEY, false);
        }
    }
}
