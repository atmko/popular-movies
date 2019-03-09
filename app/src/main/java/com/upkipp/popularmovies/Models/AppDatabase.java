package com.upkipp.popularmovies.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {MovieData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "creating new database instance");
                sInstance =
                        Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME)
                .build();

                return sInstance;
            }

        } else {
            return sInstance;
        }
    }

    public abstract FavoritesDao favoritesDao();
}
