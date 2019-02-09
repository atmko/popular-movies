package com.upkipp.popularmovies;

import android.content.Context;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

public final class SearchPreferences {

    private Context mContext;

    //urls & paths
    private String mQueryUrlString;//final url used by MovieLoader
    private final String PRESET_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    //image values
    public static final String POSTER_IMAGE_SIZE = "w185";
    public static final String BACKDROP_IMAGE_SIZE = "w780";

    private final String mApiKey;
    private static final String API_PARAM_KEY = "api_key";
    private static final String API_QUERY_FORMAT = "?" + API_PARAM_KEY + "=";
    private static final String API_PLACEHOLDER_FORMAT = "{"+ API_PARAM_KEY +"}";

    //preset parameters---------------------------------------------------------------
    private String mPresetApiUrl;

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
    private static  final String PAGE_PLACEHOLDER_FORMAT = "{"+ PAGE_PARAM_KEY +"}";
    private int mCurrentPage;
    private int mTotalPages;

    //language parameters
    private String mLanguageVal;
    private static final String LANGUAGE_PARAM_KEY = "language";
    private static final String LANGUAGE_QUERY_FORMAT = "&" + LANGUAGE_PARAM_KEY + "=";
    private static final String LANGUAGE_PLACEHOLDER_FORMAT = "{"+ LANGUAGE_PARAM_KEY +"}";
    static final String ENG_US = "en-US";

    SearchPreferences(String apiKey, Context context) {
        //presets config
        mPresetParamVal = POPULAR_PRESET;
        //general//
        mApiKey = apiKey;
        mContext = context;
        mPageParamVal = 1;
        mLanguageVal = ENG_US;
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

    public String getQueryUrlString() {
        return mQueryUrlString;
    }

    void setPresetParameter(String preset) {
        mPresetParamVal = preset;
        mPageParamVal = 1;
    }

    void executePresetMovieSearch(boolean newSearch) {
        //newSearch value indicates if to overwrite Adapter Data
        //false value used in paging to not overwrite, but append
        if (newSearch) {
            MainActivity.searchAdapter.clearData();
        }

        ANRequest request = AndroidNetworking.get(
                PRESET_BASE_URL +
                PRESET_PLACEHOLDER_FORMAT +
                API_QUERY_FORMAT + API_PLACEHOLDER_FORMAT +
                LANGUAGE_QUERY_FORMAT + LANGUAGE_PLACEHOLDER_FORMAT +
                PAGE_QUERY_FORMAT + PAGE_PLACEHOLDER_FORMAT)

                .addPathParameter(PRESET_PARAM_KEY, mPresetParamVal)
                .addQueryParameter(LANGUAGE_PARAM_KEY, mLanguageVal)
                .addQueryParameter(PAGE_PARAM_KEY, String.valueOf(mPageParamVal))
                .addQueryParameter(API_PARAM_KEY, mApiKey)
                .build();

        mQueryUrlString = request.getUrl();

        request.getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String returnedJSONString) {
                        MovieDataParser.parseData(returnedJSONString);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(mContext, anError.getErrorDetail(), Toast.LENGTH_SHORT).show();

                    }
        });

    }

    void loadNextPage() {
        mPageParamVal++;
        executePresetMovieSearch(false);
    }

}
