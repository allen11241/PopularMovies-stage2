package com.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.popularmovies.MovieAdapter.MovieAdapterOnClickHandler;
import android.support.v7.widget.RecyclerView;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

        private RecyclerView mRecyclerView;
        private MovieAdapter mMovieAdapter;
        private TextView mErrorMessageDisplay;
        private ProgressBar mLoadingIndicator;
        private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
        private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        int num_of_columns = 2;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            num_of_columns = 3;
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, num_of_columns, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LoaderManager.LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundleForLoader, callback);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            }
        });
    }

    @Override
    public void onClick(Movie movieInfo) {
        Class destinationActivity = DetailActivity.class;
        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra("Movie", movieInfo);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movieData = null;

            @Override
            protected void onStartLoading(){
                if(movieData != null){
                    deliverResult(movieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }
            @Override
            public List<Movie> loadInBackground() {

                String apiQuery = MoviePreferences
                        .getPreferredMovieSort(MainActivity.this);

                URL movieRequestUrl = NetworkUtils.buildUrl(apiQuery);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    return OpenMoviesJsonUtils
                            .getMovieDataFromJson(MainActivity.this, jsonMovieResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(List<Movie> data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovieData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        else if(id == R.id.action_favorites){
            Intent startFavoritesActivity = new Intent(this, FavoritesActivity.class);
            startActivity(startFavoritesActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}