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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDataParser {
    //convert JSONArray to List
    public static List<Integer> checkAndConvertJSONArrayToList(JSONArray jsonArray) {
        //if jsonArray is null return empty ArrayList
        List<Integer> list = new ArrayList<>();
        if (jsonArray == null) {return list;}

        int length = jsonArray.length();

        //iterate through JSONArray
        for (int position = 0 ; position < length ; position++) {
            try {
                if (jsonArray.get(position) instanceof Integer) {
                    //add items individually to list
                    list.add((int) jsonArray.get(position));
                }

                else  {
                    //add items individually to list
//                    list.add((int) jsonArray.get(position));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    //check for int/double errors
    public static String checkAndConvertNumber(Object number){
        if (Double.parseDouble(number.toString()) == MovieData.ErrorValues.DOUBLE_ERROR) {
            return "{error}";
        } else {
            return String.valueOf(number);
        }
    }

    public static List<MovieData> parseData(String returnedJSONString) throws JSONException {
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return new ArrayList<>();
        }

        // convert returnedJSONString to JSONObject
        JSONObject jsonObject = new JSONObject(returnedJSONString);
        //set total pages available to searchPreferences
        //set current page in searchPreferences
        int totalPages = jsonObject.getInt(MovieData.MovieDataKeys.TOTAL_PAGES_KEY);

        SearchPreferences searchPreferences = SearchPreferences.getInstance();
        searchPreferences.setTotalPages(totalPages);
        searchPreferences.setCurrentPage(jsonObject.getInt(MovieData.MovieDataKeys.CURRENT_PAGE_KEY));

        //use RESULTS_KEY to get results as JSONArray
        JSONArray results = jsonObject.getJSONArray(MovieData.MovieDataKeys.RESULTS_KEY);

        //SearchAdapter data will be stored as ArrayList<MovieData>
        List<MovieData> movieDataList = new ArrayList<>();

        //iterate through each movie in results
        for (int index = 0; index < results.length() ; index++) {
            JSONObject currentObject = (JSONObject) results.get(index);//get current movie

            //create new MovieData from currentObject
            MovieData movieData =
                    new MovieData(
                            //get by keys. use fallback values if error.(format: key, fallback)
                            checkAndConvertNumber(currentObject.optInt(MovieData.MovieDataKeys.MOVIE_ID,
                                    MovieData.ErrorValues.INT_ERROR)),

                            checkAndConvertNumber(currentObject.optInt(MovieData.MovieDataKeys.VOTE_COUNT,
                                    MovieData.ErrorValues.INT_ERROR)),

                            currentObject.optBoolean(MovieData.MovieDataKeys.VIDEO,
                                    MovieData.ErrorValues.BOOLEAN_ERROR),

                            checkAndConvertNumber(currentObject
                                    .optDouble(MovieData.MovieDataKeys.VOTE_AVERAGE,
                                    MovieData.ErrorValues.DOUBLE_ERROR)),

                            currentObject.optString(MovieData.MovieDataKeys.MOVIE_TITLE,
                                    MovieData.ErrorValues.STRING_ERROR),

                            currentObject.optDouble(MovieData.MovieDataKeys.POPULARITY,
                                    MovieData.ErrorValues.DOUBLE_ERROR),

                            currentObject.optString(MovieData.MovieDataKeys.POSTER_PATH,
                                    MovieData.ErrorValues.STRING_ERROR),

                            currentObject.optString(MovieData.MovieDataKeys.ORIG_LANG,
                                    MovieData.ErrorValues.STRING_ERROR),

                            currentObject.optString(MovieData.MovieDataKeys.ORIG_TITLE,
                                    MovieData.ErrorValues.STRING_ERROR),

                            //optJSONArray method has no fallback
                            checkAndConvertJSONArrayToList(currentObject
                                    .optJSONArray(MovieData.MovieDataKeys.GENRE_IDS)),

                            currentObject.optString(MovieData.MovieDataKeys.BACKDROP_PATH,
                                    MovieData.ErrorValues.STRING_ERROR),

                            currentObject.optBoolean(MovieData.MovieDataKeys.ADULT,
                                    MovieData.ErrorValues.BOOLEAN_ERROR),

                            currentObject.optString(MovieData.MovieDataKeys.OVERVIEW,
                                    MovieData.ErrorValues.STRING_ERROR),

                            currentObject.optString(MovieData.MovieDataKeys.RELEASE_DATE,
                                    MovieData.ErrorValues.STRING_ERROR)
                    );

            movieDataList.add(movieData);

        }
        //set data to adapter
        return movieDataList;

    }

    public static ArrayList<Map<String, String>> parseReviews(String returnedJSONString)
            throws JSONException{
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

    public static ArrayList<Map<String, String>> parseVideos(String returnedJSONString)
            throws JSONException{
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
