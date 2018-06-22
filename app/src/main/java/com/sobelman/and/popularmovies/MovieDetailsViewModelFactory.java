package com.sobelman.and.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider.NewInstanceFactory;
import android.support.annotation.NonNull;

/**
 * Factory for creating instances of
 */
public class MovieDetailsViewModelFactory extends NewInstanceFactory {
    private TMDbMovie mMovie;

    public MovieDetailsViewModelFactory(TMDbMovie movie) {
        mMovie = movie;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailsViewModel(mMovie);
    }
}
