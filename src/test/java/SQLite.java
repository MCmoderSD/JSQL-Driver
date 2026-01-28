import de.MCmoderSD.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("ALL")
public class SQLite extends Driver {

    public SQLite(Builder builder) {
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
        Builder builder = Driver.Builder
                .withType(DatabaseType.SQLITE)  // Database Type
                .withDatabase("Database.db");   // Database File

        // Initialize Database Connection
        SQLite database = new SQLite(builder);
        database.connect();

        // Test Database
        IO.println("Connected: " + database.isConnected());
        IO.println("Row Count: " + database.getRowCount());
    }
}