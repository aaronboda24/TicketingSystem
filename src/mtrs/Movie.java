//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: Movie.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    // Fields representing movie information
    private int moviePK;    // Primary key for the movie record
    private String title;   // Title of the movie
    private int rating;     // Rating (1-5 stars)
    private String info;    // Description of the movie

    // Default constructor
    public Movie() {
    }

    // Full-argument constructor
    public Movie(int moviePK, String title, int rating, String info) {
        this.moviePK = moviePK;
        this.title = title;
        this.rating = rating;
        this.info = info;
    }

    // Getters and Setters

    public int getMoviePK() {
        return moviePK;
    }

    public void setMoviePK(int moviePK) {
        this.moviePK = moviePK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the movie rating.
     * @return Movie rating on a scale of 1 to 5.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the movie rating.
     * This method assumes the rating is between 1 and 5.
     * @param rating New rating value.
     */
    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String review) {
        this.info = review;
    }
}