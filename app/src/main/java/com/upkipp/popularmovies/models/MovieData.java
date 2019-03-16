/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;

import com.upkipp.popularmovies.R;
import com.upkipp.popularmovies.utils.network_utils.ApiConstants;

import java.util.List;
import java.util.Map;

@Entity(tableName = "favorites")
public class MovieData {

    @PrimaryKey@NonNull
    @ColumnInfo(name = "id") private String mId;
    @Ignore private String  mVoteCount;
    @Ignore private boolean mVideo;
    @ColumnInfo(name = "vote_average") private String mVoteAverage;
    @ColumnInfo(name = "title") private String mTitle;
    @Ignore private double mPopularity;
    @ColumnInfo(name = "poster_path")  private String mPosterPath;
    @Ignore private String mOriginalLanguage;
    @Ignore private String mOriginalTitle;
    @Ignore private List<Integer> mGenreIds;
    @ColumnInfo(name = "backdrop_path")  private String mBackdropPath;
    @Ignore private boolean mAdult;
    @ColumnInfo(name = "overview") private String mOverview;
    @ColumnInfo(name = "release_date") private String mReleaseDate;
    @Ignore private List<Map<String, String>> mVideos;
    @Ignore private List<String> mReviews;

    @Ignore
    public MovieData(@NonNull String id, String voteCount, boolean video, String voteAverage, String title,
                     double popularity, String posterPath, String originalLanguage,
                     String originalTitle, List<Integer> genreIds, String backdropPath,
                     boolean adult, String overview, String releaseDate, Context context) {

        this.mId = id;
        this.mVoteCount = voteCount;
        this.mVideo = video;
        this.mVoteAverage = voteAverage;
        this.mTitle = title;
        this.mPopularity = popularity;
        this.mPosterPath = ApiConstants.IMAGE_BASE_URL +
                context.getResources().getString(R.string.detail_poster_size) +
                posterPath;
        this.mOriginalLanguage = originalLanguage;
        this.mOriginalTitle = originalTitle;
        this.mGenreIds = genreIds;
        this.mBackdropPath = ApiConstants.IMAGE_BASE_URL +
                ApiConstants.BACKDROP_IMAGE_SIZE +
                backdropPath;
        this.mAdult = adult;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
    }

    public MovieData(@NonNull String id, String voteAverage, String title,
                     String posterPath, String backdropPath,
                     String overview, String releaseDate) {

        //check numbers(int / double) and JSONArray objects and replace error values;
        //Boolean and String error already replaced by default
        this.mId = id;
        this.mVoteAverage = voteAverage;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
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

    public double getPopularity() {
        return mPopularity;
    }


    public String getPosterPath() {
        return mPosterPath;
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
        return mBackdropPath;
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

    public List<Map<String, String>> getVideos() {
        return mVideos;
    }


    public void setId(String id) {
        this.mId = id;
    }

    public void setVoteAverage(String voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.mBackdropPath = backdropPath;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    //    public void setVideos(List<Map<String, String>> videoList) {
//        mVideos = videoList;
//    }
}
