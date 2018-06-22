package com.sobelman.and.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.sobelman.and.popularmovies.database.AppDatabase;

import java.util.List;

/**
 * LiveData for the main movie list. Extends MediatorLiveData so data source can be either
 * data from the API calls or from the favorites in the database. Data source changes in
 * response to changes to the the list style preference.
 */
public class MovieListLiveData extends MediatorLiveData<TMDbMovie[]>
        implements SharedPreferences.OnSharedPreferenceChangeListener, FetchMovieCallback {
    // Context for getting the default shared preferences
    private Context mContext;
    // LiveData source for favorites database data
    private LiveData<List<TMDbMovie>> dbSource;

    public MovieListLiveData(Context context) {
        mContext = context;
        dbSource = AppDatabase.getInstance(mContext.getApplicationContext())
                .movieDao()
                .loadAllMovies();
        // register as a listener for shared preference changes
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);
        loadMovieList();
    }

    private void loadMovieList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Resources resources = mContext.getResources();
        String listTypePref = resources.getString(R.string.pref_list_style);
        String defaultListType = resources.getString(R.string.pref_popular_value);
        String listType = preferences.getString(listTypePref, defaultListType);
        if (listType.equals(resources.getString(R.string.pref_favorite_value))) {
            // get favorites list from db
            addDbSource();
        } else {
            removeSource(dbSource);
            // run FetchMovieDataTask to get movie data from network
            new FetchMovieDataTask().execute(MovieListLiveData.this);
        }
    }

    private void addDbSource() {
        addSource(dbSource, new Observer<List<TMDbMovie>>() {
            @Override
            public void onChanged(@Nullable List<TMDbMovie> movies) {
                if (movies != null) {
                    setValue(movies.toArray(new TMDbMovie[movies.size()]));
                } else {
                    setValue(null);
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mContext.getResources().getString(R.string.pref_list_style))) {
            loadMovieList();
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onFetchCompleted(TMDbMovie[] movies) {
        setValue(movies);
    }
}
