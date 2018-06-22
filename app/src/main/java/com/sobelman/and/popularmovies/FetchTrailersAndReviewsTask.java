package com.sobelman.and.popularmovies;

import android.os.AsyncTask;

import com.sobelman.and.popularmovies.util.MovieParsingUtils;
import com.sobelman.and.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTask subclass for retrieving trailers and reviews data from the TMDb API.
 */
public class FetchTrailersAndReviewsTask
        extends AsyncTask<FetchTrailersAndReviewsTask.Callback, Void, TMDbMovie> {

    // the Callback to notify once the data is retrieved
    private Callback mCallback;

    /**
     * Interface to implement in order to receive results of the API call
     * executed by a FetchTrailersAndReviewsTask.
     */
    public interface Callback {
        /**
         * Gets the movie for which trailers and reviews are being fetched.
         * @return a TMDbMovie whose id will be used in the API call.
         */
        TMDbMovie getMovie();

        /**
         * Called on completion of the task to allow the Callback to process the results.
         * @param movie a TMDbMovie holding the retrieved trailers and reviews, if available.
         */
        void onFetchCompleted(TMDbMovie movie);
    }

    @Override
    protected TMDbMovie doInBackground(Callback... callbacks) {
        mCallback = callbacks[0];
        TMDbMovie movie = mCallback.getMovie();

        // query the trailers endpoint, parse the data, and store it in the movie object
        URL trailersURL = NetworkUtils.buildVideosURL(movie.getId());
        String response;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(trailersURL);
            movie.setTrailers(MovieParsingUtils.parseJSONVideoListData(response));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // query the reviews endpoint, parse the data, and store it in the movie object
        URL reviewsURL = NetworkUtils.buildReviewsURL(movie.getId());
        try {
            response = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
            movie.setReviews(MovieParsingUtils.parseJSONReviewListData(response));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movie;
    }

    @Override
    protected void onPostExecute(TMDbMovie movie) {
        // send the movie back to the caller
        mCallback.onFetchCompleted(movie);
    }
}
