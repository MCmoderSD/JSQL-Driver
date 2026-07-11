import de.MCmoderSD.sql.Driver;

import java.sql.SQLException;

import static de.MCmoderSD.sql.Driver.DatabaseType.MARIADB;
import static java.lang.IO.println;

void main() {

    // Build Database Configuration
    var builder = Database.builder()    // Create Builder
            .withType(MARIADB)          // Database Type
            .withHost("localhost")      // Host
            .withPort(3306)             // Port
            .withDatabase("database")   // Database
            .withUsername("username")   // Username
            .withPassword("password");  // Password

    // Initialize Database Connection
    var database = new Database(builder);
    database.setAutoReconnectSettings(5, 10000);    // Auto Reconnect Settings (5 Attempts, 10s Delay)
    database.setAutoReconnect(true);                // Enable Auto Reconnect
    database.connect();                             // Connect to Database

    // Test Database
    println("Connected: " + database.isConnected());
    println("Row Count: " + database.getRowCount());
}

// Database Driver Implementation
private static class Database extends Driver {

    // Constructor
    public Database(Builder builder) {
        super(builder); // Initialize Driver
    }

    // Get Row Count Method
    public Integer getRowCount() {

        // Initialize Statement
        try (var statement = connection.prepareStatement("SELECT COUNT(*) FROM `table`")) {

            // Execute Query
            var resultSet = statement.executeQuery();

            // Return Result
            if (resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}