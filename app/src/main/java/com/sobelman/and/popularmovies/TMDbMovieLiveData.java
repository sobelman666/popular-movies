package com.sobelman.and.popularmovies;

import android.arch.lifecycle.LiveData;

/**
 * LiveData for trailers and reviews. Retrieves data from the TMDb API.
 */
public class TMDbMovieLiveData extends LiveData<TMDbMovie>
        implements FetchTrailersAndReviewsTask.Callback {
    private TMDbMovie mMovie;

    public TMDbMovieLiveData(TMDbMovie movie) {
        mMovie = movie;
        new FetchTrailersAndReviewsTask().execute(this);
    }

    @Override
    public TMDbMovie getMovie() {
        return mMovie;
    }

    @Override
    public void onFetchCompleted(TMDbMovie movie) {
        setValue(movie);
    }
}
