package com.upkipp.popularmovies.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.upkipp.popularmovies.databases.AppDatabase;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDatabase;
    private final String mId;

    public DetailViewModelFactory(AppDatabase database, String movieId) {
        mDatabase = database;
        mId = movieId;
    }

    @Override
    public @NonNull<T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDatabase, mId);
    }

}
