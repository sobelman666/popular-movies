package com.sobelman.and.popularmovies.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

/**
 * Utility functions for querying the TMDb API.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // base URL for TMDb API movie data queries
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    // base URL for poster images
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p";

    // movie list paths
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String POPULAR_PATH = "popular";

    // poster image size path
    private static final String IMAGE_SIZE_PATH = "w185";

    // API key query parameter
    private static final String API_KEY_PARAM = "api_key";

    // API key
    private static String API_KEY;

    /**
     * Load the API key for querying the TMDb API from a properties file. The file
     * should be named tmdb.properties and it should be located in src/main/assets.
     *
     * @param context the Context in which to look for the properties file.
     * @return true if the API key was loaded successfully or previously existed, false otherwise.
     */
    public static boolean loadApiKey(Context context) {
        if (API_KEY == null) {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream;
            try {
                inputStream = assetManager.open("tmdb.properties");
                properties.load(inputStream);
                API_KEY = properties.getProperty(API_KEY_PARAM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return API_KEY != null;
    }

    /**
     * Builds the URL for the "top rated" endpoint.
     *
     * @return the URL to connect to.
     */
    public static URL buildTopRatedPath() {
        return buildMovieURL(TOP_RATED_PATH);
    }

    /**
     * Builds the URL for the "most popular" endpoint.
     *
     * @return the URL to connect to.
     */
    public static URL buildPopularPath() {
        return buildMovieURL(POPULAR_PATH);
    }

    // helper for building URLs for a given path
    private static URL buildMovieURL(String movieListPath) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL)
                .buildUpon()
                .appendPath(movieListPath)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        return buildURLFromUri(builtUri);
    }

    /**
     * Builds the Uri used by Picasso for fetching the poster images.
     *
     * @param posterFilePath the file name of the poster image.
     * @return a Uri suitable for Picasso.
     */
    public static Uri buildPosterUri(String posterFilePath) {
        return Uri.parse(POSTER_BASE_URL)
                .buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendPath(posterFilePath)
                .build();
    }

    // helper for building URLs from a given Uri
    private static URL buildURLFromUri(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "built URL: " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response. Shamelessly stolen
     * from Sunshine app.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
