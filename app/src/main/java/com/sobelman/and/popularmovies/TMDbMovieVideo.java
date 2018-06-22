package com.sobelman.and.popularmovies;

import android.net.Uri;

/**
 * Model class for video data for a movie's details.
 */
public class TMDbMovieVideo {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private Uri uri;

    public TMDbMovieVideo(String id, String key, String name, String site, String type, Uri uri) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
