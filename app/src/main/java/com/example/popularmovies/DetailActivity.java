package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements
        MyTrailerAdapter.MyTrailerAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Movie> {

    private static final int DEFAULT_TASK_ID = -1;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int REVIEW_LOADER_ID = 0;
    private AppDatabase mDb;
    private String reviewQuery;
    private String trailerQuery;
    private Movie movie;
    private int index;

    private Toast mToast;
    private MyTrailerAdapter mTrailerAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private Button mButton;
    private TextView reviewAuthorTv;
    private TextView reviewContentTv;
    private TextView indexTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = this.getSupportActionBar();

        mRecyclerView = findViewById(R.id.trailer_recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new MyTrailerAdapter(this, this);
        mRecyclerView.setAdapter(mTrailerAdapter);

        index = 0;

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        movie = intent.getParcelableExtra("Movie");

        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(mDb, DEFAULT_TASK_ID);
        final AddFavoriteViewModel viewModel
                = ViewModelProviders.of(this, factory).get(AddFavoriteViewModel.class);

        viewModel.getFavorite().observe(this, new Observer<FavoriteEntry>() {
            @Override
            public void onChanged(@Nullable FavoriteEntry favoriteEntry) {
                viewModel.getFavorite().removeObserver(this);
            }
        });

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LoaderManager.LoaderCallbacks<Movie> callback = DetailActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, bundleForLoader, callback);

        populateMovieInfo(movie);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        checkIfFavorite();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Movie>(this) {

            final Movie movieData = null;

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    deliverResult(movieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Movie loadInBackground() {

                reviewQuery = getReviewQuery(movie);
                trailerQuery = getTrailerQuery(movie);
                URL reviewRequestUrl = NetworkUtils.buildUrl(reviewQuery);
                URL trailerRequestUrl = NetworkUtils.buildUrl(trailerQuery);

                try {
                    String jsonReviewResponse = NetworkUtils
                            .getResponseFromHttpUrl(reviewRequestUrl);

                    List<Review> simpleJsonReviewData = OpenMoviesJsonUtils
                            .getReviewDataFromJson(jsonReviewResponse);

                    String jsonTrailerResponse = NetworkUtils
                            .getResponseFromHttpUrl(trailerRequestUrl);

                    List<String> simpleJsonTrailerData = OpenMoviesJsonUtils
                            .getTrailerDataFromJson(DetailActivity.this, jsonTrailerResponse);

                    movie.setReviewList(simpleJsonReviewData);
                    movie.setTrailerData(simpleJsonTrailerData);

                    return movie;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Movie data) {
                movie = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mLoadingIndicator.setVisibility(View.GONE);

        if (data == null) {
            showErrorMessage();
        } else {
            mTrailerAdapter.setTrailerData(data.trailerList);
            populateReview(data);
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID, null, DetailActivity.this);
            }
        });
    }

    private void populateMovieInfo(Movie movie) {
        String imageURL = "https://image.tmdb.org/t/p/w500" + movie.getImage();
        ImageView posterIv = findViewById(R.id.iv_poster);
        Picasso.get().load(imageURL).into(posterIv);

        TextView reviewNameTv = findViewById(R.id.tv_movie_name);
        reviewNameTv.setText(movie.getMovieName());

        TextView userRatingTv = findViewById(R.id.tv_user_rating);
        userRatingTv.setText(movie.getUserRating());

        TextView releaseDateTv = findViewById(R.id.tv_release_date);
        releaseDateTv.setText(movie.getReleaseDate());

        TextView descriptionTv = findViewById(R.id.tv_description);
        descriptionTv.setText(movie.getDescription());

        mButton = findViewById(R.id.btn_add_fav);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setText("");
                mButton.setBackground(getResources().getDrawable(R.drawable.button_star));
                onAddFavButtonClicked();
            }
        });
    }

    private void onAddFavButtonClicked() {
        final FavoriteEntry favorite = new FavoriteEntry(movie.getMovieId(), movie.getMovieName(), movie.getReleaseDate(), movie.getImage(), movie.getDescription());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count;
                count = mDb.favoriteDao().findSpecificMovie(movie.getMovieId());

                if (count == 0) {
                    mDb.favoriteDao().insertFavorite(favorite);
                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast();
                        }
                    });
                }
            }

        });
    }

    private String getReviewQuery(Movie movie) {
        String id = movie.getMovieId();
        String QUERY_PREFIX = "https://api.themoviedb.org/3/movie/";
        String QUERY_SUFFIX = "/reviews?api_key=901ea24cdf15b2bb801af77bfadb4831";
        return QUERY_PREFIX + id + QUERY_SUFFIX;
    }

    private String getTrailerQuery(Movie movie) {
        String id = movie.getMovieId();
        String QUERY_PREFIX = "https://api.themoviedb.org/3/movie/";
        String QUERY_SUFFIX = "/videos?api_key=901ea24cdf15b2bb801af77bfadb4831";
        return QUERY_PREFIX + id + QUERY_SUFFIX;
    }

    private void populateReview(Movie data) {

        reviewAuthorTv = findViewById(R.id.review_author);
        reviewContentTv = findViewById(R.id.review_content);
        TextView authorLabelTv = findViewById(R.id.review_author_label);

        if (data.reviewList.isEmpty()) {
            reviewAuthorTv.setText("");
            authorLabelTv.setVisibility(View.INVISIBLE);
            reviewContentTv.setText(getResources().getString(R.string.no_reviews));
        } else {
            reviewAuthorTv.setText(data.reviewList.get(0).getAuthor());
            reviewContentTv.setText(data.reviewList.get(0).getContent());
        }

        ImageView btnPreviousReview = findViewById(R.id.btn_previous);
        btnPreviousReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAuthorListSize() == 0)
                    return;
                if (index > 0) {
                    index -= 1;
                    reviewAuthorTv.setText(getNextAuthorInfo());
                    reviewContentTv.setText(getNextReviewInfo());
                    int displayedIndex = index + 1;
                    indexTv.setText(String.format(Locale.getDefault(), "%d", displayedIndex));
                }
            }
        });

        indexTv = findViewById(R.id.tv_index);
        TextView reviewCountTv = findViewById(R.id.tv_review_count);
        reviewCountTv.setText(String.format(Locale.getDefault(), "%d", checkAuthorListSize()));

        if (checkAuthorListSize() == 0) {
            indexTv.setText(String.format(Locale.getDefault(), "%d", index));
        } else {
            indexTv.setText(String.format(Locale.getDefault(), "%d", index + 1));
        }

        ImageView btnNextReview = findViewById(R.id.btn_next);
        btnNextReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAuthorListSize() == 0) {
                    return;
                }
                index += 1;
                if (index < movie.reviewList.size()) {
                    reviewAuthorTv.setText(getNextAuthorInfo());
                    reviewContentTv.setText(getNextReviewInfo());
                    int displayedIndex = index + 1;
                    indexTv.setText(String.format(Locale.getDefault(), "%d", displayedIndex));
                }
            }
        });
    }

    private int checkAuthorListSize() {
        return movie.reviewList.size();
    }

    private String getNextAuthorInfo() {
        return movie.reviewList.get(index).getAuthor();
    }

    private String getNextReviewInfo() {
        return movie.reviewList.get(index).getContent();
    }

    private void checkIfFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count;
                count = mDb.favoriteDao().findSpecificMovie(movie.getMovieId());

                if (count == 1) {
                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mButton.setText("");
                            mButton.setBackground(getResources().getDrawable(R.drawable.button_star));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(String currentTrailer) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentTrailer));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + currentTrailer));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        movie.reviewList.clear();
        mTrailerAdapter.clearData();
    }

    private void showToast() {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout));
        layout.getBackground().setAlpha(222);

        TextView text = layout.findViewById(R.id.text);
        text.setText(getResources().getString(R.string.added_to_favorites));

        mToast = new Toast(getApplicationContext());
        mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(layout);
        mToast.show();
    }
}