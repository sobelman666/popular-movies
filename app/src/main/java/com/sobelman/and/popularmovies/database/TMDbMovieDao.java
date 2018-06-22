package com.sobelman.and.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sobelman.and.popularmovies.TMDbMovie;

import java.util.List;

/**
 * DAO for accessing movie data stored with Room.
 */
@Dao
public interface TMDbMovieDao {
    @Query("SELECT * FROM tmdb_movie")
    LiveData<List<TMDbMovie>> loadAllMovies();

    @Insert
    void insertMovie(TMDbMovie movie);

    @Delete
    void deleteMovie(TMDbMovie movie);

    @Query("SELECT * FROM tmdb_movie where id = :id")
    LiveData<TMDbMovie> loadMovieById(int id);
}
