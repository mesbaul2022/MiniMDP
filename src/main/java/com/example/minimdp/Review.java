package com.example.minimdp;

public class Review {
    private int id;
    private int movieId;
    private int userId;
    private int rating;        // 1..10
    private String comment;
    private String createdAt;


    private String movieTitle;
    private String userEmail;

    public Review(int id, int movieId, int userId, int rating, String comment, String createdAt) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }


    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public int getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
