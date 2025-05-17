//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: MovieItem.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

@SuppressWarnings("serial")
public class MovieItem implements Serializable {
    private int movieID;
    private String movieName;
    private double rating;
    private String description;

    public MovieItem(int movieID, String movieName, double rating, String description) {
        this.movieID = movieID;
        this.movieName = movieName;
        this.rating = rating;
        this.description = description;
    }

    public int getMovieID() {
        return movieID;
    }

    public String getMovieName() {
        return movieName;
    }
    
    public double getRating() {
        return rating;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return movieName;
    }
}