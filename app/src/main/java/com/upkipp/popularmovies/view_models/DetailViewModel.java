package com.upkipp.popularmovies.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.database.AppDatabase;

public class DetailViewModel extends ViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();
    private final LiveData<MovieData> movie;

    public DetailViewModel(AppDatabase database, String movieId) {
        Log.d(TAG,"Actively retrieving movie by id from the database");
        movie = database.favoritesDao().getMovieById(movieId);
    }

    public LiveData<MovieData> getMovie() {
        return movie;
    }

}
