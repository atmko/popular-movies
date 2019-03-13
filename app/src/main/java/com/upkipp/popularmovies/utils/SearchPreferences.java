package com.upkipp.popularmovies.utils;

public final class SearchPreferences {
    //singleton variables
    private static final Object LOCK = new Object();
    private static SearchPreferences sInstance;

    //urls & paths
    private static String mQueryUrlString;//final url used by MovieLoader
    public static final String PRESET_BASE_URL = "https://api.themoviedb.org/3/movie/";

    //sort parameters---------------------------------------------------------------
    private String mSortParamVal;
    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_FAVORITES = "favorites";

    //---------------------------------------------------------
    //paging parameters
    private int mPageParamVal = 1;//the target/desired page and not necessarily the current page
    private int mCurrentPage;
    private int mTotalPages;

    //language parameters
    private String mLanguageParamVal;
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

    public static void resetPreferences() {
        sInstance = null;
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

    public String getLanguageValue() {
        return mLanguageParamVal;
    }

    public String getQueryUrlString() {
        return mQueryUrlString;
    }

    public void setQueryUrlString(String urlString) {
        mQueryUrlString = urlString;
    }

    public void setSortParameter(String sortValue) {
        mSortParamVal = sortValue;
    }

}
