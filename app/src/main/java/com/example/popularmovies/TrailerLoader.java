package com.example.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.net.URL;

public class TrailerLoader {

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] reviewData = null;

            @Override
            protected void onStartLoading(){
                if(reviewData != null){
                    deliverResult(reviewData);
                } else {
                    //mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }
            @Override
            public String[] loadInBackground() {

                URL reviewRequestUrl = NetworkUtils.buildUrl(reviewQuery);
                Log.v("LOG", "query: " + reviewQuery);

                try {
                    String jsonReviewResponse = NetworkUtils
                            .getResponseFromHttpUrl(reviewRequestUrl);

                    String[] simpleJsonReviewData = OpenMoviesJsonUtils
                            .getTrailerDataFromJson(DetailActivity.this, jsonReviewResponse);

                    return simpleJsonReviewData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                reviewData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        //mLoadingIndicator.setVisibility(View.INVISIBLE);
        mReviewAdapter = new ReviewAdapter(this, data);
        listView = findViewById(R.id.review_list);
        listView.setAdapter(mReviewAdapter);
        if (null == data) {
            showErrorMessage();
        } else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }
}
