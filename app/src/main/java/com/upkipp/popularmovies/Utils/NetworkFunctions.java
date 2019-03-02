package com.upkipp.popularmovies.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public final class NetworkFunctions {

    private static final String MOVIE_PARAM_KEY = "movie_id";

    public static ANRequest buildSearchRequest() {
        SearchPreferences searchPreferences = SearchPreferences.getInstance();
        //build request using Fast Android Networking
        //format: "https://{base_url}/{sort}{api_query_key}{api_key_val}{lang_query_key}{lang_val}{page_query}{page_val}"
        ANRequest request = AndroidNetworking.get(
                SearchPreferences.PRESET_BASE_URL +
                        SearchPreferences.SORT_PLACEHOLDER_FORMAT +
                        SearchPreferences.API_QUERY_FORMAT + SearchPreferences.API_PLACEHOLDER_FORMAT +
                        SearchPreferences.LANGUAGE_QUERY_FORMAT + SearchPreferences.LANGUAGE_PLACEHOLDER_FORMAT +
                        SearchPreferences.PAGE_QUERY_FORMAT + SearchPreferences.PAGE_PLACEHOLDER_FORMAT)

                .addPathParameter(SearchPreferences.SORT_PARAM_KEY, searchPreferences.getSortValue())
                .addQueryParameter(SearchPreferences.API_PARAM_KEY, SearchPreferences.API_KEY)
                .addQueryParameter(SearchPreferences.LANGUAGE_PARAM_KEY, searchPreferences.getLanguageValue())
                .addQueryParameter(SearchPreferences.PAGE_PARAM_KEY, String.valueOf(searchPreferences.getTargetPage()))
                .build();

        return request;
    }

    //------------------------------------------------------------
    private static String formatANNKey(String key) {
        return "{" + key + "}";
    }

    //------------------------------------------------------------

    //loads images into ImageViews using glide
    public static void loadImage(Context context, String urlString, ImageView imageView) {
        //configure glide behaviour
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert);

        Glide.with(context)
                .load(urlString)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(imageView);
    }

    public static ANRequest loadVideos(String movieId) {
        String videoUrl = SearchPreferences.PRESET_BASE_URL + formatANNKey(MOVIE_PARAM_KEY) + "/videos" +
                SearchPreferences.API_QUERY_FORMAT + SearchPreferences.API_PLACEHOLDER_FORMAT;

        //build request using Fast Android Networking
        //format: "https://{base_url}/{movie_id}/videos"
        ANRequest request = AndroidNetworking.get(videoUrl)
                .addPathParameter(MOVIE_PARAM_KEY, movieId)
                .addQueryParameter(SearchPreferences.API_PARAM_KEY, SearchPreferences.API_KEY)
                .build();

        return request;

    }

    public static ANRequest loadReviews(String movieId) {
        String videoUrl = SearchPreferences.PRESET_BASE_URL + formatANNKey(MOVIE_PARAM_KEY) + "/reviews" +
                SearchPreferences.API_QUERY_FORMAT + SearchPreferences.API_PLACEHOLDER_FORMAT;

        //build request using Fast Android Networking
        //format: "https://{base_url}/{movie_id}/reviews"
        ANRequest request = AndroidNetworking.get(videoUrl)
                .addPathParameter(MOVIE_PARAM_KEY, movieId)
                .addQueryParameter(SearchPreferences.API_PARAM_KEY, SearchPreferences.API_KEY)
                .build();

        return request;

    }
}
