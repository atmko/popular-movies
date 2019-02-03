package com.example.popularmovies.Models;

import com.example.popularmovies.SearchPreferences;

import org.json.JSONArray;

public class MovieData {
    private final String mID;
    private final String  mVoteCount;
    private final boolean mVideo;
    private final String mVoteAverage;
    private final String mTitle;
    private final String mPopularity;
    private final String mPosterPath;
    private final String mOriginalLanguage;
    private final String mOriginalTitle;
    private final JSONArray mGenreIDs;
    private final String mBackdropPath;
    private final boolean mAdult;
    private final String mOverview;
    private final String mReleaseDate;

    public MovieData(int ID, int voteCount, boolean video, Double voteAverage, String title, Double popularity, String posterPath, String originalLanguage, String originalTitle, JSONArray genreIDs, String backdropPath, boolean adult, String overview, String releaseDate) {
        //check numbers(int / double) and JSONArray objects and replace error values;
        //Boolean and String error already replaced by default
        this.mID = checkNumber(ID);
        this.mVoteCount = checkNumber(voteCount);
        this.mVideo = video;
        this.mVoteAverage = checkNumber(voteAverage);
        this.mTitle = title;
        this.mPopularity = checkNumber(popularity);
        this.mPosterPath = posterPath;
        this.mOriginalLanguage = originalLanguage;
        this.mOriginalTitle = originalTitle;
        this.mGenreIDs = checkJSONArray(genreIDs);
        this.mBackdropPath = backdropPath;
        this.mAdult = adult;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
    }

    //MovieData keys
    public static class MovieDataKeys {
        public static final String VOTE_COUNT = "vote_count";
        public static final String MOVIE_ID = "id";
        public static final String VIDEO = "video";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_TITLE = "title";
        public static final String POPULARITY = "popularity";
        public static final String POSTER_PATH = "poster_path";
        public static final String ORIG_LANG = "original_language";
        public static final String ORIG_TITLE = "original_title";
        public static final String GENRE_IDS = "genre_ids";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String ADULT = "adult";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";

        public static final String RESULTS_KEY = "results";
        public static final String TOTAL_PAGES_KEY = "total_pages";
        public static final String CURRENT_PAGE_KEY = "page";

    }

    //Error Values
    public static class ErrorValues{
        public static final String STRING_ERROR = "{error}";
        public static final int INT_ERROR = -10101;
        public static final double DOUBLE_ERROR = -10101.0;
        public static final Boolean BOOLEAN_ERROR = false;

    }

    //check for int/double errors
    private String checkNumber(Object number){
        if (Double.parseDouble(number.toString()) == ErrorValues.DOUBLE_ERROR) {
            return "{error}";
        } else {
            return String.valueOf(number);
        }
    }
    //check for JSONArray errors
    private JSONArray checkJSONArray(JSONArray jsonArray){
        if (jsonArray == null) {
            return new JSONArray();
        } else {
            return jsonArray;
        }
    }

    public String getID() {
        return mID;
    }

    public String getVoteCount() {
        return mVoteCount;
    }

    public boolean isVideo() {
        return mVideo;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPopularity() {
        return mPopularity;
    }

    public String getPosterPath() {
        return SearchPreferences.IMAGE_BASE_URL + SearchPreferences.POSTER_IMAGE_SIZE + mPosterPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public JSONArray getGenreIDs() {
        return mGenreIDs;
    }

    public String getBackdropPath() {
        return SearchPreferences.IMAGE_BASE_URL + SearchPreferences.BACKDROP_IMAGE_SIZE + mBackdropPath;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}
