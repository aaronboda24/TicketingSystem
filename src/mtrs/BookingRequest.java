//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: BookingRequest.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

@SuppressWarnings("serial")
public class BookingRequest implements Serializable {
    private int showID;
    private int numTickets;
    private String username;
    private String creditCardNumber;  // New field for payment info

    public BookingRequest(int showID, int numTickets, String username, String creditCardNumber) {
        this.showID = showID;
        this.numTickets = numTickets;
        this.username = username;
        this.creditCardNumber = creditCardNumber;
    }

    public int getShowID() {
        return showID;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public String getUsername() {
        return username;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }
}