package com.upkipp.popularmovies.Utils;

import com.upkipp.popularmovies.BuildConfig;

public final class SearchPreferences {
    //singleton variables
    private static final Object LOCK = new Object();
    private static SearchPreferences sInstance;

    //api key
    public static final String API_KEY = BuildConfig.apiKey;

    //urls & paths
    private static String mQueryUrlString;//final url used by MovieLoader
    public static final String PRESET_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    //image values
    public static final String POSTER_IMAGE_SIZE = "w185";
    public static final String BACKDROP_IMAGE_SIZE = "w780";

    public static final String API_PARAM_KEY = "api_key";
    public static final String API_QUERY_FORMAT = "?" + API_PARAM_KEY + "=";
    public static final String API_PLACEHOLDER_FORMAT = "{"+ API_PARAM_KEY +"}";

    //sort parameters---------------------------------------------------------------
    private String mSortParamVal;
    public static final String SORT_PARAM_KEY = "sort";
    public static final String SORT_PLACEHOLDER_FORMAT = "{"+ SORT_PARAM_KEY +"}";
    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";

    //---------------------------------------------------------
    //paging parameters
    private int mPageParamVal = 1;//the target/desired page and not necessarily the current page
    public static final String PAGE_PARAM_KEY = "page";
    public static final String PAGE_QUERY_FORMAT = "&" + PAGE_PARAM_KEY + "=";
    public static final String PAGE_PLACEHOLDER_FORMAT = "{"+ PAGE_PARAM_KEY +"}";
    private int mCurrentPage;
    private int mTotalPages;

    //language parameters
    private String mLanguageParamVal;
    public static final String LANGUAGE_PARAM_KEY = "language";
    public static final String LANGUAGE_QUERY_FORMAT = "&" + LANGUAGE_PARAM_KEY + "=";
    public static final String LANGUAGE_PLACEHOLDER_FORMAT = "{"+ LANGUAGE_PARAM_KEY +"}";
    private static final String ENG_US = "en-US";

    //---------------------------------------------------------
    private SearchPreferences(String defaultSort, String defaultLanguage) {
        //sort config
        mSortParamVal = defaultSort;
        //general//
        mLanguageParamVal = defaultLanguage;
    }

    public static SearchPreferences getInstance() {
    if (sInstance == null) {
        synchronized (LOCK) {
            //use shared preferences for these values
            sInstance = new SearchPreferences(SORT_BY_POPULAR, ENG_US);

        }
    }

        return sInstance;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    void setTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    public String getSortValue() {
        return mSortParamVal;
    }

    public int getTargetPage() {
        return mPageParamVal;
    }

    public void setTargetPage(int page) {
        this.mPageParamVal = page;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    void setCurrentPage(int pageNum) {
        mCurrentPage = pageNum;
    }

    String getLanguageValue() {
        return mLanguageParamVal;
    }

    String getQueryUrlString() {
        return mQueryUrlString;
    }

    public void setQueryUrlString(String urlString) {
        mQueryUrlString = urlString;
    }

    public void setSortParameter(String sortValue) {
        mSortParamVal = sortValue;
        mPageParamVal = 1;
    }

}
