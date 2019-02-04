package com.upkipp.popularmovies;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public final class SearchPreferences {

    private final String mApiKey;

    //urls & paths
    private URL mQueryUrl;//final url used by MovieLoader
    private final String PRESET_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    //image values
    public static final String POSTER_IMAGE_SIZE = "w185";
    public static final String BACKDROP_IMAGE_SIZE = "w780";

    //preset parameters---------------------------------------------------------------
    private String mPresetApiUrl;

    private String mPresetParamVal;
    static final String POPULAR_PRESET = "popular";
    static final String TOP_RATED_PRESET = "top_rated";
    //---------------------------------------------------------

    //paging parameters
    private final String PAGE_PARAM_KEY = "page";
    private int mPageParamVal;//the target/desired page and not necessarily the current page
    private int mCurrentPage;
    private int mTotalPages;

    //language parameters
    private String mLanguageVal;
    private final String LANGUAGE_PARAM_KEY = "language";
    static final String ENG_US = "en-US";

    SearchPreferences(String apiKey) {
        //presets config
        mPresetParamVal = POPULAR_PRESET;
        mPresetApiUrl =  PRESET_BASE_URL + mPresetParamVal + "?api_key=" + apiKey;
        //general//
        mApiKey = apiKey;
        mPageParamVal = 1;
        mLanguageVal = ENG_US;
    }

    void setPresetParameter(String preset) {
        mPresetParamVal = preset;
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

    public URL getQueryUrl() {
        return mQueryUrl;
    }

    private void buildPresetUrl() {
        mPresetApiUrl = PRESET_BASE_URL + mPresetParamVal + "?api_key=" + mApiKey;

        Uri builtUri = Uri.parse(mPresetApiUrl).buildUpon()
                .appendQueryParameter(LANGUAGE_PARAM_KEY, mLanguageVal)
                .appendQueryParameter(PAGE_PARAM_KEY, String.valueOf(mPageParamVal))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mQueryUrl = url;
    }

    void executePresetMovieSearch(boolean newSearch) {
        buildPresetUrl();

        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append
        if (newSearch) {
            MainActivity.searchAdapter.clearData();
        }
        MovieLoader movieLoader = new MovieLoader();
        movieLoader.execute(mQueryUrl);
    }

    void loadNextPage() {
        mPageParamVal++;
        executePresetMovieSearch(false);
    }

}
