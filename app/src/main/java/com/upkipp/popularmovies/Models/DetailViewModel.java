package com.upkipp.popularmovies.Models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<MovieData> movie;

    public DetailViewModel(AppDatabase database, String movieId) {
        Log.d(TAG,"Actively retrieving movie by id from the database");
        movie = database.favoritesDao().getMovieById(movieId);
    }

    public LiveData<MovieData> getMovie() {
        return movie;
    }

}
