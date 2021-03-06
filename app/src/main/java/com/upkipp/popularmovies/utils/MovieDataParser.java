/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.google.gson.Gson;
import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;

import org.json.JSONException;

public class MovieDataParser {
    //check for int/double errors
    private static String checkAndConvertNumber(Object number) {
        return String.valueOf(number);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static List<MovieData> parseData(String returnedJSONString, Context context,
                                            SearchPreferences searchPreferences) throws JSONException {

        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Map returnedMap = gson.fromJson(returnedJSONString, Map.class);

        //set total pages available to searchPreferences
        //set current page in searchPreferences
        //note: GSON number format default is double
        Double totalPages = (double) returnedMap.get(MovieData.MovieDataKeys.TOTAL_PAGES_KEY);
        Double currentPage = (double) returnedMap.get(MovieData.MovieDataKeys.CURRENT_PAGE_KEY);

        searchPreferences.setTotalPages(totalPages.intValue());
        searchPreferences.setCurrentPage(currentPage.intValue());

        //use RESULTS_KEY to get results as JSONArray
        ArrayList results = (ArrayList) returnedMap.get(MovieData.MovieDataKeys.RESULTS_KEY);

        //SearchAdapter data will be stored as ArrayList<MovieData>
        List<MovieData> movieDataList = new ArrayList<>();

        //iterate through each movie in results
        for (int index = 0; index < results.size() ; index++) {
            Map currentObject = (Map) results.get(index);//get current movie

            //create new MovieData from currentObject
            MovieData movieData =
                    new MovieData(
                            //get by keys
                            checkAndConvertNumber(currentObject.get(MovieData.MovieDataKeys.MOVIE_ID)),

                            checkAndConvertNumber(currentObject.get(MovieData.MovieDataKeys.VOTE_COUNT)),

                            (Boolean) currentObject.get(MovieData.MovieDataKeys.VIDEO),

                            checkAndConvertNumber(currentObject.get(MovieData.MovieDataKeys.VOTE_AVERAGE)),

                            (String) currentObject.get(MovieData.MovieDataKeys.MOVIE_TITLE),

                            (Double) currentObject.get(MovieData.MovieDataKeys.POPULARITY),

                            (String) currentObject.get(MovieData.MovieDataKeys.POSTER_PATH),

                            (String) currentObject.get(MovieData.MovieDataKeys.ORIG_LANG),

                            (String) currentObject.get(MovieData.MovieDataKeys.ORIG_TITLE),

                            //GSON numbers default to doubles
                            convertGenreIdsToIntegers(
                                    (List<Double>) currentObject.get(MovieData.MovieDataKeys.GENRE_IDS)),

                            (String) currentObject.get(MovieData.MovieDataKeys.BACKDROP_PATH),

                            (Boolean) currentObject.get(MovieData.MovieDataKeys.ADULT),

                            (String) currentObject.get(MovieData.MovieDataKeys.OVERVIEW),

                            (String) currentObject.get(MovieData.MovieDataKeys.RELEASE_DATE),

                            context
                    );

            movieDataList.add(movieData);

        }

        return movieDataList;
    }

    private static List<Integer> convertGenreIdsToIntegers(List<Double> genreDoubleIds) {
        List<Integer> genreIntegerIds = new ArrayList<>();

        for (Double genreId : genreDoubleIds) {
            genreIntegerIds.add(genreId.intValue());
        }

        return genreIntegerIds;
    }

    @SuppressWarnings("ConstantConditions")
    public static ArrayList<Map<String, String>> parseReviews(String returnedJSONString) {
        //review data will be stored as Map<String, ArrayList<String>>
        ArrayList<Map<String, String>> reviews = new ArrayList<>();
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return reviews;
        }

        Gson gson = new Gson();
        Map returnedMap = gson.fromJson(returnedJSONString, Map.class);

        //use RESULTS_KEY to get results as JSONArray
        ArrayList results = (ArrayList) returnedMap.get(MovieData.MovieDataKeys.RESULTS_KEY);

//        iterate through each review in results
        for (int index = 0; index < results.size() ; index++) {
            Map currentResult = (Map) results.get(index);//get current review

            Map<String, String> newReview = new HashMap<>();

            newReview.put(ApiConstants.REVIEW_AUTHOR_KEY,
                    ((String) currentResult.get(ApiConstants.REVIEW_AUTHOR_KEY)));

            newReview.put(ApiConstants.REVIEW_CONTENT_KEY,
                    (String) currentResult.get(ApiConstants.REVIEW_CONTENT_KEY));

            reviews.add(newReview);

        }

        return reviews;

    }

    @SuppressWarnings("ConstantConditions")
    public static ArrayList<Map<String, String>> parseVideos(String returnedJSONString) {
        //video data will be stored as Map<String, ArrayList<String>>
        ArrayList<Map<String, String>> videos = new ArrayList<>();
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return videos;
        }

        Gson gson = new Gson();
        Map returnedMap = gson.fromJson(returnedJSONString, Map.class);

        //use RESULTS_KEY to get results as JSONArray
        ArrayList results = (ArrayList) returnedMap.get(MovieData.MovieDataKeys.RESULTS_KEY);

//        iterate through each video in results
        assert results != null;//null checking occurs in above if statement
        for (int index = 0; index < results.size() ; index++) {
            Map currentResult = (Map) results.get(index);//get current review

            Map<String, String> newVideo = new HashMap<>();

            newVideo.put(ApiConstants.VIDEO_PATH_KEY,
                    (String) currentResult.get(ApiConstants.VIDEO_PATH_KEY));

            newVideo.put(ApiConstants.VIDEO_SITE_KEY,
                    (String) currentResult.get(ApiConstants.VIDEO_SITE_KEY));

            newVideo.put(ApiConstants.VIDEO_TYPE_KEY,
                    (String) currentResult.get(ApiConstants.VIDEO_TYPE_KEY));

            newVideo.put(ApiConstants.VIDEO_NAME_KEY,
                    (String) currentResult.get(ApiConstants.VIDEO_NAME_KEY));

            videos.add(newVideo);

        }

        return videos;
    }

    public static String createVideoImagePath(String videoPath) {

        ANRequest request = AndroidNetworking.get(ApiConstants.VIDEO_IMAGE_URL_FORMAT)
                .addPathParameter(ApiConstants.VIDEO_IMG_KEY, videoPath)
                .build();

        return request.getUrl();

    }
}
