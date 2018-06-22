package com.sobelman.and.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.sobelman.and.popularmovies.database.AppDatabase;

/**
 * ViewModel subclass for managing movies that have been marked as a favorite.
 */
public class FavoriteMovieViewModel extends ViewModel {
    private LiveData<TMDbMovie> movieLiveData;

    public FavoriteMovieViewModel(AppDatabase db, int movieId) {
        movieLiveData = db.movieDao().loadMovieById(movieId);
    }

    public LiveData<TMDbMovie> getMovieLiveData() {
        return movieLiveData;
    }
}
