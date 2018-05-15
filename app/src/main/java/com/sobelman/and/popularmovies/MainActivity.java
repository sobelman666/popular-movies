package com.sobelman.and.popularmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobelman.and.popularmovies.util.MovieParsingUtils;
import com.sobelman.and.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Main activity for the app. Much of the design is based on Sunshine.
 */
public class MainActivity extends AppCompatActivity implements FetchMovieCallback,
        MovieAdapter.OnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ABOUT_DIALOG_TAG = "AboutDialog";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorDisplay;
    private ProgressBar mProgressBar;
    private FetchMovieDataTask mFetchTask;

    private boolean isPreferenceUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.movie_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // This bit is from Stack Overflow: https://stackoverflow.com/a/38472370
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 200);

        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mErrorDisplay = findViewById(R.id.error_display);

        mProgressBar = findViewById(R.id.progressBar);

        // register for preference changes so RecyclerView will update when list style is changed
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        boolean apiKeyExists = NetworkUtils.loadApiKey(this);

        if (!apiKeyExists) {
            mErrorDisplay.setText(R.string.no_api_key);
        } else {
            loadMovieData();
        }
    }

    private void loadMovieData() {
        // since a loader isn't being used, manually cancel any existing fetch task
        if (mFetchTask != null) {
            mFetchTask.cancel(true);
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mFetchTask = new FetchMovieDataTask();
        mFetchTask.execute(this);
    }

    private void showMovieList() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPreferenceUpdated) {
            mMovieAdapter.setMovieData(null);
            loadMovieData();
            isPreferenceUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // the choice of list style (top rated or most popular) is handled by a setting
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            // show a dialog with the TMDb attribution
            new AboutDialogFragment().show(getSupportFragmentManager(), ABOUT_DIALOG_TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // handle clicks on items in the RecyclerView
    @Override
    public void onClick(TMDbMovie selectedMovie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, selectedMovie);
        startActivity(intent);
    }

    // from FetchMovieCallback, used by FetchMovieDataTask to access shared preferences
    @Override
    public Context getContext() {
        return this;
    }

    // from FetchMovieCallback, called by FetchMovieDataTask after the movie data is fetched
    @Override
    public void onFetchCompleted(TMDbMovie[] movies) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (movies == null) {
            mErrorDisplay.setText(R.string.error_movie_list);
            showError();
        } else {
            showMovieList();
            mMovieAdapter.setMovieData(movies);
        }

        // the task is done, so set the instance variable to null
        mFetchTask = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        isPreferenceUpdated = true;
    }

    /**
     * A DialogFragment for displaying the TMDb attribution information. Shown when
     * the "About" menu item is selected.
     */
    public static class AboutDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_about, null));
            return builder.create();
        }
    }

    /**
     * AsyncTask for fetching movie data.
     */
    private static class FetchMovieDataTask
            extends AsyncTask<FetchMovieCallback, Void, TMDbMovie[]> {

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
}
