/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.upkipp.popularmovies.utils.network_utils;

import com.upkipp.popularmovies.BuildConfig;

public class ApiConstants {
    //api key
    public static final String API_KEY = BuildConfig.apiKey;

    //URLS
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";
    public static final String MOVIE_SEARCH_FORMAT = SEARCH_BASE_URL + "/?api_key={api_key}&language={lang}&query={query}&page={page}";
    public static final String SEARCH_FORMAT = BASE_URL + "/{sort}?api_key={api_key}&language={lang}&page={page}";
    public static final String REVIEWS_URL_FORMAT = BASE_URL + "/{movie_id}/reviews?api_key={api_key}&language={lang}";
    public static final String VIDEOS_URL_FORMAT = BASE_URL + "/{movie_id}/videos?api_key={api_key}&language={lang}";

    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    @SuppressWarnings("SpellCheckingInspection")
    public static final String VIDEO_IMAGE_URL_FORMAT = "https://img.youtube.com/vi/{video_id}/sddefault.jpg";
    public static final String YOUTUBE_INTENT_BASE_URL = "https://www.youtube.com/watch?v=";

    //PLACEHOLDER KEYS
    public static final String API_KEY_KEY = "api_key";
    public static final String MOVIE_ID_KEY = "movie_id";
    public static final String LANG_KEY = "lang";
    public static final String  QUERY_KEY = "query";
    public static final String PAGE_KEY = "page";
    public static final String SORT_KEY = "sort";
    public static final String VIDEO_IMG_KEY = "video_id";
    public static final String VIDEO_IMG_SIZE = "img_size";

    //image values
    public static final String BACKDROP_IMAGE_SIZE = "/w780";

    //RESULTS KEYS
    //for videos
    public static final String VIDEO_TYPE_KEY = "type";
    public static final String VIDEO_PATH_KEY = "key";
    public static final String VIDEO_NAME_KEY = "name";
    public static final String VIDEO_SITE_KEY = "site";
    //for reviews
    public static final String REVIEW_AUTHOR_KEY = "author";
    public static final String REVIEW_CONTENT_KEY = "content";

}
