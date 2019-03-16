package com.upkipp.popularmovies.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.upkipp.popularmovies.models.MovieData;
import com.upkipp.popularmovies.databases.AppDatabase;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();
    private final LiveData<List<MovieData>> movies;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        Log.d(TAG,"Actively retrieving movies from the database");
        movies = database.favoritesDao().loadFavorites();
    }

    public LiveData<List<MovieData>> getMovies() {
        return movies;
    }
}
