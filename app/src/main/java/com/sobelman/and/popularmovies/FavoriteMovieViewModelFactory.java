package com.sobelman.and.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.sobelman.and.popularmovies.database.AppDatabase;

/**
 * Factory for creating a FavoriteMovieViewModel associated with a particular movie.
 */
public class FavoriteMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // a handle to the database
    private AppDatabase mDb;
    // the TMDb id of the movie
    private int mMovieId;

    /**
     * Constructor for the factory.
     *
     * @param database a reference to the Room database instance.
     * @param movieId the TMDb id of the movie for which a ViewModel is desired.
     */
    public FavoriteMovieViewModelFactory(AppDatabase database, int movieId) {
        mDb = database;
        mMovieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FavoriteMovieViewModel(mDb, mMovieId);
    }
}
