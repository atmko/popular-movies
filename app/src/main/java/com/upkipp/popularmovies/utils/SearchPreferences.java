/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.utils;

import org.parceler.Parcel;

@Parcel
public final class SearchPreferences {
    //urls & paths
    private static String mQueryUrlString;//final url used by MovieLoader

    //sort parameters---------------------------------------------------------------
    String mSortParamVal;
    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_FAVORITES = "favorites";

    //---------------------------------------------------------
    //paging parameters
    int mPageParamVal = 1;//the target/desired page and not necessarily the current page
    int mCurrentPage;
    int mTotalPages;

    //language parameters
    String mLanguageParamVal;
    public static final String ENG_US = "en-US";

    //---------------------------------------------------------

    //constructor for parceler
    public SearchPreferences() {

    }

    public SearchPreferences(String defaultSort, String defaultLanguage) {
        //sort config
        mSortParamVal = defaultSort;
        //general//
        mLanguageParamVal = defaultLanguage;
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
