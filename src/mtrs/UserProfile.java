//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: UserProfile.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserProfile implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    public UserProfile(String username, String email, String firstName, String lastName, String address, String phone) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\n" +
                "Email: " + email + "\n" +
                "First Name: " + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Address: " + address + "\n" +
                "Phone: " + phone;
    }
}