//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: Payment.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    // Fields representing payment information
    private int paymentPK;           // Primary key for the payment
    private int reservationPK;       // Foreign key linking to a Reservation
    private double amount;           // Payment amount
    private String creditCardNumber; // CC number (String provides flexibility for leading 0's and other validation)

    // Default constructor
    public Payment() {
    }

    // Full-argument constructor
    public Payment(int paymentPK, int reservationPK, double amount, String creditCardNumber) {
        this.paymentPK = paymentPK;
        this.reservationPK = reservationPK;
        this.amount = amount;
        this.creditCardNumber = creditCardNumber;
    }

    // Getters and Setters

    public int getPaymentPK() {
        return paymentPK;
    }

    public void setPaymentPK(int paymentPK) {
        this.paymentPK = paymentPK;
    }

    public int getReservationPK() {
        return reservationPK;
    }

    public void setReservationPK(int reservationPK) {
        this.reservationPK = reservationPK;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Validates the credit card number.
     * For simplicity, a valid credit card number must be exactly 5 digits.
     * 
     * @return true if the credit card number is exactly 5 digits; false otherwise.
     */
    public boolean isValidCreditCard() {
        if (creditCardNumber == null) {
            return false;
        }

        // Check if the length is exactly 5 and all characters are digits
        return creditCardNumber.matches("\\d{5}");
    }
}