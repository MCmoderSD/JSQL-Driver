import de.MCmoderSD.sql.Driver.Builder;
import de.MCmoderSD.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import static de.MCmoderSD.sql.Driver.DatabaseType.SQLITE;

void main() {

    // Build SQLite Configuration
    Builder builder = SQLite.builder()
            .withType(SQLITE)               // Database Type
            .withDatabase("Database.db");   // Database File

    // Initialize Database Connection
    SQLite database = new SQLite(builder);
    database.connect();

    // Test Database
    IO.println("Connected: " + database.isConnected());
    IO.println("Row Count: " + database.getRowCount());
}

// SQLite Driver Implementation
private static class SQLite extends Driver {

    // Constructor
    public SQLite(Builder builder) {
        super(builder); // Initialize Driver
    }

    // Get Row Count Method
    public Integer getRowCount() {

        // Initialize SQL Query
        String query = "SELECT COUNT(*) FROM `table`";

        // Initialize Statement
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            // Execute Query
            ResultSet resultSet = statement.executeQuery();

            // Return Result
            if (resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}