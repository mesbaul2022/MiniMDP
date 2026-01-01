package com.example.minimdp;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private Integer year;
    private String description;

    public Movie(int id, String title, String genre, Integer year, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.description = description;
    }
    public Movie(String title, String genre, Integer year, String description) {
        this(-1, title, genre, year, description);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public Integer getYear() { return year; }
    public String getDescription() { return description; }
}
