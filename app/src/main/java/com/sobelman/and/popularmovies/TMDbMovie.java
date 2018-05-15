package com.sobelman.and.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for movie data. Implements Parcelable so objects of this class can be passed
 * as extras in intents to MovieDetailActivity most efficiently.
 */
public class TMDbMovie implements Parcelable {
    private String title;
    private String releaseDate;
    private String posterPath;
    private float voteAverage;
    private String synopsis;

    public TMDbMovie(String title, String releaseDate, String posterPath, float voteAverage,
                     String synopsis) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    // private constructor used by the Creator
    private TMDbMovie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readFloat();
        synopsis = in.readString();
    }

    /**
     * Anonymous inner class implementing the Parcelable.Creator interface,
     * necessary for implementing the Parcelable interface.
     */
    public static final Creator<TMDbMovie> CREATOR = new Creator<TMDbMovie>() {
        @Override
        public TMDbMovie createFromParcel(Parcel in) {
            return new TMDbMovie(in);
        }

        @Override
        public TMDbMovie[] newArray(int size) {
            return new TMDbMovie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    // part of Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    // part of Parcelable interface, instance variable values must be written in the same
    // order that the are read in the private constructor
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeFloat(voteAverage);
        parcel.writeString(synopsis);
    }

    @Override
    public String toString() {
        return "TMDbMovie{" +
                "title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", voteAverage=" + voteAverage +
                ", synopsis='" + synopsis + '\'' +
                '}';
    }
}
