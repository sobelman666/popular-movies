package com.sobelman.and.popularmovies;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobelman.and.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Adapter for the trailers RecyclerView. Sends click events to a handler so trailer Uris
 * can be passed to an Intent.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private TMDbMovieVideo[] mDataset;

    private final TrailerAdapter.OnClickHandler mClickHandler;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // an ImageView to display the thumbnail image for the trailer
        final ImageView mTrailerThumbnail;
        // a TextView for the title of the trailer
        final TextView mTrailerTitle;

        ViewHolder(View v) {
            super(v);
            mTrailerThumbnail = v.findViewById(R.id.iv_trailer_thumbnail);
            mTrailerTitle = v.findViewById(R.id.tv_trailer_title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mDataset[getAdapterPosition()]);
        }
    }

    public interface OnClickHandler {
        void onClick(TMDbMovieVideo selectedTrailer);
    }

    public TrailerAdapter(OnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TMDbMovieVideo video = mDataset[position];
        Uri thumbnailUri = NetworkUtils.buildTrailerThumbnailUri(video.getKey());
        Picasso.get().load(thumbnailUri).into(holder.mTrailerThumbnail);
        holder.mTrailerTitle.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        return mDataset.length;
    }

    public void setTrailerData(TMDbMovieVideo[] trailers) {
        mDataset = trailers;
        notifyDataSetChanged();
    }
}
