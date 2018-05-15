package com.sobelman.and.popularmovies.util;

import android.text.TextUtils;

import com.sobelman.and.popularmovies.TMDbMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions for parsing JSON data into TMDbMovie objects.
 */
public class MovieParsingUtils {
    /**
     * Parses a JSON string containing the results of a query to the TMDb API
     * into an array of TMDbMovie objects.
     *
     * @param jsonString the JSON string to be parsed.
     * @return an array of TMDbMovie objects.
     */
    public static TMDbMovie[] parseJSONMovieListData(String jsonString) {
        if (jsonString == null) return null;

        TMDbMovie[] movies = null;
        JSONObject responseJSON;
        try {
            responseJSON = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        JSONArray results = responseJSON.optJSONArray("results");
        if (results != null) {
            movies = new TMDbMovie[results.length()];
            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJSON = results.optJSONObject(i);
                if (movieJSON != null) {
                    movies[i] = parseMovie(movieJSON);
                }
            }
        }
        return movies;
    }

    /**
     * Parses a JSONObject containing movie data into a TMDbMovie object.
     *
     * @param movieJSON the JSONObject to be parsed.
     * @return a TMDbMovie object containing the parsed data.
     */
    public static TMDbMovie parseMovie(JSONObject movieJSON) {
        String title = movieJSON.optString("title");
        String releaseDate = movieJSON.optString("release_date");
        String posterPath = movieJSON.optString("poster_path");
        // poster path might have an unwanted '/' prepended to it, get rid of it if it's there
        if (!TextUtils.isEmpty(posterPath) && posterPath.charAt(0) == '/') {
            if (posterPath.length() > 1) {
                posterPath = posterPath.substring(1);
            } else {
                posterPath = null;
            }
        }
        float voteAverage = (float)movieJSON.optDouble("vote_average");
        String synopsis = movieJSON.optString("overview");
        return new TMDbMovie(title, releaseDate, posterPath, voteAverage, synopsis);
    }
}
