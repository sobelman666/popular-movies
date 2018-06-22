package com.sobelman.and.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sobelman.and.popularmovies.TMDbMovie;

/**
 * Underlying database used by Room.
 */
@Database(entities = {TMDbMovie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    // DAO for accessing stored data
    public abstract TMDbMovieDao movieDao();

}
