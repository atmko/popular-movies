package com.upkipp.popularmovies.Utils;

import java.util.ArrayList;
import java.util.List;

import com.upkipp.popularmovies.Activities.MainActivity;
import com.upkipp.popularmovies.Adapters.SearchAdapter;
import com.upkipp.popularmovies.Models.MovieData;

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
                //add items individually to list
                list.add((int) jsonArray.get(position));
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

    public static void parseData(String returnedJSONString) throws JSONException {
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return;
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
        ArrayList<MovieData> movieDataList = new ArrayList<>();

        //iterate through each movie in results
        for (int index = 0; index < results.length() ; index++) {
            JSONObject currentObject = (JSONObject) results.get(index);//get current movie

            //create new MovieData from currentObject
            MovieData movieData =
                    new MovieData(
                            //get by keys. use fallback values if error.(format: key, fallback)
                            currentObject.optInt(MovieData.MovieDataKeys.MOVIE_ID,
                                    MovieData.ErrorValues.INT_ERROR),

                            currentObject.optInt(MovieData.MovieDataKeys.VOTE_COUNT,
                                    MovieData.ErrorValues.INT_ERROR),

                            currentObject.optBoolean(MovieData.MovieDataKeys.VIDEO,
                                    MovieData.ErrorValues.BOOLEAN_ERROR),

                            currentObject.optDouble(MovieData.MovieDataKeys.VOTE_AVERAGE,
                                    MovieData.ErrorValues.DOUBLE_ERROR),

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
                            currentObject.optJSONArray(MovieData.MovieDataKeys.GENRE_IDS),

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
        SearchAdapter searchAdapter = SearchAdapter.getInstance(null);
        searchAdapter.addAdapterData(movieDataList);

    }

    public static void parseReviews(String returnedJSONString) throws JSONException{
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return;
        }

        // convert returnedJSONString to JSONObject
        JSONObject jsonObject = new JSONObject(returnedJSONString);

        //use RESULTS_KEY to get results as JSONArray
        JSONArray results = jsonObject.getJSONArray(MovieData.MovieDataKeys.RESULTS_KEY);

        //SearchAdapter data will be stored as ArrayList<MovieData>
        ArrayList<MovieData> movieDataList = new ArrayList<>();

        //iterate through each movie in results
        for (int index = 0; index < results.length() ; index++) {
            JSONObject currentReview = (JSONObject) results.get(index);//get current review

        }

    }

    public static void parseVideos(String returnedJSONString) throws JSONException{
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return;
        }

        // convert returnedJSONString to JSONObject
        JSONObject jsonObject = new JSONObject(returnedJSONString);

        //use RESULTS_KEY to get results as JSONArray
        JSONArray results = jsonObject.getJSONArray(MovieData.MovieDataKeys.RESULTS_KEY);

        //SearchAdapter data will be stored as ArrayList<MovieData>
        ArrayList<MovieData> movieDataList = new ArrayList<>();

        //iterate through each movie in results
        for (int index = 0; index < results.length() ; index++) {
            JSONObject currentReview = (JSONObject) results.get(index);//get current review

        }

    }
}
