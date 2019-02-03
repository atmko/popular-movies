package com.example.popularmovies;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public final class SearchPreferences {

    //urls & paths
    private URL mQueryURL;//final url used by MovieLoader
    private final String mApiURL;
    private final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    //image size values
    public static final String POSTER_IMAGE_SIZE = "w185";
    public static final String BACKDROP_IMAGE_SIZE = "w780";

    //sort parameters
    private static final String SORT_PARAM_KEY = "sort_by";
    private String mSortParamVal;

    static final String SORT_BY_POPULARITY = "popularity.desc";
    static final String SORT_BY_VOTE = "vote_count.desc";

    //paging parameters
    private final String PAGE_PARAM_KEY = "page";
    private int mPageParamVal;//the target/desired page and not necessarily the current page
    private int mCurrentPage;
    private int mTotalPages;

    SearchPreferences(String apiKey) {
        mApiURL = BASE_URL + apiKey;
        mSortParamVal = SORT_BY_POPULARITY;
        mPageParamVal = 1;
    }

    void setSortParameter(String sort) {
        mSortParamVal = sort;
        mPageParamVal = 1;
    }

    int getTotalPages() {
        return mTotalPages;
    }

    void setTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    int getTargetPage() {
        return mPageParamVal;
    }

    int getCurrentPage() {
        return mCurrentPage;
    }

    void setCurrentPage(int pageNum) {
        mCurrentPage = pageNum;
    }

    public URL getQueryURL() {
        return mQueryURL;
    }

    private void buildQueryUrl() {
        Uri builtUri = Uri.parse(mApiURL).buildUpon()
                .appendQueryParameter(SORT_PARAM_KEY, mSortParamVal)
                .appendQueryParameter(PAGE_PARAM_KEY, String.valueOf(mPageParamVal))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mQueryURL = url;
    }

    void executeMovieSearch(boolean newSearch) {
        buildQueryUrl();

        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append
        if (newSearch) {
            MainActivity.searchAdapter.clearData();
        }
        MovieLoader movieLoader = new MovieLoader();
        movieLoader.execute(mQueryURL);
    }

    void loadNextPage() {
        mPageParamVal++;
        executeMovieSearch(false);
    }
}
