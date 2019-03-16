package com.upkipp.popularmovies.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.google.gson.Gson;
import com.upkipp.popularmovies.adapters.ReviewAdapter;
import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;

import org.json.JSONException;

public class MovieDataParser {
    //check for int/double errors
    private static String checkAndConvertNumber(Object number){
        if (Double.parseDouble(number.toString()) == MovieData.ErrorValues.DOUBLE_ERROR) {
            return "{error}";
        } else {
            return String.valueOf(number);
        }
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static List<MovieData> parseData(String returnedJSONString) throws JSONException {
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Map returnedMap = gson.fromJson(returnedJSONString, Map.class);

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

                            (List<Integer>) currentObject.get(MovieData.MovieDataKeys.GENRE_IDS),

                            (String) currentObject.get(MovieData.MovieDataKeys.BACKDROP_PATH),

                            (Boolean) currentObject.get(MovieData.MovieDataKeys.ADULT),

                            (String) currentObject.get(MovieData.MovieDataKeys.OVERVIEW),

                            (String) currentObject.get(MovieData.MovieDataKeys.RELEASE_DATE)
                    );

            movieDataList.add(movieData);

        }

        return movieDataList;
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

            newReview.put(ReviewAdapter.REVIEW_AUTHOR_KEY, ((String) currentResult.get("author")));
            newReview.put(ReviewAdapter.REVIEW_CONTENT_KEY, (String) currentResult.get("content"));

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

            newVideo.put("path", ((String) currentResult.get("key")));
            newVideo.put("site", (String) currentResult.get("site"));
            newVideo.put("type", (String) currentResult.get("type"));
            newVideo.put("name", (String) currentResult.get("name"));

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
