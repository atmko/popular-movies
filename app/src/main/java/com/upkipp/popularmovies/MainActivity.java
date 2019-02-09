package com.upkipp.popularmovies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public final class MainActivity extends AppCompatActivity
        implements SearchAdapter.OnListItemClickListener{

    static SearchPreferences searchPreferences;
    static SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                //!emptyAdapter prevents unwanted page load when clearing adapter data because lastItem is considered true
                boolean emptyAdapter = searchAdapter.isEmpty();

                //if at lastItem && if morePagesAvailable && if adapter not empty
                if (lastItem && morePagesAvailable && !emptyAdapter) {
                    //load next page
                    int targetPage = searchPreferences.getTargetPage();
                    searchPreferences.loadNextPage();

                }
            }

        });
        //define and set adapter
        searchAdapter = new SearchAdapter(this);
        mSearchPosterRecyclerView.setAdapter(searchAdapter);

        //create searchPreferences with api key and execute search
        String apiKey = BuildConfig.apiKey;
        searchPreferences = new SearchPreferences(apiKey, this);//takes api key and context
        searchPreferences.executePresetMovieSearch(true);//defaults to popular movies
    }

    private GridLayoutManager configureLayoutManager() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    public void onItemClick(int position) {
        //start detail activity
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        detailActivityIntent.putExtra("index", position);
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
                searchPreferences.setPresetParameter(SearchPreferences.POPULAR_PRESET);
                //execute search
                searchPreferences.executePresetMovieSearch(true);
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
                searchPreferences.setPresetParameter(SearchPreferences.TOP_RATED_PRESET);
                //execute search
                searchPreferences.executePresetMovieSearch(true);
                return true;
            }
        });

        return true;
    }

}
