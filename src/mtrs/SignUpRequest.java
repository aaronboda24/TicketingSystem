//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: SignUpRequest.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.Serializable;

@SuppressWarnings("serial")
public class SignUpRequest implements Serializable {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;

    public SignUpRequest(String username, String password, String firstName,
            String lastName, String email, String address, String phone) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    // Getter methods
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}