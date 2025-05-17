//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: TicketReservationServer.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.List;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class TicketReservationServer {
    // Configuration for server and database connection
    private static final int SERVER_PORT        = 8000;
    private static final String DBSQL_URL       = "TBD";
    private static final String DBSQL_USER_NAME = "TBD";
    private static final String DBSQL_PASSWORD  = "TBD";

    // Constants
    private static final int SHOW_GAP_THRESHOLD_MINUTES = 180;

    private ServerSocket serverSocket;
    private Connection conn = null;
    private Statement stmt = null;


    /**
     * Initializes the Ticket Reservation Server.
     * - Ensures only one instance runs by checking if the port is already in use.
     * - Establishes a server socket to listen for client connections.
     * - Loads the MySQL JDBC driver and sets up a database connection.
     * - Handles errors gracefully, including server port conflicts and database connection failures.
     * 
     * @param port The port number on which the server listens for client connections.
     */
    public TicketReservationServer(int port) {
        try {
            // Check if the port is already in use before attempting to bind the server socket
            if (isPortOccupied(port)) {
                System.err.println("Error: Server is already running on port " + port);
                JOptionPane.showMessageDialog(null, "Server is already running. Please do not start it again.",
                        "Server Error", JOptionPane.ERROR_MESSAGE);
                return; // Prevent duplicate startup
            }

            // Create the server socket and listen on all interfaces
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Server is listening on port " + port);

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");

            // Establish a connection with the provided connection parameters
            String url = DBSQL_URL
                    + "useSSL=true"
                    + "&verifyServerCertificate=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&user="     + DBSQL_USER_NAME
                    + "&password=" + DBSQL_PASSWORD;
            conn = DriverManager.getConnection(url);

            // Create a statement object for executing SQL commands
            try {
                stmt = conn.createStatement();
                System.out.println("Database connection successful! Statement object: " + stmt);
            } catch (SQLException e) {
                System.err.println("Error creating Statement object: " + e.getMessage());
            }
        } catch (BindException e) {
            System.err.println("Error: Server is already running on this port.");
            JOptionPane.showMessageDialog(null, "Server is already running. Please do not start it again.",
                    "Server Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error initializing server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }


    /**
     * Checks whether the specified port is already in use.
     * - Attempts to create a socket connection to the given port.
     * - If successful, it means a server is already running on that port.
     * - If an IOException occurs, the port is available for use.
     *
     * @param port The port number to check.
     * @return True if the port is occupied (server running), false if it is available.
     */
    private boolean isPortOccupied(int port) {
        try (Socket _ = new Socket("localhost", port)) {
            return true;  // Server is already running
        } catch (IOException e) {
            return false; // No active server on this port
        }
    }


    /**
     * Starts the server to continuously listen for client connections.
     * - Accepts incoming client connections through the server socket.
     * - Each connected client is handled in a separate thread for concurrent processing.
     * - Runs indefinitely to serve multiple client requests.
     *
     * Note: If an IOException occurs during client acceptance, it is logged.
     */
    public void start() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                // For each incoming connection, handle client in a new thread.
                new Thread(new ClientHandler(socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Handles user login authentication.
     * - Checks the database for a matching username and password.
     * - Verifies the user's role to ensure correct access permissions.
     * - Returns a success message with the user's name for customer logins.
     * - Provides an appropriate error message for failed logins or role mismatches.
     *
     * @param loginRequest The login request containing username, password, and role.
     * @return A success or error message based on authentication results.
     */
    private String handleLoginUser(LoginRequest loginRequest) {
        try {
            String query = "SELECT role, firstName, lastName FROM registereduser WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, loginRequest.getUsername());
            pstmt.setString(2, loginRequest.getPassword());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedRole = rs.getString("role");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");

                // Compare the role provided in the login request with the stored role.
                if (storedRole.equalsIgnoreCase(loginRequest.getRole())) {
                    if ("admin".equalsIgnoreCase(storedRole)) {
                        return "Admin login successful!";
                    } else {
                        return "Login successful, welcome " + firstName + " " + lastName + ".";
                    }
                } else {
                    return "Error: Role mismatch.";
                }
            } else {
                return "Invalid username or password.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error during login: " + e.getMessage();
        }
    }


    /**
     * Adds a new movie to the database.
     * - Checks if a movie with the same title already exists to prevent duplicates.
     * - Inserts the movie into the database if no matching title is found.
     * - Returns success or error messages based on the operation outcome.
     *
     * @param movie The Movie object containing title, rating, and information.
     * @return A message indicating success or failure of the operation.
     */
    private String handleAddMovie(Movie movie) {
        try {
            // First, check if a movie with the same title already exists.
            String checkSQL = "SELECT COUNT(*) FROM movie WHERE title = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, movie.getTitle());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "Error: A movie with the title '" + movie.getTitle() + "' already exists.";
            }
            rs.close();
            checkStmt.close();

            // If not, then proceed to insert the movie.
            String insertSQL = "INSERT INTO movie (title, rating, info) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getRating());
            pstmt.setString(3, movie.getInfo());
            pstmt.executeUpdate();
            pstmt.close();

            return "Movie added successfully!";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error adding movie: " + ex.getMessage();
        }
    }


    /**
     * Retrieves a list of movies from the database.
     * - Executes a query to fetch movie details, including ID, title, rating, and description.
     * - Populates and returns a list of MovieItem objects.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @return A list of MovieItem objects representing available movies.
     */
    private List<MovieItem> handleGetMovies() {
        List<MovieItem> movies = new ArrayList<>();
        String query = "SELECT moviePK, title, rating, info FROM movie";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int movieId = rs.getInt("moviePK");
                String title = rs.getString("title");
                double rating = rs.getDouble("rating");
                String description = rs.getString("info");
                movies.add(new MovieItem(movieId, title, rating, description));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return movies;
    }


    /**
     * Retrieves a list of available shows from the database.
     * - Joins the "shows" table with the "movie" table to fetch show details.
     * - Extracts key information such as show ID, movie title, date, time, room, available seats, and price.
     * - Returns a list of ShowItem objects representing scheduled shows.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @return A list of ShowItem objects containing details of available shows.
     */
    private List<ShowItem> handleGetShows() {
        List<ShowItem> shows = new ArrayList<>();
        String query = "SELECT s.showPK, m.title, s.showDate, s.showTime, s.roomNumber, s.availableSeats, s.price " +
                "FROM shows s " +
                "JOIN movie m ON s.moviePK = m.moviePK";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int showId = rs.getInt("showPK");
                String movieTitle = rs.getString("title");
                Date showDate = rs.getDate("showDate");
                Time showTime = rs.getTime("showTime");
                int roomNumber = rs.getInt("roomNumber");
                int availableSeats = rs.getInt("availableSeats");
                double price = rs.getDouble("price");
                shows.add(new ShowItem(showId, movieTitle, showDate, showTime, roomNumber, availableSeats, price));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return shows;
    }


    /**
     * Adds a new show to the database while enforcing scheduling constraints.
     * - Ensures no existing show is scheduled in the same room within a 3-hour window.
     * - Inserts a new show record into the database if scheduling is valid.
     * - Returns a success message or an error if the schedule conflicts or insertion fails.
     *
     * @param show The Show object containing movie ID, date, time, room number, available seats, and price.
     * @return A message indicating the success or failure of the operation.
     */
    private String handleAddShow(Show show) {
        try {
            // Validate scheduling: check if there's an existing show in the same room and on the same date
            String checkSQL = "SELECT showtime FROM shows WHERE roomnumber = ? AND showdate = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, show.getRoomNumber());
            checkStmt.setDate(2, show.getShowDate());

            ResultSet rs = checkStmt.executeQuery();
            while (rs.next()) {
                Time existingTime = rs.getTime("showtime");
                // Convert both times to LocalTime for comparison.
                LocalTime newShowTime = show.getShowTime().toLocalTime();
                LocalTime existingShowTime = existingTime.toLocalTime();

                // Compute absolute difference in minutes.
                long diffMinutes = Math.abs(Duration.between(newShowTime, existingShowTime).toMinutes());

                // If the difference is less than 180 minutes (3 hours), block the insertion.
                if (diffMinutes < SHOW_GAP_THRESHOLD_MINUTES) {
                    rs.close();
                    checkStmt.close();
                    return "Error: Cannot schedule a show in room " + show.getRoomNumber() +
                            " within 3 hours of an existing show.";
                }
            }
            rs.close();
            checkStmt.close();

            // Prepare an SQL statement for inserting a new show record.
            String insertSQL = "INSERT INTO shows (moviepk, showdate, showtime, roomnumber, availableseats, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            pstmt.setInt(1, show.getMoviePK());
            pstmt.setDate(2, show.getShowDate());
            pstmt.setTime(3, show.getShowTime());
            pstmt.setInt(4, show.getRoomNumber());
            pstmt.setInt(5, show.getAvailableSeats());
            pstmt.setDouble(6, show.getPrice());

            // Execute the update.
            pstmt.executeUpdate();

            return "Show added successfully!";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error adding show: " + ex.getMessage();
        }
    }


    /**
     * Deletes a movie from the database along with its associated shows.
     * - First removes all show entries linked to the specified movie.
     * - Then deletes the movie itself from the database.
     * - Returns a success message if the deletion is successful, otherwise an error message.
     *
     * @param movieID The unique identifier of the movie to be deleted.
     * @return A message indicating success or failure of the deletion.
     */
    private String handleDeleteMovie(int movieID) {
        try {
            // Delete all associated shows first
            String deleteShowsSQL = "DELETE FROM shows WHERE moviePK = ?";
            PreparedStatement deleteShowsStmt = conn.prepareStatement(deleteShowsSQL);
            deleteShowsStmt.setInt(1, movieID);
            deleteShowsStmt.executeUpdate();
            deleteShowsStmt.close();

            // Delete the movie itself
            String deleteMovieSQL = "DELETE FROM movie WHERE moviePK = ?";
            PreparedStatement deleteMovieStmt = conn.prepareStatement(deleteMovieSQL);
            deleteMovieStmt.setInt(1, movieID);
            int affectedRows = deleteMovieStmt.executeUpdate();
            deleteMovieStmt.close();

            return (affectedRows > 0) ? "Movie and all associated shows deleted successfully!"
                    : "Error: Movie not found.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error deleting movie and associated shows: " + ex.getMessage();
        }
    }


    /**
     * Deletes a show from the database.
     * - Executes a DELETE operation based on the given show ID.
     * - Returns a success message if deletion occurs, otherwise an error message.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @param showID The unique identifier of the show to be deleted.
     * @return A message indicating success or failure of the deletion.
     */
    private String handleDeleteShow(int showID) {
        try {
            String deleteSQL = "DELETE FROM shows WHERE showPK = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteSQL);
            pstmt.setInt(1, showID);
            int affectedRows = pstmt.executeUpdate();
            pstmt.close();

            return (affectedRows > 0) ? "Show deleted successfully!" : "Error: Show not found.";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error deleting show: " + ex.getMessage();
        }
    }


    /**
     * Retrieves a list of available shows from the database.
     * - Filters shows to include only those with available seats.
     * - Joins the "shows" table with the "movie" table to fetch show and movie details.
     * - Returns a list of ShowItem objects representing shows that can still be booked.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @return A list of ShowItem objects containing details of available shows.
     */
    private List<ShowItem> handleGetAvailableShows() {
        List<ShowItem> shows = new ArrayList<>();
        String query = "SELECT s.showPK, s.availableSeats, s.price, m.title, s.showDate, s.showTime, s.roomNumber " +
                "FROM shows s JOIN movie m ON s.moviePK = m.moviePK " +
                "WHERE s.availableSeats > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int showId = rs.getInt("showPK");
                String movieTitle = rs.getString("title");
                Date showDate = rs.getDate("showDate");
                Time showTime = rs.getTime("showTime");
                int roomNumber = rs.getInt("roomNumber");
                int availableSeats = rs.getInt("availableSeats");
                double price = rs.getDouble("price");
                shows.add(new ShowItem(showId, movieTitle, showDate, showTime, roomNumber, availableSeats, price));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return shows;
    }


    /**
     * Handles ticket booking for a movie show.
     * - Validates that the show exists and has available seats.
     * - Ensures that the booking is made before the show time.
     * - Prevents users from reserving multiple shows that overlap in time.
     * - Deducts seats, records reservations, and processes payment.
     * - Uses transactions to maintain data integrity and rollbacks on failure.
     *
     * @param bookingReq The BookingRequest containing show ID, number of tickets, username, and payment details.
     * @return A message indicating success or failure of the booking and payment process.
     */
    private String handleBookTicket(BookingRequest bookingReq) {
        int showID = bookingReq.getShowID();
        int numTickets = bookingReq.getNumTickets();
        String username = bookingReq.getUsername();
        String cardNumber = bookingReq.getCreditCardNumber();

        try {
            // Begin transaction.
            conn.setAutoCommit(false);

            // Check available seats and get show details (date/time and room number).
            String checkSQL = "SELECT availableSeats, showdate, showtime, roomnumber FROM shows WHERE showPK = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, showID);
            ResultSet rs = checkStmt.executeQuery();

            java.sql.Date showDateSQL = null;
            java.sql.Time showTimeSQL = null;
            int availableSeats = 0;
            if (rs.next()) {
                availableSeats = rs.getInt("availableSeats");
                showDateSQL = rs.getDate("showdate");
                showTimeSQL = rs.getTime("showtime");
                // Combine SQL Date and Time into a LocalDateTime.
                LocalDate showDate = showDateSQL.toLocalDate();
                LocalTime showTime = showTimeSQL.toLocalTime();
                LocalDateTime showDateTime = LocalDateTime.of(showDate, showTime);

                // Ensure that the current local time is before the show time.
                if (LocalDateTime.now().isAfter(showDateTime)) {
                    rs.close();
                    checkStmt.close();
                    conn.rollback();
                    return "Error: Cannot book ticket; the show time has already passed.";
                }

                // Ensure that there are enough available seats.
                if (availableSeats < numTickets) {
                    rs.close();
                    checkStmt.close();
                    conn.rollback();
                    return "Error: Only " + availableSeats + " seats are available for this show.";
                }
            } else {
                conn.rollback();
                return "Error: Show not found.";
            }
            rs.close();
            checkStmt.close();

            // Get userPK based on the provided username.
            int userPK = 0;
            String userSQL = "SELECT userPK FROM registereduser WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSQL);
            userStmt.setString(1, username);
            ResultSet userRS = userStmt.executeQuery();
            if (userRS.next()) {
                userPK = userRS.getInt("userPK");
            } else {
                userRS.close();
                userStmt.close();
                conn.rollback();
                return "Error: User not found.";
            }
            userRS.close();
            userStmt.close();

            // Check if the user already has a reservation at the same time in the same room.
            String conflictSQL =
                    "SELECT COUNT(*) AS cnt " +
                            "FROM reservation r INNER JOIN shows s ON r.showpk = s.showPK " +
                            "WHERE r.userpk = ? AND s.showdate = ? AND s.showtime = ?";
            PreparedStatement conflictStmt = conn.prepareStatement(conflictSQL);
            conflictStmt.setInt(1, userPK);
            conflictStmt.setDate(2, showDateSQL);
            conflictStmt.setTime(3, showTimeSQL);
            ResultSet conflictRS = conflictStmt.executeQuery();
            if (conflictRS.next()) {
                int count = conflictRS.getInt("cnt");
                if (count > 0) {
                    conflictRS.close();
                    conflictStmt.close();
                    conn.rollback();
                    return "Error: You already have a reservation at the same time for this theater.";
                }
            }
            conflictRS.close();
            conflictStmt.close();

            // Deduct seats from the shows table.
            String updateSQL = "UPDATE shows SET availableSeats = availableSeats - ? WHERE showPK = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setInt(1, numTickets);
            updateStmt.setInt(2, showID);
            int rowsUpdated = updateStmt.executeUpdate();
            updateStmt.close();
            if (rowsUpdated <= 0) {
                conn.rollback();
                return "Error: Ticket booking failed during seat update.";
            }

            // Insert reservation record.
            String insertResSQL = "INSERT INTO reservation (userpk, showpk, numberoftickets, reservationtime) VALUES (?, ?, ?, NOW())";
            PreparedStatement resStmt = conn.prepareStatement(insertResSQL, Statement.RETURN_GENERATED_KEYS);
            resStmt.setInt(1, userPK);
            resStmt.setInt(2, showID);
            resStmt.setInt(3, numTickets);
            int resRows = resStmt.executeUpdate();
            if (resRows <= 0) {
                resStmt.close();
                conn.rollback();
                return "Error: Reservation failed.";
            }
            ResultSet resKeys = resStmt.getGeneratedKeys();
            int reservationID = 0;
            if (resKeys.next()) {
                reservationID = resKeys.getInt(1);
            }
            resKeys.close();
            resStmt.close();

            // Validate credit card number (must be exactly 5 digits).
            if (cardNumber == null || !cardNumber.matches("\\d{5}")) {
                conn.rollback();
                return "Error: Invalid credit card number. Must be exactly 5 digits.";
            }

            // Retrieve ticket price from the shows table.
            String priceSQL = "SELECT price FROM shows WHERE showPK = ?";
            PreparedStatement priceStmt = conn.prepareStatement(priceSQL);
            priceStmt.setInt(1, showID);
            ResultSet priceRS = priceStmt.executeQuery();
            double ticketPrice = 0.0;
            if (priceRS.next()){
                ticketPrice = priceRS.getDouble("price");
            }
            priceRS.close();
            priceStmt.close();
            double totalAmount = ticketPrice * numTickets;

            // Insert payment record.
            String insertPaySQL = "INSERT INTO payment (reservationpk, amount, creditcardnumber) VALUES (?, ?, ?)";
            PreparedStatement payStmt = conn.prepareStatement(insertPaySQL);
            payStmt.setInt(1, reservationID);
            payStmt.setDouble(2, totalAmount);
            payStmt.setString(3, cardNumber);
            int payRows = payStmt.executeUpdate();
            payStmt.close();
            if (payRows <= 0) {
                conn.rollback();
                return "Error: Payment processing failed.";
            }

            // If everything is successful, commit the transaction.
            conn.commit();
            return "Booking and payment processed successfully! Reservation ID: " + reservationID;
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            return "Error processing booking: " + ex.getMessage();
        }
        finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    /**
     * Handles user sign-up and account creation.
     * - Checks if the username already exists to prevent duplicates.
     * - Inserts the new user into the database with predefined role "customer".
     * - Returns a success message if the registration succeeds, otherwise an error.
     *
     * @param signUpReq The SignUpRequest containing user details such as username, password, name, email, and address.
     * @return A message indicating success or failure of the registration process.
     */
    private String handleSignUpUser(SignUpRequest signUpReq) {
        try {
            // Check for duplicate username.
            String checkSQL = "SELECT COUNT(*) FROM registereduser WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, signUpReq.getUsername());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                rs.close();
                checkStmt.close();
                return "Error: Username already exists.";
            }
            rs.close();
            checkStmt.close();

            // Insert the new user. Note: we assume that role is set to "customer".
            String insertSQL = "INSERT INTO registereduser (username, password, firstName, lastName, email, address, phone, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            insertStmt.setString(1, signUpReq.getUsername());
            insertStmt.setString(2, signUpReq.getPassword());
            insertStmt.setString(3, signUpReq.getFirstName());
            insertStmt.setString(4, signUpReq.getLastName());
            insertStmt.setString(5, signUpReq.getEmail());
            insertStmt.setString(6, signUpReq.getAddress());
            insertStmt.setString(7, signUpReq.getPhone());
            insertStmt.setString(8, "customer");
            int affectedRows = insertStmt.executeUpdate();
            insertStmt.close();

            return (affectedRows > 0) ? "Sign-up successful!" : "Error: Sign-up failed.";

        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error signing up: " + ex.getMessage();
        }
    }


    /**
     * Retrieves all reservations made by a specific customer.
     * - Joins reservation, show, movie, and user tables to gather relevant details.
     * - Formats reservation details to include the movie title, date, time, room, and price.
     * - Returns a list of ReservationItem objects representing the user's bookings.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @param username The username of the customer whose reservations are being retrieved.
     * @return A list of ReservationItem objects containing details of the customer's reservations.
     */
    private List<ReservationItem> handleGetCustomerReservations(String username) {
        List<ReservationItem> reservations = new ArrayList<>();
        String query = "SELECT r.reservationpk, " +
                "CONCAT(m.title, ' on ', s.showdate, ' at ', SUBSTRING(s.showtime,1,5), ' in Room ', s.roomnumber, ' (Price: $', s.price, ')') AS showDetails, " +
                "r.numberoftickets, r.reservationtime " +
                "FROM reservation r " +
                "JOIN shows s ON r.showpk = s.showpk " +
                "JOIN movie m ON s.moviepk = m.moviepk " +
                "JOIN registereduser ru ON r.userpk = ru.userpk " +
                "WHERE ru.username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int reservationID = rs.getInt("reservationpk");
                    String showDetails = rs.getString("showDetails");
                    int numberOfTickets = rs.getInt("numberoftickets");
                    Timestamp reservationTime = rs.getTimestamp("reservationtime");
                    // Create a ReservationItem object. Adjust the constructor as needed.
                    reservations.add(new ReservationItem(reservationID, showDetails, numberOfTickets, reservationTime.toString()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reservations;
    }


    /**
     * Handles the cancellation of a reservation.
     * - Validates that the reservation exists and retrieves associated show details.
     * - Ensures cancellation occurs at least one hour before the show time.
     * - Updates seat availability, removes payment records, and deletes the reservation.
     * - Uses transactions to maintain data integrity and rollbacks on failure.
     *
     * @param reservationID The unique identifier of the reservation to be canceled.
     * @return A message indicating success or failure of the cancellation process.
     */
    private String handleCancelReservation(int reservationID) {
        try {
            // Begin a transaction.
            conn.setAutoCommit(false);

            // Retrieve the reservation details.
            String getResSQL = "SELECT numberoftickets, showpk FROM reservation WHERE reservationpk = ?";
            int numTickets = 0;
            int showPK = 0;
            try (PreparedStatement resStmt = conn.prepareStatement(getResSQL)) {
                resStmt.setInt(1, reservationID);
                try (ResultSet rsRes = resStmt.executeQuery()) {
                    if (rsRes.next()) {
                        numTickets = rsRes.getInt("numberoftickets");
                        showPK = rsRes.getInt("showpk");
                    } else {
                        conn.rollback();
                        return "Error: Reservation not found.";
                    }
                }
            }

            // Retrieve the show's date, time, and available seats.
            String getShowSQL = "SELECT showdate, showtime, availableseats FROM shows WHERE showPK = ?";
            LocalDate showDate = null;
            LocalTime showTime = null;
            try (PreparedStatement showStmt = conn.prepareStatement(getShowSQL)) {
                showStmt.setInt(1, showPK);
                try (ResultSet rsShow = showStmt.executeQuery()) {
                    if (rsShow.next()) {
                        showDate = rsShow.getDate("showdate").toLocalDate();
                        showTime = rsShow.getTime("showtime").toLocalTime();
                    } else {
                        conn.rollback();
                        return "Error: Show not found for this reservation.";
                    }
                }
            }

            // Check cancellation cutoff.
            LocalDateTime showStart = LocalDateTime.of(showDate, showTime);
            LocalDateTime cancellationDeadline = showStart.minusHours(1);
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(showStart)) {
                // The show has already started (or passed).
                conn.rollback();
                return "Error: Cannot cancel reservations for past shows.";
            } else if (!now.isBefore(cancellationDeadline)) {
                // We are within 1 hour of the show time.
                conn.rollback();
                return "Error: Cancellation is only allowed at least 1 hour before the show time.";
            }

            // Update the available seats: add back the canceled tickets.
            String updateSeatsSQL = "UPDATE shows SET availableseats = availableseats + ? WHERE showPK = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSeatsSQL)) {
                updateStmt.setInt(1, numTickets);
                updateStmt.setInt(2, showPK);
                int updated = updateStmt.executeUpdate();
                if (updated <= 0) {
                    conn.rollback();
                    return "Error: Could not update available seats.";
                }
            }

            // Delete any associated payment record.
            String deletePaymentSQL = "DELETE FROM payment WHERE reservationpk = ?";
            try (PreparedStatement payStmt = conn.prepareStatement(deletePaymentSQL)) {
                payStmt.setInt(1, reservationID);
                payStmt.executeUpdate();
            }

            // Delete the reservation record.
            String deleteReservationSQL = "DELETE FROM reservation WHERE reservationpk = ?";
            int affectedRows = 0;
            try (PreparedStatement cancelStmt = conn.prepareStatement(deleteReservationSQL)) {
                cancelStmt.setInt(1, reservationID);
                affectedRows = cancelStmt.executeUpdate();
            }

            if (affectedRows > 0) {
                conn.commit();
                return "Reservation cancelled successfully.";
            } else {
                conn.rollback();
                return "Error: Reservation could not be cancelled.";
            }
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            ex.printStackTrace();
            return "Error cancelling reservation: " + ex.getMessage();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Retrieves user profile information from the database.
     * - Searches for a registered user by username.
     * - If found, extracts and returns details such as email, name, address, and phone number.
     * - Logs the result and returns a UserProfile object if successful, otherwise returns null.
     * - Handles SQL exceptions to ensure robustness.
     *
     * @param username The username of the user whose profile is being retrieved.
     * @return A UserProfile object containing user details, or null if the user is not found.
     */
    private UserProfile handleGetUserInfo(String username) {
        try {
            String query = "SELECT username, email, firstname, lastname, address, phone " +
                    "FROM registereduser WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                System.out.println("User found: " + username + ", " + email);
                return new UserProfile(username, email, firstName, lastName, address, phone);
            } else {
                System.out.println("No user found with username: " + username);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Handles client requests in a separate thread.
     * - Manages object streams for communication with the client.
     * - Processes different request types such as signing up, logging in, booking tickets, and retrieving data.
     * - Sends appropriate responses back to the client based on request handling.
     * - Ensures proper cleanup of resources when the client disconnects.
     */
    private class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            ObjectOutputStream outputToClient = null;
            ObjectInputStream inputFromClient = null;
            try {
                // Set up object streams.
                outputToClient = new ObjectOutputStream(socket.getOutputStream());
                outputToClient.flush();
                inputFromClient = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    // Read the request type first.
                    String requestType = (String) inputFromClient.readObject();
                    String response = "";

                    // Dispatch based on the request type.
                    switch (requestType) {
                    case "SIGNUP_USER":
                        SignUpRequest signUpReq = (SignUpRequest) inputFromClient.readObject();
                        String signupResponse = handleSignUpUser(signUpReq);
                        outputToClient.writeObject(signupResponse);
                        outputToClient.flush();
                        break;
                    case "LOGIN_USER":
                        LoginRequest loginRequest = (LoginRequest) inputFromClient.readObject();
                        response = handleLoginUser(loginRequest);
                        break;
                    case "ADD_MOVIE":
                        Movie movie = (Movie) inputFromClient.readObject();
                        response = handleAddMovie(movie);
                        break;
                    case "ADD_SHOW":
                        Show show = (Show) inputFromClient.readObject();
                        response = handleAddShow(show);
                        break;
                    case "GET_MOVIES":
                        List<MovieItem> movies = handleGetMovies();
                        outputToClient.writeObject(movies);
                        outputToClient.flush();
                        break;
                    case "GET_SHOWS":
                        List<ShowItem> shows = handleGetShows();
                        outputToClient.writeObject(shows);
                        outputToClient.flush();
                        break;
                    case "DELETE_MOVIE":
                        int movieID = (Integer) inputFromClient.readObject();
                        response = handleDeleteMovie(movieID);
                        outputToClient.writeObject(response);
                        outputToClient.flush();
                        break;
                    case "DELETE_SHOW":
                        int showID = (Integer) inputFromClient.readObject();
                        response = handleDeleteShow(showID);
                        outputToClient.writeObject(response);
                        outputToClient.flush();
                        break;
                    case "GET_AVAILABLE_SHOWS":
                        List<ShowItem> showsAvail = handleGetAvailableShows();
                        outputToClient.writeObject(showsAvail);
                        outputToClient.flush();
                        break;
                    case "BOOK_TICKET":
                        BookingRequest bookingReq = (BookingRequest) inputFromClient.readObject();
                        String bookingResponse = handleBookTicket(bookingReq);
                        outputToClient.writeObject(bookingResponse);
                        outputToClient.flush();
                        break;
                    case "GET_CUSTOMER_RESERVATIONS":
                        String username = (String) inputFromClient.readObject();
                        List<ReservationItem> reservations = handleGetCustomerReservations(username);
                        outputToClient.writeObject(reservations);
                        outputToClient.flush();
                        break;
                    case "CANCEL_RESERVATION":
                        Integer reservationID = (Integer) inputFromClient.readObject();
                        String cancelResponse = handleCancelReservation(reservationID);
                        outputToClient.writeObject(cancelResponse);
                        outputToClient.flush();
                        break;
                    case "GET_USER_INFO":
                        String userid = (String) inputFromClient.readObject();
                        UserProfile profile = handleGetUserInfo(userid);
                        outputToClient.writeObject(profile);
                        outputToClient.flush();
                    default:
                        response = "Invalid request type.";
                    }
                    // Send the response back to the client.
                    outputToClient.writeObject(response);
                    outputToClient.flush();
                }
            } catch (Exception e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    if (inputFromClient != null) {
                        inputFromClient.close();
                    }
                    if (outputToClient != null) {
                        outputToClient.close();
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * Main entry point for the Ticket Reservation Server.
     * - Initializes the server on the specified port.
     * - Starts listening for client connections.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        TicketReservationServer server = new TicketReservationServer(SERVER_PORT);
        server.start();
    }
}