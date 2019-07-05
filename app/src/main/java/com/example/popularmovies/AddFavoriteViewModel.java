package com.example.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

class AddFavoriteViewModel extends ViewModel {

    private final LiveData<FavoriteEntry> favorite;

    public AddFavoriteViewModel(AppDatabase database, int favoriteId) {
        favorite = database.favoriteDao().loadFavoriteById(favoriteId);
    }


    public LiveData<FavoriteEntry> getFavorite() {
        return favorite;
    }
}