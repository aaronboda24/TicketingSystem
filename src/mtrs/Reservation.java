//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: Reservation.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;
import java.sql.Timestamp;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    // Fields representing a reservation record
    private int reservationPK;          // Primary key for the reservation record
    private int userPK;                 // Foreign key: identifies the RegisteredUser who made the reservation
    private int showPK;                 // Foreign key: identifies the Show that is reserved
    private int numberOfTickets;        // Number of tickets being reserved (if a reservation can include multiple tickets)
    private Timestamp reservationTime;  // Timestamp indicating when the reservation was made

    // Default constructor
    public Reservation() {
    }

    // Full-argument constructor
    public Reservation(int reservationPK, int userPK, int showPK, int numberOfTickets, Timestamp reservationTime) {
        this.reservationPK = reservationPK;
        this.userPK = userPK;
        this.showPK = showPK;
        this.numberOfTickets = numberOfTickets;
        this.reservationTime = reservationTime;
    }

    // Getters and Setters

    public int getReservationPK() {
        return reservationPK;
    }

    public void setReservationPK(int reservationPK) {
        this.reservationPK = reservationPK;
    }

    public int getUserPK() {
        return userPK;
    }

    public void setUserPK(int userPK) {
        this.userPK = userPK;
    }

    public int getShowPK() {
        return showPK;
    }

    public void setShowPK(int showPK) {
        this.showPK = showPK;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public Timestamp getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Timestamp reservationTime) {
        this.reservationTime = reservationTime;
    }
}