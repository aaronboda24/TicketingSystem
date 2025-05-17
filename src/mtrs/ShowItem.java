//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: ShowItem.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@SuppressWarnings("serial")
public class ShowItem implements Serializable {
    private int showID;
    private String movieTitle;
    private Date showDate;
    private Time showTime;
    private int roomNumber;
    private int availableSeats;
    private double price;

    public ShowItem(int showID, String movieTitle, Date showDate, Time showTime, int roomNumber, int availableSeats, double price) {
        this.showID = showID;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.roomNumber = roomNumber;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public int getShowID() {
        return showID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Date getShowDate() {
        return showDate;
    }

    public Time getShowTime() {
        return showTime;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public String getDetails() {
        String formattedTime = showTime.toString().substring(0, 5);
        return movieTitle + " on " + showDate + " at " + formattedTime +
                " in Room " + roomNumber + " (Available: " + availableSeats +
                ", Price: $" + price + ")";
    }

    @Override
    public String toString() {
        return getDetails();
    }
}