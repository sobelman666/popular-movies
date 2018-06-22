package com.sobelman.and.popularmovies.util;

import android.net.Uri;
import android.text.TextUtils;

import com.sobelman.and.popularmovies.TMDbMovie;
import com.sobelman.and.popularmovies.TMDbMovieReview;
import com.sobelman.and.popularmovies.TMDbMovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions for parsing JSON data into TMDbMovie objects.
 */
public class MovieParsingUtils {

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com";
    private static final String YOUTUBE_VIDEO_PATH = "watch";
    private static final String YOUTUBE_VIDEO_PARAM = "v";

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
        int id = movieJSON.optInt("id");
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
        return new TMDbMovie(id, title, releaseDate, posterPath, voteAverage, synopsis);
    }

    /**
     * Parses a JSON string retrieved from the TMDb API containing the list of video data
     * associated with a particular movie.
     *
     * @param jsonString the JSON to be parsed.
     * @return an array of TMDbMovieVideo objects.
     */
    public static TMDbMovieVideo[] parseJSONVideoListData(String jsonString) {
        if (jsonString == null) return null;

        TMDbMovieVideo[] videos = null;
        JSONObject videoListJSON;
        try {
            videoListJSON = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        JSONArray results = videoListJSON.optJSONArray("results");
        if (results != null) {
            videos = new TMDbMovieVideo[results.length()];
            for (int i = 0; i < results.length(); i++) {
                JSONObject videoJSON = results.optJSONObject(i);
                if (videoJSON != null) {
                    videos[i] = parseVideo(videoJSON);
                }
            }
        }

        return videos;
    }

    /**
     * Parses a JSONObject containing the data for a video associated with a particular movie.
     *
     * @param videoJSON the JSONObject to be parsed.
     * @return a TMDbMovieVideo object containing the parsed data.
     */
    public static TMDbMovieVideo parseVideo(JSONObject videoJSON) {
        String id = videoJSON.optString("id");
        String key = videoJSON.optString("key");
        String name = videoJSON.optString("name");
        String site = videoJSON.optString("site");
        String type = videoJSON.optString("type");

        Uri uri = null;
        if (site != null && site.equals("YouTube")) {
            uri = Uri.parse(YOUTUBE_BASE_URL)
                    .buildUpon()
                    .appendPath(YOUTUBE_VIDEO_PATH)
                    .appendQueryParameter(YOUTUBE_VIDEO_PARAM, key)
                    .build();
        }

        return new TMDbMovieVideo(id, key, name, site, type, uri);
    }

    /**
     * Parses a JSON string retrieved from the TMDb API containing the list of reviews
     * associated with a particular movie.
     *
     * @param jsonString the JSON to be parsed.
     * @return an array of TMDbReview objects.
     */
    public static TMDbMovieReview[] parseJSONReviewListData(String jsonString) {
        if (jsonString == null) return null;

        TMDbMovieReview[] reviews = null;
        JSONObject reviewListJSON;
        try {
            reviewListJSON = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        JSONArray results = reviewListJSON.optJSONArray("results");
        if (results != null) {
            reviews = new TMDbMovieReview[results.length()];
            for (int i = 0; i < results.length(); i++) {
                JSONObject reviewJSON = results.optJSONObject(i);
                if (reviewJSON != null) {
                    reviews[i] = parseReview(reviewJSON);
                }
            }
        }

        return reviews;
    }

    /**
     * Parses a JSONObject containing the data for a review associated with a particular movie.
     *
     * @param reviewJSON the JSONObject to be parsed.
     * @return a TMDbMovieReview object containing the parsed data.
     */
    public static TMDbMovieReview parseReview(JSONObject reviewJSON) {
        String id = reviewJSON.optString("id");
        String author = reviewJSON.optString("author");
        String content = reviewJSON.optString("content");
        return new TMDbMovieReview(id, author, content);
    }
}
