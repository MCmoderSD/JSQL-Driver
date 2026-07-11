import de.MCmoderSD.sql.Driver;

import java.sql.SQLException;

import static de.MCmoderSD.sql.Driver.DatabaseType.SQLITE;
import static java.lang.IO.println;

void main() {

    // Build SQLite Configuration
    var builder = SQLite.builder()
            .withType(SQLITE)               // Database Type
            .withDatabase("Database.db");   // Database File

    // Initialize Database Connection
    var database = new SQLite(builder);
    database.connect();

    // Test Database
    println("Connected: " + database.isConnected());
    println("Row Count: " + database.getRowCount());
}

// SQLite Driver Implementation
private static class SQLite extends Driver {

    // Constructor
    public SQLite(Builder builder) {
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