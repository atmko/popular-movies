package com.upkipp.popularmovies.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.upkipp.popularmovies.database.AppDatabase;
import com.upkipp.popularmovies.view_models.DetailViewModel;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDatabase;
    private final String mId;

    public DetailViewModelFactory(AppDatabase database, String movieId) {
        mDatabase = database;
        mId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDatabase, mId);
    }

}
