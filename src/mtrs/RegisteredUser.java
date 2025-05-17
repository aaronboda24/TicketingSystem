//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: RegisteredUser.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

public class RegisteredUser implements Serializable {

    private static final long serialVersionUID = 1L;

    // Fields representing user information
    private int userPK;             // Primary Key for the user
    private String username;        // The login username
    private String password;        // The login password
    private String firstName;       // Customer's first name
    private String lastName;        // Customer's last name
    private String email;           // Email address for contact/offers
    private String address;         // Mailing address
    private String phone;           // Phone number

    // Default constructor
    public RegisteredUser() {
    }

    // Full-argument constructor for convenience
    public RegisteredUser(int userPK, String username, String password, 
            String firstName, String lastName, 
            String email, String address, String phone) {
        this.userPK = userPK;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters

    public int getUserPK() {
        return userPK;
    }

    public void setUserPK(int userPK) {
        this.userPK = userPK;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}