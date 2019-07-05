package com.example.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<FavoriteEntry>> loadAllFavorites();

    @Insert
    void insertFavorite(FavoriteEntry favoriteEntry);

    @Delete
    void deleteFavorite(FavoriteEntry favoriteEntry);

    @Query("SELECT * FROM favorite WHERE id = :id")
    LiveData<FavoriteEntry> loadFavoriteById(int id);

    @Query("SELECT count(movieId) FROM favorite WHERE movieId = :movieId")
    int findSpecificMovie(String movieId);
}