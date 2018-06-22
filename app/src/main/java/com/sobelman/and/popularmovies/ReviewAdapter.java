package com.sobelman.and.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter for the reviews RecyclerView.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private TMDbMovieReview[] mDataset;

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView contentTextView;

        ViewHolder(View v) {
            super(v);
            contentTextView = v.findViewById(R.id.tv_content);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TMDbMovieReview review = mDataset[position];
        String content = review.getContent();
        String author = review.getAuthor();
        holder.contentTextView.setText(content.trim());
        holder.contentTextView.append("\n\n -");
        holder.contentTextView.append(author);
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        return mDataset.length;
    }

    public void setReviewData(TMDbMovieReview[] reviews) {
        mDataset = reviews;
        notifyDataSetChanged();
    }
}
