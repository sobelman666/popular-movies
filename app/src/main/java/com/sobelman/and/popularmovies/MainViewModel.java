package com.sobelman.and.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * ViewModel for managing the LiveData for the main movie list.
 */
public class MainViewModel extends AndroidViewModel {

    private MovieListLiveData movieData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        movieData = new MovieListLiveData(getApplication());
    }

    public MovieListLiveData getMovieData() {
        return movieData;
    }
}
