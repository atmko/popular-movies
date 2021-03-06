/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.utils.network_utils;

import android.content.Context;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.SearchPreferences;

@SuppressWarnings("UnnecessaryLocalVariable")
public final class NetworkFunctions {
    //preset movie search request
    public static ANRequest buildPresetMovieSearchRequest(SearchPreferences searchPreferences) {
        //build request using Fast Android Networking
        //format: "https://{base_url}/{sort}{api_query_key}{api_key_val}{lang_query_key}{lang_val}{page_query}{page_val}"
        ANRequest request = AndroidNetworking.get(ApiConstants.SEARCH_FORMAT)
                .addPathParameter(ApiConstants.SORT_KEY, searchPreferences.getSortValue())
                .addQueryParameter(ApiConstants.API_KEY_KEY, ApiConstants.API_KEY)
                .addQueryParameter(ApiConstants.LANG_KEY, searchPreferences.getLanguageValue())
                .addQueryParameter(ApiConstants.PAGE_KEY, String.valueOf(searchPreferences.getTargetPage()))
                .build();

        return request;
    }

    //movie search request
    public static ANRequest buildSearchMovieRequest(SearchPreferences searchPreferences, String queryString) {
        //build request using Fast Android Networking
        ANRequest request = AndroidNetworking.get(ApiConstants.MOVIE_SEARCH_FORMAT)
                .addQueryParameter(ApiConstants.API_KEY_KEY, ApiConstants.API_KEY)
                .addQueryParameter(ApiConstants.LANG_KEY, searchPreferences.getLanguageValue())
                .addQueryParameter(ApiConstants.QUERY_KEY, queryString)
                .addQueryParameter(ApiConstants.PAGE_KEY, String.valueOf(searchPreferences.getTargetPage()))
                .build();

        return request;
    }

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

    public static ANRequest loadVideos(String movieId, SearchPreferences searchPreferences) {
//        String videoUrl = SearchPreferences.PRESET_BASE_URL + formatANNKey(MOVIE_PARAM_KEY) + "/videos" +
//                SearchPreferences.API_QUERY_FORMAT + SearchPreferences.API_PLACEHOLDER_FORMAT;

        //build request using Fast Android Networking
        //format: "https://{base_url}/{movie_id}/videos"
        ANRequest request = AndroidNetworking.get(ApiConstants.VIDEOS_URL_FORMAT)
                .addPathParameter(ApiConstants.MOVIE_ID_KEY, movieId)
                .addPathParameter(ApiConstants.API_KEY_KEY, ApiConstants.API_KEY)
                .addPathParameter(ApiConstants.LANG_KEY,
                        searchPreferences.getLanguageValue())
                .build();

        return request;

    }

    public static ANRequest loadReviews(String movieId, SearchPreferences searchPreferences) {
//        String reviewUrl = SearchPreferences.PRESET_BASE_URL + formatANNKey(MOVIE_PARAM_KEY) + "/reviews" +
//                SearchPreferences.API_QUERY_FORMAT + SearchPreferences.API_PLACEHOLDER_FORMAT;

        //build request using Fast Android Networking
        //format: "https://{base_url}/{movie_id}/reviews"
        ANRequest request = AndroidNetworking.get(ApiConstants.REVIEWS_URL_FORMAT)
                .addPathParameter(ApiConstants.MOVIE_ID_KEY, movieId)
                .addPathParameter(ApiConstants.API_KEY_KEY, ApiConstants.API_KEY)
                .addPathParameter(ApiConstants.LANG_KEY,
                        searchPreferences.getLanguageValue())
                .build();

        return request;

    }

    public static String getMovieUrl(Context context, String movieId) {
        return context.getString(R.string.movie_base_url) + "/" + movieId;
    }
}
