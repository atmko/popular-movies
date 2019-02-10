package com.upkipp.popularmovies;

import java.util.ArrayList;
import com.upkipp.popularmovies.Models.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class MovieDataParser {

    static void parseData(String returnedJSONString) throws JSONException {
        //skips code below if returnedJSONString null or empty
        if (returnedJSONString == null || returnedJSONString.equals("")){
            return;
        }

        // convert returnedJSONString to JSONObject
        JSONObject jsonObject = new JSONObject(returnedJSONString);
        //set total pages available to searchPreferences
        //set current page in searchPreferences
        int totalPages = jsonObject.getInt(MovieData.MovieDataKeys.TOTAL_PAGES_KEY);
        
        MainActivity.searchPreferences.setTotalPages(totalPages);
        MainActivity.searchPreferences
                .setCurrentPage(jsonObject.getInt(MovieData.MovieDataKeys.CURRENT_PAGE_KEY));

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
        MainActivity.searchAdapter.addAdapterData(movieDataList);

    }
}
