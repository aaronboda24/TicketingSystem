# TicketingSystem  

A Java-based movie ticket reservation system with GUI interfaces for both customer and administrator interactions. This was developed as part of a university course project, focusing on real-world application of software development principles, database management, and client-server architecture. It supports user authentication, movie/show scheduling, ticket booking and cancellation, and administrative management, while enforcing business rules for secure transactions.

## Features

- **Movie & Show Management:**  
  Browse available movies and view/manage scheduled showtimes.

- **Reservation System:**  
  Book and cancel tickets with real-time availability checks and enforced cancellation time-frames.

- **User Authentication:**  
  Secure login management and user profile handling.

- **Administrative Tools:**  
  Manage movies, shows, and customer reservations through an intuitive admin panel.

- **Payment Handling:**  
  Process ticket purchases with basic payment validation.

- **Business Rule Enforcement:**  
  In-built validations (e.g., cancellation cutoffs, reservation constraints) to ensure compliance with business logic.

## Entity Relationship Diagram

The system uses a MySQL database to manage data. The ERD below outlines the primary entities and their relationships:

<p align="center">
  <img src="https://github.com/user-attachments/assets/b106db87-1b4f-4cc8-8e6b-51cac0fdd176" alt="Entity Relationship Diagram" width="350">
</p>

## GUI Screenshots

Below are some screenshots from the application's GUI.

### Customer Client
<div align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/861249f1-a9df-44d7-bf0a-dd8bb7588988" alt="Login Screen" width="300"/><br>
      <strong>Login Screen</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/c9df2373-7400-4eb7-a1d2-cecaa73eff35" alt="Signup Screen" width="300"/><br>
      <strong>Signup Screen</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ca25b665-493e-472c-8eee-a0a7269bb172" alt="List Movies" width="300"/><br>
      <strong>List Movies</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/2553e291-dd95-4012-9120-1ad8e3c2f1ec" alt="List Shows" width="300"/><br>
      <strong>List Shows</strong>
    </td>
  </tr>
  <tr>
    <td colspan="2" align="center">
      <img src="https://github.com/user-attachments/assets/ad533136-bb4e-4f95-8940-23e063a030bb" alt="Show Bookings" width="300"/><br>
      <strong>Show Bookings</strong>
    </td>
  </tr>
</table>
</div>

### Administrator Client
<div align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/f3d46d54-a6a3-4514-83aa-1b65cedaeca7" alt="Add Movie" width="300"/><br>
      <strong>Add Movie</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/31cd0edd-0dcb-4fc4-a4c0-8c41d77fcc41" alt="Add Show" width="300"/><br>
      <strong>Add Show</strong>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/b87857c4-61a0-4a52-bbaf-29154c061955" alt="Delete Movie" width="300"/><br>
      <strong>Delete Movie</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ba17394f-1f6f-4e31-a1cc-efcea9c606b8" alt="Delete Show" width="300"/><br>
      <strong>Delete Show</strong>
    </td>
  </tr>
</table>
</div>

## Setup Instructions

### 1. Clone the Repository

Clone the project from GitHub:

```bash
git clone https://github.com/<your-username>/TicketingSystem.git
```

> Replace `<your-username>` with your actual GitHub username.

### 2. Download the MySQL JDBC Driver

This project requires the **MySQL Connector/J** for database connectivity.

- **You will need the driver:**  
  [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)

- **After downloading:**  
  Place the `.jar` file (e.g., `mysql-connector-java-8.0.xx.jar`) in a known directory.

### 3. Compile & Run the Project

Make sure you have **Java 11+** installed. When compiling and running, include the JDBC jar file in your classpath.

**Compilation:**

```bash
javac -d bin -cp path/to/mysql-connector-java-8.0.xx.jar src/*.java
```

**Running the Server:**

```bash
java -cp bin:path/to/mysql-connector-java-8.0.xx.jar TicketReservationServer
```

> **Note:** Replace `path/to/mysql-connector-java-8.0.xx.jar` with the actual path to your JDBC driver jar file.

## License  

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.  

### Disclaimer  

This project is provided as-is, without any warranty or guarantee of functionality, security, or suitability for any purpose. The author assumes no responsibility for any issues, damages, or losses resulting from its use. Users are encouraged to review the code and make necessary adjustments based on their individual requirements.
