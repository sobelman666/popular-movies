package com.sobelman.and.popularmovies;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobelman.and.popularmovies.util.DateUtils;
import com.sobelman.and.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static com.sobelman.and.popularmovies.util.StringUtils.*;

/**
 * Activity for showing details about a particular movie.
 */
public class MovieDetailActivity extends AppCompatActivity {

    // key for the extra containing the TMDbMovie object
    public static final String EXTRA_MOVIE = "com.sobelman.and.popularmovies.ExtraMovie";

    // the TMDbMovie object whose data is being displayed
    private TMDbMovie mMovie;

    private TextView mReleaseDateTextView, mVoteAverageTextView, mSynopsisTextView;
    private ImageView mPosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
        }

        // make an up button
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mMovie != null) {
            setTitle(mMovie.getTitle());
        }

        mReleaseDateTextView = findViewById(R.id.tv_release_date);
        mVoteAverageTextView = findViewById(R.id.tv_vote_average);
        mSynopsisTextView = findViewById(R.id.tv_synopsis);
        mPosterImageView = findViewById(R.id.iv_poster);

        populateUI();
    }

    private void populateUI() {
        Picasso.get()
                .load(NetworkUtils.buildPosterUri(mMovie.getPosterPath()))
                .into(mPosterImageView);

        // make sure something is displayed for release date and vote average if data is missing
        String releaseDate = mMovie.getReleaseDate();
        String formattedReleaseDate;
        if (TextUtils.isEmpty(releaseDate)) {
            formattedReleaseDate = getNonEmptyString(releaseDate, this);
        } else {
            Date parsedReleaseDate = DateUtils.parseISO8601DateString(releaseDate);
            if (parsedReleaseDate != null) {
                formattedReleaseDate = DateUtils.getDateDisplayString(parsedReleaseDate);
            } else { // date string could not be parsed
                formattedReleaseDate = getString(R.string.no_data);
            }
        }
        mReleaseDateTextView.setText(formattedReleaseDate);
        float voteAverage = mMovie.getVoteAverage();
        String displayedVoteAverage;
        if (voteAverage == Float.NaN) {
            displayedVoteAverage = getString(R.string.no_data);
        } else {
            displayedVoteAverage = String.valueOf(mMovie.getVoteAverage());
        }
        mVoteAverageTextView.setText(displayedVoteAverage);

        String synopsis = mMovie.getSynopsis();
        // just leave synopsis empty if it's missing
        if (synopsis == null) {
            synopsis = "";
        }
        mSynopsisTextView.setText(synopsis);
    }
}
