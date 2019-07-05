package com.example.popularmovies;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite")
class FavoriteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String movieId;
    private final String movieName;
    private final String releaseDate;
    private final String image;
    private String synopsis;

    @Ignore
    public FavoriteEntry(String movieId, String movieName, String releaseDate, String image, String synopsis) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        this.image = image;
        this.synopsis = synopsis;
    }

    public FavoriteEntry(int id, String movieId, String movieName, String releaseDate, String image, String synopsis) {
        this.id = id;
        this.movieId = movieId;
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        this.image = image;
        this.synopsis = synopsis;
    }

    public int getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getReleaseDate() { return releaseDate; }

    public String getSynopsis() {
        return synopsis;
    }

    public String getImage() {
        return image;
    }
}