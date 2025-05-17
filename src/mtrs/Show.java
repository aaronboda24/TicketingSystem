//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: Show.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class Show implements Serializable {

    private static final long serialVersionUID = 1L;

    // Fields representing a movie show
    private int showPK;         // Primary key for the show record
    private int moviePK;        // Foreign key: identifies the associated Movie
    private Date showDate;      // The date when the show is scheduled
    private Time showTime;      // The time when the show starts
    private int roomNumber;     // Room or auditorium number for the show
    private int availableSeats; // Seats available for booking (40 for each show)
    private double price;       // Ticket price for this particular show

    // Default constructor
    public Show() {
    }

    // Full-argument constructor
    public Show(int showPK, int moviePK, Date showDate, Time showTime, int roomNumber, int availableSeats, double price) {
        this.showPK = showPK;
        this.moviePK = moviePK;
        this.showDate = showDate;
        this.showTime = showTime;
        this.roomNumber = roomNumber;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Getters and Setters

    public int getShowPK() {
        return showPK;
    }

    public void setShowPK(int showPK) {
        this.showPK = showPK;
    }

    public int getMoviePK() {
        return moviePK;
    }

    public void setMoviePK(int moviePK) {
        this.moviePK = moviePK;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public Time getShowTime() {
        return showTime;
    }

    public void setShowTime(Time showTime) {
        this.showTime = showTime;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}