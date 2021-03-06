package com.sobelman.and.popularmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.sobelman.and.popularmovies.util.NetworkUtils;

/**
 * Main activity for the app. Much of the design is based on Sunshine.
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.OnClickHandler {

    private static final String ABOUT_DIALOG_TAG = "AboutDialog";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorDisplay;
    private ProgressBar mProgressBar;

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

        boolean apiKeyExists = NetworkUtils.loadApiKey(this);

        if (!apiKeyExists) {
            mErrorDisplay.setText(R.string.no_api_key);
        } else {
            setUpViewModel();
        }
    }

    private void setUpViewModel() {
        mProgressBar.setVisibility(View.VISIBLE);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovieData().observe(this, new Observer<TMDbMovie[]>() {
            @Override
            public void onChanged(@Nullable TMDbMovie[] movies) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (movies == null) {
                    mErrorDisplay.setText(R.string.error_movie_list);
                    showError();
                } else {
                    showMovieList();
                    mMovieAdapter.setMovieData(movies);
                }
            }
        });
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

}
