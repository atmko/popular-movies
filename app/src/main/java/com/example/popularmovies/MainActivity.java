package com.example.popularmovies;

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

public class MainActivity extends AppCompatActivity implements SearchAdapter.OnListItemClickListener{

    static SearchPreferences searchPreferences;
    static SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---configure recyclerVIew
        RecyclerView mSearchPosterRecyclerView = findViewById(R.id.searchPosterRecyclerView);
        mSearchPosterRecyclerView.setHasFixedSize(true);
        mSearchPosterRecyclerView.setLayoutManager(configureLayoutManager());//configureLayoutManager returns a LayoutManager

        //--configure load on scroll
        mSearchPosterRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentPage = searchPreferences.getCurrentPage();
                int availablePages = searchPreferences.getTotalPages();

                int totalNumOfItems = searchAdapter.getItemCount();
                int lastItemIndex = totalNumOfItems - 1;

                //catches possible NullPointerException when invoking (GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()
                try {
                    int lastShown = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    boolean lastItem = lastShown == lastItemIndex;//lastVis == lastItemIndex makes sure we are at the end of list
                    boolean morePagesAvailable = currentPage < availablePages;

                    //if at lastItem && if morePagesAvailable
                    if (lastItem && morePagesAvailable) {
                        //load next page
                        searchPreferences.loadNextPage();
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        });
        //define and set adapter
        searchAdapter = new SearchAdapter(this);
        mSearchPosterRecyclerView.setAdapter(searchAdapter);

        //create searchPreferences with api key and execute search
        String apiKey = getString(R.string.api_key);
        searchPreferences = new SearchPreferences(apiKey);//takes api key
        searchPreferences.executeMovieSearch(true);
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
        MenuItem popularityItem = menu.findItem(R.id.sort_by_popularity);
        popularityItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {//set click listener
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //SET SORT BY POPULARITY
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_POPULARITY);
                //execute search
                searchPreferences.executeMovieSearch(true);
                return true;

            }
        });
        //get menu item
        MenuItem votesItem = menu.findItem(R.id.sort_by_vote);
        votesItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {//set click listener
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //SET SORT BY POPULARITY
                searchPreferences.setSortParameter(SearchPreferences.SORT_BY_VOTE);
                //execute search
                searchPreferences.executeMovieSearch(true);
                return true;
            }
        });

        return true;
    }


}
