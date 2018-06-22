package com.sobelman.and.popularmovies;

/**
 * Model class for review data for a movie's details.
 */
public class TMDbMovieReview {
    private String id;
    private String author;
    private String content;

    public TMDbMovieReview(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
