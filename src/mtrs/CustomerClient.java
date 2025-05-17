//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: CustomerClient.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CustomerClient extends JFrame {

    // Host and port for the server connection.
    private String hostname;
    private int port;

    // To hold the logged-in customer’s username.
    private String currentUsername;
    private UserProfile currentUserProfile;

    // GUI components for the login screen.
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    // Main panel uses CardLayout to switch between views.
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // A status label for providing feedback.
    private JLabel statusLabel;


    /**
     * Initializes the CustomerClient application.
     * - Sets up the main window with appropriate size, title, and close behavior.
     * - Creates and manages the main panel using CardLayout for navigation.
     * - Builds login and signup panels for user authentication.
     * - Adds a status label at the bottom for displaying messages.
     * - Makes the application visible after setup.
     *
     * @param hostname The server hostname for communication.
     * @param port The server port number for connection.
     */
    public CustomerClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        setTitle("Movie Ticket Reservation System - Customer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Set up main panel.
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Build the login panel.
        JPanel loginPanel = buildLoginPanel();
        mainPanel.add(loginPanel, "login");

        // Build signup panel.
        mainPanel.add(buildSignupPanel(), "signup");

        // Initialize the status label.
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(mainPanel);
        add(statusLabel, BorderLayout.SOUTH);
        setVisible(true);
    }


    /**
     * Builds the login panel for user authentication.
     * - Creates a structured UI using GridBagLayout with fields for username and password.
     * - Provides login and sign-up buttons for user interaction.
     * - Handles login logic by sending credentials to the server and verifying the response.
     * - If login is successful, transitions to the dashboard view.
     * - Allows users to navigate to the sign-up panel for new account creation.
     *
     * @return A JPanel containing login fields and action buttons.
     */
    private JPanel buildLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);
        gbc.gridx = 1;
        loginPanel.add(signupButton, gbc);

        // Login action.
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Gracefully validate inputs.
                if (username.isEmpty() || password.isEmpty()) {
                    updateStatus("Username or Password cannot be empty.");
                    return;
                }
                
                // Create a LoginRequest object (assumed to exist).
                LoginRequest loginReq = new LoginRequest(username, password, "customer");

                // Send login request to server.
                String response = sendMessageToServer("LOGIN_USER", loginReq);
                updateStatus(response);

                // If login is successful, remember the username and switch view.
                if (response.toLowerCase().contains("success")) {
                    currentUsername = loginReq.getUsername();
                    currentUserProfile = fetchUserProfileFromServer(currentUsername);

                    // Create dashboard panel since login was successful
                    JPanel dashboardPanel = buildDashboardPanel();
                    mainPanel.add(dashboardPanel, "dashboard");
                    switchCard("dashboard");
                }
            }
        });

        // Sign-up action.
        signupButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                switchCard("signup");
            }
        });

        return loginPanel;
    }


    /**
     * Builds the sign-up panel for new user registration.
     * - Creates a structured UI using GridBagLayout with fields for user details.
     * - Includes validation checks for required fields, username format, phone number, and address.
     * - Sends the sign-up request to the server upon form submission.
     * - Displays feedback messages for successful registration or validation errors.
     * - Transitions to the login screen upon successful sign-up.
     *
     * @return A JPanel containing sign-up fields and a submit button.
     */
    private JPanel buildSignupPanel() {
        JPanel signupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username:
        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(new JLabel("Username:"), gbc);
        JTextField signupUsername = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(signupUsername, gbc);

        // Password:
        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(new JLabel("Password:"), gbc);
        JPasswordField signupPassword = new JPasswordField(15);
        gbc.gridx = 1;
        signupPanel.add(signupPassword, gbc);

        // First Name:
        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(new JLabel("First Name:"), gbc);
        JTextField firstNameField = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(firstNameField, gbc);

        // Last Name:
        gbc.gridx = 0;
        gbc.gridy = 3;
        signupPanel.add(new JLabel("Last Name:"), gbc);
        JTextField lastNameField = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(lastNameField, gbc);

        // Email:
        gbc.gridx = 0;
        gbc.gridy = 4;
        signupPanel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(emailField, gbc);

        // Address:
        gbc.gridx = 0;
        gbc.gridy = 5;
        signupPanel.add(new JLabel("Address:"), gbc);
        JTextField addressField = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(addressField, gbc);

        // Phone:
        gbc.gridx = 0;
        gbc.gridy = 6;
        signupPanel.add(new JLabel("Phone:"), gbc);
        JTextField phoneField = new JTextField(15);
        gbc.gridx = 1;
        signupPanel.add(phoneField, gbc);

        // Submit Signup Button:
        JButton submitSignup = new JButton("Submit Signup");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        signupPanel.add(submitSignup, gbc);

        // Action listener for the submit button.
        submitSignup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Retrieve and trim all user inputs.
                String username = signupUsername.getText().trim();
                String password = new String(signupPassword.getPassword());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();

                // Check for empty fields
                List<String> emptyFields = new ArrayList<>();
                if (username.isEmpty()) emptyFields.add("Username");
                if (password.isEmpty()) emptyFields.add("Password");
                if (firstName.isEmpty()) emptyFields.add("First Name");
                if (lastName.isEmpty()) emptyFields.add("Last Name");
                if (email.isEmpty()) emptyFields.add("Email");
                if (address.isEmpty()) emptyFields.add("Address");
                if (phone.isEmpty()) emptyFields.add("Phone");

                // If multiple fields are empty, show a generic error message
                if (emptyFields.size() > 1) {
                    updateStatus("Please fill in all required fields.");
                    return;
                }
                // If only one field is empty, show a specific error message
                else if (!emptyFields.isEmpty()) {
                    updateStatus(emptyFields.get(0) + " cannot be empty.");
                    return;
                }

                // Validate username: must start with a letter and contain only letters/numbers
                if (!username.matches("^[A-Za-z][A-Za-z0-9]*$")) {
                    updateStatus("Username must start with a letter and contain only letters and numbers.");
                    return;
                }

                // Validate first name: must contain only letters
                if (!firstName.matches("^[A-Za-z]+$")) {
                    updateStatus("First name must contain only letters.");
                    return;
                }

                // Validate last name: must contain only letters
                if (!lastName.matches("^[A-Za-z]+$")) {
                    updateStatus("Last name must contain only letters.");
                    return;
                }

                // Validate phone number: must contain only digits
                if (!phone.matches("^\\d{1,15}$")) {
                    updateStatus("Phone number must contain only digits and be at most 15 digits long.");
                    return;
                }

                // Address must contain at least one letter or digit (to prevent nonsense input)
                if (!address.matches("^(?=.*[a-zA-Z0-9]).{5,}$")) {
                    updateStatus("Please enter a valid address.");
                    return;
                }

                // Validate email address: allow only letters, digits, '@', and '.'; must include at least one '@' and one '.'
                if (!email.matches("^(?=[A-Za-z0-9@.]+$)(?=.*[@])(?=.*\\.).{5,}$")) {
                    updateStatus("Please enter a valid email address.");
                    return;
                }

                // Create SignUpRequest and send to server
                SignUpRequest signupReq = new SignUpRequest(username, password, firstName, lastName, email, address, phone);
                String response = sendMessageToServer("SIGNUP_USER", signupReq);

                // Show server response
                updateStatus(response);

                // If signup is successful, return to login screen
                if (response.toLowerCase().contains("successful")) {
                    switchCard("login");
                }
            }
        });

        return signupPanel;
    }


    /**
     * Builds the dashboard panel containing various customer features.
     * - Uses a tabbed interface to organize different sections, including movies, shows, booking, reservations, and profile.
     * - Dynamically adds the profile tab if user information is available.
     * - Structures the main panel with BorderLayout to accommodate the tabbed view.
     *
     * @return A JPanel containing a tabbed interface for customer interaction.
     */
    private JPanel buildDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // List movies tab.
        JPanel listMoviesPanel = buildListMoviesPanel();
        tabbedPane.addTab("List Movies", listMoviesPanel);

        // List shows tab.
        JPanel listShowsPanel = buildListShowsPanel();
        tabbedPane.addTab("List Shows", listShowsPanel);

        // Booking tab.
        JPanel browseBookPanel = buildBrowseBookPanel();
        tabbedPane.addTab("Booking", browseBookPanel);

        // Reservations tab.
        JPanel reservationsPanel = buildReservationsPanel();
        tabbedPane.addTab("Reservations", reservationsPanel);

        // Profile tab.
        if (currentUserProfile != null) {
            JPanel profilePanel = buildProfilePanel(currentUserProfile);
            tabbedPane.addTab("Profile", profilePanel);
        }

        dashboard.add(tabbedPane, BorderLayout.CENTER);
        return dashboard;
    }


    /**
     * Builds the movie listing panel for displaying available movies.
     * - Uses a table to present movie details including title, rating, and description.
     * - Provides a refresh button to update the movie list dynamically.
     * - Fetches movie data from the server and populates the table upon refresh.
     * - Automatically refreshes the list when the panel is loaded.
     *
     * @return A JPanel containing a movie table and refresh functionality.
     */
    private JPanel buildListMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Set up the table model and table.
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable moviesTable = new JTable(tableModel);
        tableModel.addColumn("Movie");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Description");

        // Create a panel for the refresh button.
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Movies");
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(moviesTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Define the action to refresh the movies list.
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                List<MovieItem> movies = fetchMoviesFromServer();
                if (movies != null) {
                    for (MovieItem movie : movies) {
                        String name  = movie.getMovieName();
                        double rating  = movie.getRating();
                        String description = movie.getDescription();
                        tableModel.addRow(new Object[]{ name, rating, description });
                    }
                }
            }
        });

        // Trigger a refresh when the panel is loaded.
        refreshButton.doClick();

        return panel;
    }


    /**
     * Builds the show listing panel for displaying available movie showtimes.
     * - Uses a table to present details such as movie title, date & time, room, seats, and price.
     * - Provides a refresh button to dynamically update the show list.
     * - Fetches show data from the server and populates the table upon refresh.
     * - Automatically refreshes the list when the panel is loaded.
     *
     * @return A JPanel containing a show table and refresh functionality.
     */
    private JPanel buildListShowsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Set up the table model and table.
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable showsTable = new JTable(tableModel);
        tableModel.addColumn("Movie");
        tableModel.addColumn("Date & Time");
        tableModel.addColumn("Room");
        tableModel.addColumn("Seats");
        tableModel.addColumn("Price");

        // Create a panel for the refresh button.
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Shows");
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(showsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Define the action to refresh the shows list.
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0); // Clear existing rows.
                List<ShowItem> shows = fetchShowsFromServer();
                if (shows != null) {
                    for (ShowItem show : shows) {
                        String movie = show.getMovieTitle();
                        String dateTime = show.getShowDate().toString() + " " +
                                show.getShowTime().toString().substring(0, 5);
                        int room = show.getRoomNumber();
                        int seats = show.getAvailableSeats();
                        double price = show.getPrice();

                        tableModel.addRow(new Object[] { movie, dateTime, room, seats, price });
                    }
                }
            }
        });

        // Trigger a refresh when the panel is loaded.
        refreshButton.doClick();

        return panel;
    }


    /**
     * Builds the profile panel to display user information.
     * - Uses GridBagLayout to neatly arrange user details.
     * - Displays username, email, first name, last name, address, and phone number.
     * - Structures components with appropriate spacing for readability.
     *
     * @param profile The UserProfile object containing user details.
     * @return A JPanel displaying the user's profile information.
     */
    private JPanel buildProfilePanel(UserProfile profile) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getUsername()), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getEmail()), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getFirstName()), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getLastName()), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getAddress()), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(profile.getPhone()), gbc);

        panel.add(infoPanel, BorderLayout.NORTH);
        return panel;
    }


    /**
     * Switches the displayed card in the main panel.
     * - Uses CardLayout to show the specified panel based on its name.
     * - Enables seamless navigation between different views in the application.
     *
     * @param cardName The name of the card (panel) to display.
     */
    private void switchCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }


    /**
     * Sends a request to the server and retrieves the response.
     * - Establishes a socket connection to the specified hostname and port.
     * - Sends the request type and payload to the server.
     * - Reads and returns the server's response.
     * - Handles connection errors and ensures proper cleanup of resources.
     *
     * @param requestType The type of request being sent.
     * @param payload The request data being sent to the server.
     * @return The response message received from the server.
     */
    private String sendMessageToServer(String requestType, Object payload) {
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        String response = "";

        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send request type and payload to server.
            outputStream.writeObject(requestType);
            outputStream.writeObject(payload);
            outputStream.flush();

            // Receive response from server.
            response = (String) inputStream.readObject();
        } catch (ConnectException ex) {
            // Handle server connection failure gracefully.
            System.err.println("Error: Unable to connect to the server. Ensure the server is running and try again.");
            JOptionPane.showMessageDialog(null, "Server is unreachable. Please check that it is online and restart the client.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            response = "Error: Server is unavailable. Try again later.";
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error communicating with the server: " + ex.getMessage());
            response = "Error: Communication failure with the server.";
        } finally {
            // Ensure all resources are properly closed.
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
        return response;
    }


    /**
     * Builds the booking panel for reserving movie tickets.
     * - Provides dropdown selection for available shows.
     * - Includes input fields for ticket quantity and credit card payment.
     * - Handles ticket booking by sending requests to the server.
     * - Updates available shows dynamically after booking.
     *
     * @return A JPanel containing booking fields and functionality.
     */
    private JPanel buildBrowseBookPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Booking Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Show:"), gbc);

        JComboBox<ShowItem> showComboBox = new JComboBox<>();
        updateShowComboBox(showComboBox);
        gbc.gridx = 1;
        panel.add(showComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Number of Tickets:"), gbc);
        JTextField numTicketsField = new JTextField(5);
        gbc.gridx = 1;
        panel.add(numTicketsField, gbc);

        // Payment Section
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Credit Card:"), gbc);
        JTextField cardField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(cardField, gbc);

        // Book Ticket and Pay Button.
        JButton bookAndPayButton = new JButton("Book & Pay");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(bookAndPayButton, gbc);

        bookAndPayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ShowItem selectedShow = (ShowItem) showComboBox.getSelectedItem();
                    if (selectedShow == null) {
                        updateStatus("No show selected!");
                        JOptionPane.showMessageDialog(CustomerClient.this, "No show selected!");
                        return;
                    }

                    // Validate that the ticket amount is numeric and positive.
                    String numTicketsText = numTicketsField.getText().trim();
                    if (!numTicketsText.matches("^\\d+$")) {
                        updateStatus("Number of tickets must be a valid number.");
                        return;
                    }

                    int numTickets = Integer.parseInt(numTicketsText);
                    if (numTickets <= 0) {
                        updateStatus("Please enter a valid number of tickets.");
                        return;
                    }

                    // Ensure that requested tickets do not exceed the available seats.
                    if (numTickets > selectedShow.getAvailableSeats()) {
                        updateStatus("Number of tickets cannot exceed available seats (" 
                                + selectedShow.getAvailableSeats() + ").");
                        return;
                    }

                    // Validate credit card number: exactly 5 digits only.
                    String cardNumber = cardField.getText().trim();
                    if (!cardNumber.matches("^\\d{5}$")) {
                        updateStatus("Credit card number must be numeric and exactly 5 digits.");
                        return;
                    }

                    // Create a combined booking request with payment details.
                    BookingRequest bookingReq = new BookingRequest(selectedShow.getShowID(), numTickets, 
                            currentUsername, cardNumber);
                    String response = sendMessageToServer("BOOK_TICKET", bookingReq);
                    updateStatus(response);
                    updateShowComboBox(showComboBox);
                } catch (Exception ex) {
                    updateStatus("Input error: " + ex.getMessage());
                }
            }
        });

        return panel;
    }


    /**
     * Builds the reservations panel for customers to manage their bookings.
     * - Displays a table listing reservation details including ID, show info, ticket count, and reservation time.
     * - Provides a refresh button to update the list dynamically.
     * - Allows users to cancel a selected reservation with a confirmation prompt.
     * - Updates the reservation list upon cancellation to reflect changes.
     *
     * @return A JPanel containing the reservation table and action buttons.
     */
    private JPanel buildReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and JTable for listing reservations.
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable reservationsTable = new JTable(tableModel);
        tableModel.addColumn("Reservation ID");
        tableModel.addColumn("Show Details");
        tableModel.addColumn("Tickets");
        tableModel.addColumn("Reservation Time");

        // Create a panel at the bottom for buttons.
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Reservations");
        JButton cancelButton = new JButton("Cancel Reservation");
        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);

        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action to refresh the reservation list.
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                List<ReservationItem> reservations = fetchReservationsForCustomer(currentUsername);
                if (reservations != null) {
                    for (ReservationItem res : reservations) {
                        tableModel.addRow(new Object[]{
                                res.getReservationID(),
                                res.getShowDetails(),
                                res.getNumTickets(),
                                res.getReservationTime()
                        });
                    }
                }
            }
        });

        // Trigger a refresh on load.
        refreshButton.doClick();

        // Action to cancel the selected reservation.
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = reservationsTable.getSelectedRow();
                if (selectedRow < 0) {
                    updateStatus("Please select a reservation to cancel.");
                    // JOptionPane.showMessageDialog(CustomerClient.this, "Please select a reservation to cancel.");
                    return;
                }
                // Assuming the first column is the reservation ID.
                int reservationID = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        CustomerClient.this,
                        "Are you sure you want to cancel reservation " + reservationID + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION
                        );
                if (confirm == JOptionPane.YES_OPTION) {
                    // Send cancel command to the server.
                    String response = sendMessageToServer("CANCEL_RESERVATION", reservationID);
                    updateStatus(response);
                    // JOptionPane.showMessageDialog(CustomerClient.this, response);
                    // Refresh reservations after cancellation.
                    refreshButton.doClick();
                }
            }
        });

        return panel;
    }


    /**
     * Updates the show selection dropdown with available shows.
     * - Fetches the list of available shows from the server.
     * - Clears existing items in the combo box.
     * - Populates the combo box with the newly retrieved show items.
     *
     * @param comboBox The JComboBox to be updated with available shows.
     */
    private void updateShowComboBox(JComboBox<ShowItem> comboBox) {
        List<ShowItem> shows = fetchAvailableShowsFromServer();
        comboBox.removeAllItems();
        if (shows != null) {
            for (ShowItem si : shows) {
                comboBox.addItem(si);
            }
        }
    }


    /**
     * Fetches a list of movies from the server.
     * - Establishes a socket connection to communicate with the server.
     * - Sends a request to retrieve movie data.
     * - Parses the received list of MovieItem objects.
     * - Handles exceptions and ensures proper resource cleanup.
     *
     * @return A list of MovieItem objects representing available movies.
     */
    @SuppressWarnings("unchecked")
    private List<MovieItem> fetchMoviesFromServer() {
        List<MovieItem> movies = new ArrayList<>();
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send the GET_MOVIES command
            outputStream.writeObject("GET_MOVIES");
            outputStream.flush();

            // Read the list of MovieItem objects from the server.
            Object result = inputStream.readObject();
            if (result instanceof List) {
                movies = (List<MovieItem>) result;
            }
        } catch (Exception ex) {
            updateStatus("Error fetching movies: " + ex.getMessage());
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return movies;
    }


    /**
     * Fetches a list of available shows from the server.
     * - Establishes a socket connection for communication.
     * - Sends a request to retrieve show data.
     * - Parses the received list of ShowItem objects.
     * - Handles exceptions and ensures proper resource cleanup.
     *
     * @return A list of ShowItem objects representing available shows.
     */
    @SuppressWarnings("unchecked")
    private List<ShowItem> fetchShowsFromServer() {
        List<ShowItem> shows = new ArrayList<>();
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send the GET_SHOWS command.
            outputStream.writeObject("GET_SHOWS");
            outputStream.flush();

            // Read the list of ShowItem objects from the server.
            Object result = inputStream.readObject();
            if (result instanceof List) {
                shows = (List<ShowItem>) result;
            }
        } catch (Exception ex) {
            updateStatus("Error fetching shows: " + ex.getMessage());
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return shows;
    }


    /**
     * Fetches the list of available shows from the server.
     * - Establishes a socket connection to send a request.
     * - Sends a command to retrieve available show data.
     * - Parses and returns the list of ShowItem objects.
     * - Ensures proper resource cleanup in case of failure.
     *
     * @return A list of ShowItem objects representing available shows.
     */
    @SuppressWarnings({ "unchecked" })
    private List<ShowItem> fetchAvailableShowsFromServer() {
        List<ShowItem> shows = null;
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send the GET_AVAILABLE_SHOWS command.
            outputStream.writeObject("GET_AVAILABLE_SHOWS");
            outputStream.flush();

            // Read the object from the server.
            Object result = inputStream.readObject();
            if (result instanceof List) {
                shows = (List<ShowItem>) result;
            }
        } catch(Exception ex) {
            // TODO: show an error message.
        } finally {
            try {
                if (outputStream != null) { outputStream.close(); }
                if (inputStream != null) { inputStream.close(); }
                if (socket != null && !socket.isClosed()) { socket.close(); }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return shows;
    }


    /**
     * Fetches user profile information from the server.
     * - Establishes a socket connection and sends a request for user details.
     * - Reads and returns a UserProfile object if successfully retrieved.
     * - Handles exceptions and ensures proper cleanup of resources.
     *
     * @param username The username of the user whose profile is being requested.
     * @return A UserProfile object containing user details, or null if retrieval fails.
     */
    private UserProfile fetchUserProfileFromServer(String username) {
        UserProfile profile = null;
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;

        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject("GET_USER_INFO");
            outputStream.writeObject(username);
            outputStream.flush();

            // Retrieve the UserProfile object from the server.
            Object result = inputStream.readObject();
            if (result instanceof UserProfile) {
                profile = (UserProfile) result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: update a status label here to reflect the error.
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return profile;
    }


    /**
     * Fetches the reservations for a specific customer from the server.
     * - Establishes a socket connection for communication.
     * - Sends a request with the username to filter reservations.
     * - Parses and returns a list of ReservationItem objects.
     * - Handles exceptions and ensures proper resource cleanup.
     *
     * @param username The username of the customer whose reservations are being fetched.
     * @return A list of ReservationItem objects representing the customer's reservations.
     */
    @SuppressWarnings("unchecked")
    private List<ReservationItem> fetchReservationsForCustomer(String username) {
        List<ReservationItem> reservations = null;
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send the command and the username for filtering.
            outputStream.writeObject("GET_CUSTOMER_RESERVATIONS");
            outputStream.writeObject(username);
            outputStream.flush();

            Object result = inputStream.readObject();
            if (result instanceof List) {
                reservations = (List<ReservationItem>) result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (outputStream != null) { outputStream.close(); }
                if (inputStream != null) { inputStream.close(); }
                if (socket != null && !socket.isClosed()) { socket.close(); }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return reservations;
    }


    /**
     * Updates the status label with a given message.
     * - Sets the text of the status label to display relevant feedback.
     *
     * @param message The message to display on the status label.
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }


    /**
     * Main entry point for launching the CustomerClient application.
     * - Defines the SQL server hostname and port.
     * - Uses SwingUtilities to ensure proper UI initialization on the Event Dispatch Thread.
     * - Creates an instance of CustomerClient to connect to the movie ticket reservation system.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        final String SQL_HOST = "127.0.0.1";
        final int SQL_PORT = 8000;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CustomerClient(SQL_HOST, SQL_PORT);
            }
        });
    }
}