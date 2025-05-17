//*********************************************************************
//*                              CSU
//* CSU SYSENG                  SP2025                  ABODA
//*
//* Final Project: Movie Ticket Reservation System
//*
//* Date Created: May 05, 2025
//*
//* Saved in: AdminClient.java --> ABODAFP.zip
//*
//*********************************************************************
package mtrs; // mtrs: movie ticket reservation system

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class AdminClient extends JFrame {

    // Host and port for the server connection.
    private String hostname;
    private int port;

    // GUI components for the admin login screen.
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;
    private JButton adminLoginButton;

    // Container panels
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // A status label for providing feedback.
    private JLabel statusLabel;

    // Other useful fields
    private JComboBox<MovieItem> movieComboBox;
    private JComboBox<MovieItem> deleteMovieComboBox;
    private JComboBox<ShowItem> deleteShowComboBox;

    // Constants
    private static final int MAX_ROOM_NUMBER        = 10;
    private static final int MAX_SEATS_PER_ROOM     = 40;
    private static final int MIN_MOVIE_TITLE_LENGTH = 2;
    private static final int MIN_MOVIE_INFO_LENGTH  = 10;


    /**
     * Initializes the AdminClient application.
     * - Sets up the main window with appropriate size, title, and close behavior.
     * - Configures a card layout to switch between login and dashboard panels.
     * - Builds and adds login and dashboard panels for administrator functionality.
     * - Includes a status label to display system messages.
     * - Makes the application visible after setup.
     *
     * @param hostname The server hostname for communication.
     * @param port The server port number for connection.
     */
    public AdminClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        setTitle("Movie Ticket Reservation System - Administrator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize the status label.
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Set up card layout for switching between login and dashboard.
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Build the login panel and dashboard panel.
        JPanel loginPanel = buildLoginPanel();
        mainPanel.add(loginPanel, "login");
        JPanel dashboardPanel = buildAdminDashboardPanel();
        mainPanel.add(dashboardPanel, "dashboard");

        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);
    }


    /**
     * Builds the admin login panel for authentication.
     * - Uses GridBagLayout to structure input fields for username and password.
     * - Provides a login button for submitting credentials.
     * - Validates user input and sends a login request to the server.
     * - Displays feedback messages based on login success or failure.
     * - If successful, switches to the dashboard panel.
     *
     * @return A JPanel containing admin login fields and functionality.
     */
    private JPanel buildLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Admin username label and field.
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Admin Username:"), gbc);
        adminUsernameField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(adminUsernameField, gbc);

        // Admin password label and field.
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        adminPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(adminPasswordField, gbc);

        // Admin login button.
        adminLoginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(adminLoginButton, gbc);

        // Action listener for admin login button.
        adminLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = adminUsernameField.getText().trim();
                String password = new String(adminPasswordField.getPassword()).trim();

                // Gracefully validate inputs.
                if (username.isEmpty() || password.isEmpty()) {
                    updateStatus("Username or Password cannot be empty.");
                    return;
                }

                // Create a LoginRequest with role "admin".
                LoginRequest loginReq = new LoginRequest(username, password, "admin");
                String response = sendMessageToServer("LOGIN_USER", loginReq);
                updateStatus(response);

                // If login is successful, switch to the dashboard panel.
                if (response != null && response.toLowerCase().contains("successful")) {
                    cardLayout.show(mainPanel, "dashboard");
                }
            }
        });
        return loginPanel;
    }


    /**
     * Builds the admin dashboard panel with a tabbed interface.
     * - Organizes multiple admin functionalities into separate tabs.
     * - Includes options for listing, adding, and deleting movies and shows.
     * - Uses BorderLayout to structure the panel for easy navigation.
     *
     * @return A JPanel containing the admin dashboard tabs.
     */
    private JPanel buildAdminDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab for listing Movies
        JPanel listMoviePanel = buildListMoviesPanel();
        tabbedPane.addTab("List Movies", listMoviePanel);

        // Tab for listing Shows
        JPanel listShowsPanel = buildListShowsPanel();
        tabbedPane.addTab("List Shows", listShowsPanel);

        // Tab for adding movies.
        JPanel addMoviePanel = buildAddMoviePanel();
        tabbedPane.addTab("Add Movie", addMoviePanel);

        // Tab for adding shows.
        JPanel addShowPanel = buildAddShowPanel();
        tabbedPane.addTab("Add Show", addShowPanel);

        // Tab for deleting movies.
        JPanel deleteMoviePanel = buildDeleteMoviePanel();
        tabbedPane.addTab("Delete Movie", deleteMoviePanel);

        // Tab for deleting shows.
        JPanel deleteShowPanel = buildDeleteShowPanel();
        tabbedPane.addTab("Delete Show", deleteShowPanel);

        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
        return dashboardPanel;
    }


    /**
     * Builds the Add Movie panel for administrators.
     * - Provides input fields for entering the movie title, rating, and additional info.
     * - Includes a button to submit and send the movie data to the server.
     * - Validates input to ensure the title is not empty.
     * - Updates movie-related dropdowns upon successful addition.
     *
     * @return A JPanel containing movie input fields and an add button.
     */
    private JPanel buildAddMoviePanel() {
        JPanel addMoviePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Movie title.
        gbc.gridx = 0;
        gbc.gridy = 0;
        addMoviePanel.add(new JLabel("Title:"), gbc);
        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        addMoviePanel.add(titleField, gbc);

        // Movie rating.
        gbc.gridx = 0;
        gbc.gridy = 1;
        addMoviePanel.add(new JLabel("Rating (1-5):"), gbc);
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        gbc.gridx = 1;
        addMoviePanel.add(ratingSpinner, gbc);

        // Movie info.
        gbc.gridx = 0;
        gbc.gridy = 2;
        addMoviePanel.add(new JLabel("Info:"), gbc);
        JTextArea infoArea = new JTextArea(5, 20);
        JScrollPane infoScroll = new JScrollPane(infoArea);
        gbc.gridx = 1;
        addMoviePanel.add(infoScroll, gbc);

        // Add Movie button.
        JButton addMovieButton = new JButton("Add Movie");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addMoviePanel.add(addMovieButton, gbc);

        addMovieButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText().trim();
                String info = infoArea.getText().trim();

                // Validate movie title: Must not be empty and have at least 2 characters.
                if (title.isEmpty()) {
                    updateStatus("Movie title cannot be empty.");
                    return;
                }
                if(title.length() < MIN_MOVIE_TITLE_LENGTH) {
                    updateStatus("Movie title must be at least 2 characters long.");
                    return;
                }

                // Rating is constrained via spinner (1-5).
                int rating = (Integer) ratingSpinner.getValue();

                // Validate movie info: Must not be empty and should provide sufficient detail.
                if (info.isEmpty()) {
                    updateStatus("Movie info cannot be empty.");
                    return;
                }
                if(info.length() < MIN_MOVIE_INFO_LENGTH) {
                    updateStatus("Movie info should be at least 10 characters long.");
                    return;
                }

                // Proceed with adding the movie if all validations pass.
                Movie movie = new Movie(0, title, rating, info);
                String response = sendMessageToServer("ADD_MOVIE", movie);
                updateStatus(response);
                if (response != null && response.toLowerCase().contains("success")) {
                    updateMoviesComboBox();
                    updateDeleteMoviesComboBox();
                }
            }
        });
        return addMoviePanel;
    }


    /**
     * Builds the movie listing panel for administrators.
     * - Uses a table to display movie details such as ID, title, rating, and description.
     * - Provides a refresh button to dynamically update the movie list.
     * - Fetches movie data from the server and populates the table upon refresh.
     * - Automatically refreshes the list when the panel is loaded.
     *
     * @return A JPanel containing a movie table and refresh functionality.
     */
    private JPanel buildListMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model and JTable for listing movies.
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable moviesTable = new JTable(tableModel);
        tableModel.addColumn("ID");
        tableModel.addColumn("Movie");
        tableModel.addColumn("Rating");
        tableModel.addColumn("Description");

        // Panel for the refresh button.
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Movies");
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(moviesTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action to refresh the movies table.
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                List<MovieItem> movies = fetchMoviesFromServer();
                if (movies != null) {
                    for (MovieItem movie : movies) {
                        int id = movie.getMovieID();
                        String title = movie.getMovieName();
                        double rating = movie.getRating();
                        String description = movie.getDescription();

                        tableModel.addRow(new Object[]{ id, title, rating, description });
                    }
                }
            }
        });

        // Trigger a refresh on panel load.
        refreshButton.doClick();

        return panel;
    }


    /**
     * Builds the show listing panel for administrators.
     * - Uses a table to display show details including ID, movie, date & time, room, seats, and price.
     * - Provides a refresh button to dynamically update the show list.
     * - Fetches show data from the server and populates the table upon refresh.
     * - Automatically refreshes the list when the panel is loaded.
     *
     * @return A JPanel containing a show table and refresh functionality.
     */
    private JPanel buildListShowsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table model configured with the desired columns.
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable showsTable = new JTable(tableModel);
        tableModel.addColumn("ID");
        tableModel.addColumn("Movie");
        tableModel.addColumn("Date & Time");
        tableModel.addColumn("Room");
        tableModel.addColumn("Seats");
        tableModel.addColumn("Price");

        // Panel for the refresh button.
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Shows");
        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(showsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action to refresh the shows list.
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                List<ShowItem> shows = fetchShowsFromServer();
                if (shows != null) {
                    for (ShowItem show : shows) {
                        int id = show.getShowID();
                        String movie = show.getMovieTitle();
                        String dateTime = show.getShowDate().toString() + " " +
                                show.getShowTime().toString().substring(0, 5);
                        int room = show.getRoomNumber();
                        int seats = show.getAvailableSeats();
                        double price = show.getPrice();

                        tableModel.addRow(new Object[] { id, movie, dateTime, room, seats, price });
                    }
                }
            }
        });

        // Trigger a refresh on panel load.
        refreshButton.doClick();

        return panel;
    }


    /**
     * Builds the Add Show panel for administrators.
     * - Provides input fields for selecting a movie, date, time, room number, and price.
     * - Validates input to ensure proper formatting.
     * - Sends the show details to the server for creation.
     * - Updates relevant dropdowns upon successful addition.
     *
     * @return A JPanel containing show input fields and an add button.
     */
    private JPanel buildAddShowPanel() {
        JPanel addShowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label and combo box for selecting a movie.
        gbc.gridx = 0;
        gbc.gridy = 0;
        addShowPanel.add(new JLabel("Movie:"), gbc);

        // Populate the combo box with movies fetched from the server.
        movieComboBox = new JComboBox<>();
        updateMoviesComboBox();
        gbc.gridx = 1;
        addShowPanel.add(movieComboBox, gbc);

        // Show date field.
        gbc.gridx = 0;
        gbc.gridy = 1;
        addShowPanel.add(new JLabel("Show Date (YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField(10);
        gbc.gridx = 1;
        addShowPanel.add(dateField, gbc);

        // Show time field (HH:MM input).
        gbc.gridx = 0;
        gbc.gridy = 2;
        addShowPanel.add(new JLabel("Show Time (HH:MM):"), gbc);
        JTextField timeField = new JTextField(10);
        gbc.gridx = 1;
        addShowPanel.add(timeField, gbc);

        // Room number.
        gbc.gridx = 0;
        gbc.gridy = 3;
        addShowPanel.add(new JLabel("Room Number:"), gbc);
        JTextField roomField = new JTextField(5);
        gbc.gridx = 1;
        addShowPanel.add(roomField, gbc);

        // Price.
        gbc.gridx = 0;
        gbc.gridy = 4;
        addShowPanel.add(new JLabel("Price:"), gbc);
        JTextField priceField = new JTextField(10);
        gbc.gridx = 1;
        addShowPanel.add(priceField, gbc);

        // Add Show button.
        JButton addShowButton = new JButton("Add Show");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        addShowPanel.add(addShowButton, gbc);

        addShowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate movie selection.
                    MovieItem selectedMovie = (MovieItem) movieComboBox.getSelectedItem();
                    if (selectedMovie == null) {
                        updateStatus("Please select a movie.");
                        return;
                    }
                    int moviePk = selectedMovie.getMovieID();

                    // Validate show date.
                    String date = dateField.getText().trim();
                    if (date.isEmpty()) {
                        updateStatus("Show date is required.");
                        return;
                    }
                    if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        updateStatus("Show date must be in the format YYYY-MM-DD.");
                        return;
                    }

                    // Validate show time.
                    String time = timeField.getText().trim();
                    if (time.isEmpty()) {
                        updateStatus("Show time is required.");
                        return;
                    }
                    // Allow either HH:MM or HH:MM:SS format.
                    if (!time.matches("^\\d{2}:\\d{2}(:\\d{2})?$")) {
                        updateStatus("Show time must be in the format HH:MM or HH:MM:SS.");
                        return;
                    }
                    // Append seconds if missing.
                    if (!time.matches(".*:\\d{2}$")) {
                        time = time + ":00";
                    }

                    // Combine the date and time inputs into a LocalDateTime.
                    LocalDate localDate = LocalDate.parse(date);
                    LocalTime localTime = LocalTime.parse(time);
                    LocalDateTime showDateTime = LocalDateTime.of(localDate, localTime);
                    // Validate that the show is not in the past.
                    if (showDateTime.isBefore(LocalDateTime.now())) {
                        updateStatus("Cannot add a show scheduled in the past.");
                        return;
                    }

                    // Validate room number.
                    String roomText = roomField.getText().trim();
                    if (roomText.isEmpty() || !roomText.matches("^\\d+$")) {
                        updateStatus("Room number must be a numeric value.");
                        return;
                    }
                    int room = Integer.parseInt(roomText);
                    if (room <= 0 || room > MAX_ROOM_NUMBER) {
                        updateStatus("Room number must be between 1 and " + MAX_ROOM_NUMBER + ".");
                        return;
                    }

                    // Validate price.
                    String priceText = priceField.getText().trim();
                    if (priceText.isEmpty() || !priceText.matches("^\\d+(\\.\\d{1,2})?$")) {
                        updateStatus("Price must be a positive number (up to 2 decimal places).");
                        return;
                    }
                    double price = Double.parseDouble(priceText);
                    if (price <= 0) {
                        updateStatus("Price must be greater than 0.");
                        return;
                    }

                    // Convert inputs into SQL Date and Time objects.
                    java.sql.Date showDateSQL = java.sql.Date.valueOf(localDate);
                    java.sql.Time showTimeSQL = java.sql.Time.valueOf(localTime);

                    // Create the Show object.
                    Show show = new Show(0, moviePk, showDateSQL, showTimeSQL, room, MAX_SEATS_PER_ROOM, price);
                    String response = sendMessageToServer("ADD_SHOW", show);
                    updateStatus(response);

                    // Refresh the delete show dropdown upon success.
                    if (response.toLowerCase().contains("success")) {
                        updateDeleteShowsComboBox();
                    }
                } catch (Exception ex) {
                    updateStatus("Input error: " + ex.getMessage());
                }
            }
        });
        return addShowPanel;
    }


    /**
     * Builds the movie deletion panel for administrators.
     * - Provides a dropdown to select a movie for deletion.
     * - Confirms deletion via a dialog before proceeding.
     * - Sends a request to the server to remove the selected movie.
     * - Updates related dropdowns after successful deletion.
     *
     * @return A JPanel containing movie deletion functionality.
     */
    private JPanel buildDeleteMoviePanel() {
        JPanel deleteMoviePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label and combo box for movie deletion.
        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteMoviePanel.add(new JLabel("Select Movie to Delete:"), gbc);

        deleteMovieComboBox = new JComboBox<>();
        updateDeleteMoviesComboBox();
        gbc.gridx = 1;
        deleteMoviePanel.add(deleteMovieComboBox, gbc);

        // Delete Movie button.
        JButton deleteMovieButton = new JButton("Delete Movie");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        deleteMoviePanel.add(deleteMovieButton, gbc);

        deleteMovieButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MovieItem selectedMovie = (MovieItem) deleteMovieComboBox.getSelectedItem();
                if (selectedMovie == null) {
                    updateStatus("No movie selected for deletion.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(AdminClient.this, 
                        "Are you sure you want to delete the movie: " + selectedMovie.getMovieName() + "?", 
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                // Send deletion request. Here we are just sending the movie id.
                String response = sendMessageToServer("DELETE_MOVIE", selectedMovie.getMovieID());
                updateStatus(response);
                updateMoviesComboBox();
                updateDeleteMoviesComboBox();
                updateDeleteShowsComboBox();
            }
        });
        return deleteMoviePanel;
    }


    /**
     * Builds the show deletion panel for administrators.
     * - Provides a dropdown to select a show for deletion.
     * - Confirms deletion via a dialog before proceeding.
     * - Sends a request to the server to remove the selected show.
     * - Updates relevant dropdowns after successful deletion.
     *
     * @return A JPanel containing show deletion functionality.
     */
    private JPanel buildDeleteShowPanel() {
        JPanel deleteShowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label and combo box for show deletion.
        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteShowPanel.add(new JLabel("Select Show to Delete:"), gbc);

        deleteShowComboBox = new JComboBox<>();
        updateDeleteShowsComboBox();
        gbc.gridx = 1;
        deleteShowPanel.add(deleteShowComboBox, gbc);

        // Delete Show button.
        JButton deleteShowButton = new JButton("Delete Show");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        deleteShowPanel.add(deleteShowButton, gbc);

        deleteShowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ShowItem selectedShow = (ShowItem) deleteShowComboBox.getSelectedItem();
                if (selectedShow == null) {
                    updateStatus("No show selected for deletion.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(AdminClient.this, 
                        "Are you sure you want to delete the show: " + selectedShow.toString() + "?", 
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                // Send deletion request using the show id.
                String response = sendMessageToServer("DELETE_SHOW", selectedShow.getShowID());
                updateStatus(response);
                updateDeleteShowsComboBox();
            }
        });
        return deleteShowPanel;
    }


    /**
     * Sends a request to the server and retrieves a response.
     * - Establishes a socket connection to the specified hostname and port.
     * - Sends a request type and payload to the server.
     * - Reads and returns the server's response.
     * - Handles connection errors and ensures proper cleanup of resources.
     *
     * @param requestType The command string (e.g., "LOGIN_USER", "ADD_MOVIE", "ADD_SHOW").
     * @param payload The object to be sent (e.g., LoginRequest, Movie, Show).
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
     * Fetches a list of movies from the server.
     * - Establishes a socket connection for communication.
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
     * - Sends a "PING" command to verify server connectivity before proceeding.
     * - Establishes a socket connection to request show data.
     * - Parses and returns a list of ShowItem objects.
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

        // Early exit
        String serverTest = sendMessageToServer("PING", null);
        if (serverTest.startsWith("Error")) {
            updateStatus("Server is unreachable. Please ensure it is online.");
            return shows;
        }

        // Connection is successful, move on
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
     * Updates the movie selection dropdown with available movies.
     * - Fetches the list of movies from the server.
     * - Clears existing items in the combo box.
     * - Populates the combo box with the newly retrieved movie items.
     * - Displays an error message if movie data retrieval fails.
     */
    private void updateMoviesComboBox() {
        List<MovieItem> movies = fetchMoviesFromServer();
        if (movies == null) {
            updateStatus("Failed to update movie list from server.");
            return;
        }
        // Clear existing items.
        movieComboBox.removeAllItems();
        for (MovieItem mi : movies) {
            movieComboBox.addItem(mi);
        }
    }


    /**
     * Updates the movie deletion dropdown with available movies.
     * - Fetches the list of movies from the server.
     * - Clears existing items in the combo box.
     * - Populates the combo box with the newly retrieved movie items.
     * - Displays an error message if movie data retrieval fails.
     */
    private void updateDeleteMoviesComboBox() {
        List<MovieItem> movies = fetchMoviesFromServer();
        if (movies == null) {
            updateStatus("Failed to update delete movie list from server.");
            return;
        }
        deleteMovieComboBox.removeAllItems();
        for (MovieItem mi : movies) {
            deleteMovieComboBox.addItem(mi);
        }
    }


    /**
     * Updates the show deletion dropdown with available shows.
     * - Fetches the list of shows from the server.
     * - Clears existing items in the combo box.
     * - Populates the combo box with the newly retrieved show items.
     * - Displays an error message if show data retrieval fails.
     */
    private void updateDeleteShowsComboBox() {
        List<ShowItem> shows = fetchShowsFromServer();
        if (shows == null) {
            updateStatus("Failed to update delete show list from server.");
            return;
        }
        deleteShowComboBox.removeAllItems();
        for (ShowItem si : shows) {
            deleteShowComboBox.addItem(si);
        }
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
     * Entry point for launching the AdminClient application.
     * - Defines the server hostname and port for communication.
     * - Uses SwingUtilities to ensure the UI is created on the Event Dispatch Thread.
     * - Instantiates and initializes the AdminClient.
     */
    public static void main(String[] args) {
        final String SQL_HOST = "127.0.0.1";
        final int SQL_PORT = 8000;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminClient(SQL_HOST, SQL_PORT);
            }
        });
    }
}