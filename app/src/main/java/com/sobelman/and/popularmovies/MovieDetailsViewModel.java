package com.sobelman.and.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

/**
 * ViewModel subclass for managing LiveData for the movie details.
 */
public class MovieDetailsViewModel extends ViewModel {
    private TMDbMovieLiveData movieLiveData;

    public MovieDetailsViewModel(@NonNull TMDbMovie movie) {
        movieLiveData = new TMDbMovieLiveData(movie);
    }

    public TMDbMovieLiveData getMovieLiveData() {
        return movieLiveData;
    }
}
