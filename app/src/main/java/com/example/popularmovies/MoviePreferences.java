package com.example.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

class MoviePreferences {

    public static String getPreferredMovieSort(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForMovieSort = context.getString(R.string.pref_key);
        String defaultMovieSort = context.getString(R.string.pref_popular_value);
        return prefs.getString(keyForMovieSort, defaultMovieSort);
    }
}