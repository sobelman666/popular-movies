package com.sobelman.and.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.sobelman.and.popularmovies.util.MovieParsingUtils;
import com.sobelman.and.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTask for fetching movie data.
 */
public class FetchMovieDataTask extends AsyncTask<FetchMovieCallback, Void, TMDbMovie[]> {

    private FetchMovieCallback mCallback;

    @Override
    protected TMDbMovie[] doInBackground(FetchMovieCallback... callbacks) {
        mCallback = callbacks[0];
        Context context = mCallback.getContext();

        // choose path based on setting
        URL url;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredListStyle = preferences.getString(context.getString(R.string.pref_list_style),
                context.getString(R.string.pref_popular_value));
        if (preferredListStyle.equals(context.getString(R.string.pref_popular_value))) {
            url = NetworkUtils.buildPopularPath();
        } else if (preferredListStyle.equals(context.getString(R.string.pref_rating_value))) {
            url = NetworkUtils.buildTopRatedPath();
        } else {
            return null;
        }

        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MovieParsingUtils.parseJSONMovieListData(response);
    }

    @Override
    protected void onPostExecute(TMDbMovie[] tmDbMovies) {
        mCallback.onFetchCompleted(tmDbMovies);
    }
}
