package com.sobelman.and.popularmovies;

import android.content.Context;

/**
 * Interface to be implemented by callers of FetchMovieDataTask so they can pass along context
 * information and be notified of task completion.
 */
public interface FetchMovieCallback {
    /**
     * Gets the context so FetchMovieDataTask can access preferences.
     *
     * @return the Context to be used.
     */
    public Context getContext();

    /**
     * Called on completion of the FetchMovieDataTask.
     *
     * @param movies an array of TMDbMovie objects parsed from the fetched movie data.
     */
    public void onFetchCompleted(TMDbMovie[] movies);
}
