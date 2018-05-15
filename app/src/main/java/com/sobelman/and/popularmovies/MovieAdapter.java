package com.sobelman.and.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sobelman.and.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Custom adapter class for the RecyclerView in MainActivity.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private TMDbMovie[] mDataset;

    private final OnClickHandler mClickHandler;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // an ImageView to display the poster image for the movie
        final ImageView mImageView;

        ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mDataset[getAdapterPosition()]);
        }
    }

    public interface OnClickHandler {
        public void onClick(TMDbMovie selectedMovie);
    }

    // Constructor that sets our click handler for the adapter
    public MovieAdapter(OnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // make a Uri for the poster image and use Picasso to load it into the ImageView
        String posterPath = mDataset[position].getPosterPath();
        Picasso.get().load(NetworkUtils.buildPosterUri(posterPath)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        return mDataset.length;
    }

    public void setMovieData(TMDbMovie[] data) {
        mDataset = data;
        notifyDataSetChanged();
    }
}