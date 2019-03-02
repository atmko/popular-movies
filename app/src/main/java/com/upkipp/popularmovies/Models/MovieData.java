package com.upkipp.popularmovies.Models;

import com.upkipp.popularmovies.Utils.ApiConstants;
import com.upkipp.popularmovies.Utils.MovieDataParser;
import com.upkipp.popularmovies.Utils.NetworkFunctions;
import com.upkipp.popularmovies.Utils.SearchPreferences;

import org.json.JSONArray;

import java.util.List;

public final class MovieData {
    private final String mId;
    private final String  mVoteCount;
    private final boolean mVideo;
    private final String mVoteAverage;
    private final String mTitle;
    private final String mPopularity;
    private final String mPosterPath;
    private final String mOriginalLanguage;
    private final String mOriginalTitle;
    private final List<Integer> mGenreIds;
    private final String mBackdropPath;
    private final boolean mAdult;
    private final String mOverview;
    private final String mReleaseDate;
    private final List<String> mVideos = null;
    private final List<String> mReviews = null;

    public MovieData(int Id, int voteCount, boolean video, Double voteAverage, String title,
                     Double popularity, String posterPath, String originalLanguage,
                     String originalTitle, JSONArray genreIds, String backdropPath,
                     boolean adult, String overview, String releaseDate) {

        //check numbers(int / double) and JSONArray objects and replace error values;
        //Boolean and String error already replaced by default
        this.mId = MovieDataParser.checkAndConvertNumber(Id);
        this.mVoteCount = MovieDataParser.checkAndConvertNumber(voteCount);
        this.mVideo = video;
        this.mVoteAverage = MovieDataParser.checkAndConvertNumber(voteAverage);
        this.mTitle = title;
        this.mPopularity = MovieDataParser.checkAndConvertNumber(popularity);
        this.mPosterPath = posterPath;
        this.mOriginalLanguage = originalLanguage;
        this.mOriginalTitle = originalTitle;
        this.mGenreIds = MovieDataParser.checkAndConvertJSONArrayToList(genreIds);
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

    public String getId() {
        return mId;
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
        return ApiConstants.IMAGE_BASE_URL + ApiConstants.POSTER_IMAGE_SIZE + mPosterPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public String getBackdropPath() {
        return ApiConstants.IMAGE_BASE_URL +
                ApiConstants.BACKDROP_IMAGE_SIZE +
                mBackdropPath;
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

    public List<String> getReviews() {
        return mReviews;
    }

    public List<String> getVideos() {
        return mVideos;
    }
}
