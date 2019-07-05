package com.example.popularmovies;

class Review {
    private final String author;
    private final String content;

    public Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return "Author: " + author + "\n" + content;
    }
}
