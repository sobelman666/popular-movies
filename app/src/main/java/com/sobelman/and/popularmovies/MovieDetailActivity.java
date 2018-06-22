package com.sobelman.and.popularmovies;

import android.app.ActionBar;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobelman.and.popularmovies.database.AppDatabase;
import com.sobelman.and.popularmovies.util.DateUtils;
import com.sobelman.and.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static com.sobelman.and.popularmovies.util.StringUtils.*;

/**
 * Activity for showing details about a particular movie.
 */
public class MovieDetailActivity extends AppCompatActivity
        implements TrailerAdapter.OnClickHandler {

    // key for the extra containing the TMDbMovie object
    public static final String EXTRA_MOVIE = "com.sobelman.and.popularmovies.ExtraMovie";
    // key for saving the state of the favorite button on device rotation
    public static final String IS_FAVORITE_KEY = "isFavoriteState";

    // the TMDbMovie object whose data is being displayed
    private TMDbMovie mMovie;

    private TextView mReleaseDateTextView, mVoteAverageTextView, mSynopsisTextView;
    private ImageView mPosterImageView;
    private ImageButton mFavoriteButton;

    private LinearLayout mTrailersLayout, mReviewsLayout;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private boolean isFavorite = false;

    private AppDatabase mDb;

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
        mFavoriteButton = findViewById(R.id.ib_favorite);

        mTrailersLayout = findViewById(R.id.trailers_layout);
        mTrailersLayout.setVisibility(View.GONE);
        mReviewsLayout = findViewById(R.id.reviews_layout);
        mReviewsLayout.setVisibility(View.GONE);

        RecyclerView trailersRecyclerView = findViewById(R.id.rv_trailers);
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(trailersLayoutManager);
        mTrailerAdapter = new TrailerAdapter(this);
        trailersRecyclerView.setAdapter(mTrailerAdapter);

        RecyclerView reviewsRecyclerView = findViewById(R.id.rv_reviews);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(mReviewAdapter);
        reviewsRecyclerView.setHasFixedSize(false);
        reviewsRecyclerView.addItemDecoration(new DividerItemDecoration(this, reviewsLayoutManager.getOrientation()));

        mDb = AppDatabase.getInstance(this);

        // set the correct image resource for the favorite button
        if (savedInstanceState != null && savedInstanceState.containsKey(IS_FAVORITE_KEY)) {
            // don't make a database call if the device has been rotated, just check the saved
            // instance state
            isFavorite = savedInstanceState.getBoolean(IS_FAVORITE_KEY);
            mFavoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_blue : R.drawable.ic_star_border_black);
        } else {
            // we don't have a saved instance state for the favorite button, so get it from the db
            FavoriteMovieViewModelFactory factory =
                    new FavoriteMovieViewModelFactory(mDb, mMovie.getId());
            final FavoriteMovieViewModel viewModel =
                    ViewModelProviders.of(this, factory).get(FavoriteMovieViewModel.class);
            viewModel.getMovieLiveData().observe(this, new Observer<TMDbMovie>() {
                @Override
                public void onChanged(@Nullable TMDbMovie movie) {
                    viewModel.getMovieLiveData().removeObserver(this);
                    if (movie != null) {
                        isFavorite = true;
                        mFavoriteButton.setImageResource(R.drawable.ic_star_blue);
                    } else {
                        isFavorite = false;
                    }
                }
            });
        }

        populateUI();

        // initiate a call to the API to fetch the trailers and reviews data
        MovieDetailsViewModelFactory detailsViewModelFactory =
                new MovieDetailsViewModelFactory(mMovie);
        final MovieDetailsViewModel detailsViewModel =
                ViewModelProviders.of(this, detailsViewModelFactory)
                        .get(MovieDetailsViewModel.class);
        detailsViewModel.getMovieLiveData().observe(this, new Observer<TMDbMovie>() {
            @Override
            public void onChanged(@Nullable TMDbMovie movie) {
                detailsViewModel.getMovieLiveData().removeObserver(this);
                if (movie != null) {
                    loadTrailersAndReviews(movie);
                }
            }
        });
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

    private void loadTrailersAndReviews(TMDbMovie movie) {
        // only display the trailers and reviews layouts if there is data
        TMDbMovieVideo[] trailers = movie.getTrailers();
        if (trailers != null && trailers.length > 0) {
            mTrailersLayout.setVisibility(View.VISIBLE);
            mTrailerAdapter.setTrailerData(trailers);
        }

        TMDbMovieReview[] reviews = movie.getReviews();
        if (reviews != null && reviews.length > 0) {
            mReviewsLayout.setVisibility(View.VISIBLE);
            mReviewAdapter.setReviewData(reviews);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAVORITE_KEY, isFavorite);
    }

    @Override
    public void onClick(TMDbMovieVideo selectedTrailer) {
        // let the user pick an app to view the selected trailer
        Intent intent = new Intent(Intent.ACTION_VIEW, selectedTrailer.getUri());
        startActivity(intent);
    }

    // event handler for onClick event for the favorite button
    public void onFavoriteButtonClick(View view) {
        ImageButton favoriteButton = (ImageButton)view;
        if (!isFavorite) {
            // update state and add movie to the database
            isFavorite = true;
            favoriteButton.setImageResource(R.drawable.ic_star_blue);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(mMovie);
                }
            });
        } else {
            // update state and delete movie from the database
            isFavorite = false;
            favoriteButton.setImageResource(R.drawable.ic_star_border_black);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(mMovie);
                }
            });
        }
    }
}
