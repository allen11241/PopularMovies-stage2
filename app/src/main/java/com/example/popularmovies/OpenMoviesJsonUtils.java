package com.example.popularmovies;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

final class OpenMoviesJsonUtils {

    public static List<Movie> getMovieDataFromJson(Context context, String movieJsonStr) {

        List<Movie> movies;
        String movieId;
        String movieName;
        String releaseDate;
        String description;
        String image;
        String userRating;

        try {
                JSONObject reader = new JSONObject(movieJsonStr);
                JSONArray movieArray = reader.getJSONArray("results");
                movies = new ArrayList<>(movieArray.length());

                for(int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieInfo = movieArray.getJSONObject(i);
                    movieId = movieInfo.getString("id");
                    movieName = movieInfo.getString("title");
                    releaseDate = movieInfo.getString("release_date");
                    description = movieInfo.getString("overview");
                    image = movieInfo.getString("poster_path");
                    userRating = movieInfo.getString("vote_average");

                    Movie movie = new Movie(movieId, movieName, formatDate(context, releaseDate), description, image, userRating);
                    movies.add(movie);
            }
            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Review> getReviewDataFromJson(String reviewJsonStr) {

        List<Review> reviews;
        String reviewAuthor;
        String reviewContent;

        try {
            JSONObject reader = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reader.getJSONArray("results");
            reviews = new ArrayList<>(reviewArray.length());

            if(reviewArray.length() != 0){
                for(int i = 0; i < reviewArray.length(); i++) {
                    JSONObject reviewInfo = reviewArray.getJSONObject(i);
                    reviewAuthor = reviewInfo.getString("author");
                    reviewContent = reviewInfo.getString("content");
                    Review review = new Review(reviewAuthor, reviewContent);
                    reviews.add(review);
                }
            }
            return reviews;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getTrailerDataFromJson(Context context, String trailerJsonStr) {
        List<String> trailers;
        int numOfTrailers;

        try {
            JSONObject reader = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = reader.getJSONArray("results");
            trailers = new ArrayList<>(trailerArray.length());
            numOfTrailers = trailerArray.length();
            if(trailerArray.length() > 5)
                numOfTrailers = 5;
            if(trailerArray.length() == 0){
                trailers.add(0, context.getResources().getString(R.string.no_trailers));
            } else
            {
                for(int i = 0; i < numOfTrailers; i++) {
                    JSONObject trailerInfo = trailerArray.getJSONObject(i);
                    trailers.add(i, trailerInfo.getString("key"));
                }
            }
            return trailers;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String formatDate(Context context, String date){
        //Checks to see if there's a listed date.
        if(date.length() < 10){
            return context.getResources().getString(R.string.not_listed);
        } else {

            String month = date.substring(5,7);
            String day = date.substring(8,10);
            String year = date.substring(0,4);

            date = month + "/" + day + "/" + year;
            return date;
        }
    }
}
