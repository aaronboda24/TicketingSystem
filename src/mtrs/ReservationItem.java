//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: ReservationItem.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReservationItem implements Serializable {
    private int reservationID;
    private String showDetails;
    private int numTickets;
    private String reservationTime;

    public ReservationItem(int reservationID, String showDetails, int numTickets, String reservationTime) {
        this.reservationID = reservationID;
        this.showDetails = showDetails;
        this.numTickets = numTickets;
        this.reservationTime = reservationTime;
    }

    public int getReservationID() {
        return reservationID;
    }

    public String getShowDetails() {
        return showDetails;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public String getReservationTime() {
        return reservationTime;
    }
}