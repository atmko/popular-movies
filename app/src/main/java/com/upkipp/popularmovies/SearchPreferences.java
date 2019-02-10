package com.upkipp.popularmovies;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;

import static com.upkipp.popularmovies.MainActivity.searchPreferences;

public final class SearchPreferences {
    //api key
    private static final String API_KEY = BuildConfig.apiKey;

    //urls & paths
    private static String mQueryUrlString;//final url used by MovieLoader
    private static final String PRESET_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    //image values
    public static final String POSTER_IMAGE_SIZE = "w185";
    public static final String BACKDROP_IMAGE_SIZE = "w780";

    private static final String API_PARAM_KEY = "api_key";
    private static final String API_QUERY_FORMAT = "?" + API_PARAM_KEY + "=";
    private static final String API_PLACEHOLDER_FORMAT = "{"+ API_PARAM_KEY +"}";

    //preset parameters---------------------------------------------------------------
    private String mPresetParamVal;
    private static final String PRESET_PARAM_KEY = "preset";
    private static final String PRESET_PLACEHOLDER_FORMAT = "{"+ PRESET_PARAM_KEY +"}";
    static final String POPULAR_PRESET = "popular";
    static final String TOP_RATED_PRESET = "top_rated";

    //---------------------------------------------------------
    //paging parameters
    private int mPageParamVal;//the target/desired page and not necessarily the current page
    private static final String PAGE_PARAM_KEY = "page";
    private static final String PAGE_QUERY_FORMAT = "&" + PAGE_PARAM_KEY + "=";
    private static final String PAGE_PLACEHOLDER_FORMAT = "{"+ PAGE_PARAM_KEY +"}";
    private int mCurrentPage;
    private int mTotalPages;

    //language parameters
    private String mLanguageParamVal;
    private static final String LANGUAGE_PARAM_KEY = "language";
    private static final String LANGUAGE_QUERY_FORMAT = "&" + LANGUAGE_PARAM_KEY + "=";
    private static final String LANGUAGE_PLACEHOLDER_FORMAT = "{"+ LANGUAGE_PARAM_KEY +"}";
    private static final String ENG_US = "en-US";

    //---------------------------------------------------------
    SearchPreferences() {
        //presets config
        mPresetParamVal = POPULAR_PRESET;
        //general//
        mPageParamVal = 1;
        mLanguageParamVal = ENG_US;
    }

    int getTotalPages() {
        return mTotalPages;
    }

    void setTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    private String getPresetValue() {
        return mPresetParamVal;
    }

    private int getTargetPage() {
        return mPageParamVal;
    }

    void setTargetPage(int page) {
        this.mPageParamVal = page;
    }

    int getCurrentPage() {
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

    static void setQueryUrlString(String urlString) {
        mQueryUrlString = urlString;
    }

    void setPresetParameter(String preset) {
        mPresetParamVal = preset;
        mPageParamVal = 1;
    }

    ANRequest buildRequest() {
        //build request using Fast Android Networking
        //format: "https://{base_url}/{preset}{api_query_key}{api_key_val}{lang_query_key}{lang_val}{page_query}{page_val}"
        ANRequest request = AndroidNetworking.get(
                PRESET_BASE_URL +
                        PRESET_PLACEHOLDER_FORMAT +
                        API_QUERY_FORMAT + API_PLACEHOLDER_FORMAT +
                        LANGUAGE_QUERY_FORMAT + LANGUAGE_PLACEHOLDER_FORMAT +
                        PAGE_QUERY_FORMAT + PAGE_PLACEHOLDER_FORMAT)

                .addPathParameter(PRESET_PARAM_KEY, searchPreferences.getPresetValue())
                .addQueryParameter(LANGUAGE_PARAM_KEY, searchPreferences.getLanguageValue())
                .addQueryParameter(PAGE_PARAM_KEY, String.valueOf(searchPreferences.getTargetPage()))
                .addQueryParameter(API_PARAM_KEY, API_KEY)
                .build();

        return request;
    }

}
