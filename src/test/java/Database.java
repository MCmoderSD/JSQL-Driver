import de.MCmoderSD.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("ALL")
public class Database extends Driver {

    public Database(Driver.Builder builder) {
        super(builder);
    }

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

    static void main() {

        // Build Driver
        Database.Builder builder = Database.Builder // Create Builder
                .withType(DatabaseType.MARIADB)     // Database Type
                .withHost("localhost")              // Host
                .withPort(3306)                     // Port
                .withDatabase("database")           // Database
                .withUsername("username")           // Username
                .withPassword("password");          // Password

        // Initialize Database Connection
        Database database = new Database(builder);
        database.setAutoReconnectSettings(5, 10000);    // Auto Reconnect Settings (5 Attempts, 10s Delay)
        database.setAutoReconnect(true);                // Enable Auto Reconnect
        database.connect();                             // Connect to Database

        // Test Database
        IO.println("Connected: " + database.isConnected());
        IO.println("Row Count: " + database.getRowCount());
    }
}