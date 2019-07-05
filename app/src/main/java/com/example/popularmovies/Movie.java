package com.example.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable {

    private final String movieId;
    private final String movieName;
    private final String releaseDate;
    private final String description;
    private final String image;
    private final String userRating;
    final List<Review> reviewList = new ArrayList<>();
    final List<String> trailerList = new ArrayList<>();

    private Movie(Parcel in) {
        movieId = in.readString();
        movieName = in.readString();
        releaseDate = in.readString();
        description = in.readString();
        image = in.readString();
        userRating = in.readString();
    }

    public Movie(String id, String movieName, String releaseDate, String description, String image, String userRating) {
        this.movieId = id;
        this.movieName = movieName;
        this.description = description;
        this.releaseDate = releaseDate;
        this.image = image;
        this.userRating = userRating;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getUserRating(){
        return userRating;
    }

    public String toString(){
        return "Movie Name: + " + movieName + "\n" +
                "description: " + description + "\n" +
                "release date: " + releaseDate + "\n" +
                "image: " + image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<Movie> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieId);
        parcel.writeString(movieName);
        parcel.writeString(releaseDate);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(userRating);
    }

    public void setReviewList(List<Review> list) {
        reviewList.addAll(list);
    }

    public void setTrailerData(List<String> trailerData){
        trailerList.addAll(trailerData);
    }
}