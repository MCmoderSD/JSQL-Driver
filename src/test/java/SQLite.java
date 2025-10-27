import de.MCmoderSD.sql.Driver;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("ALL")
public class SQLite extends Driver {

    public SQLite(DatabaseType databaseType, @Nullable String host, @Nullable Integer port, String database, @Nullable String username, @Nullable String password) {
        super(databaseType, host, port, database, username, password);
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

    public static void main(String[] args) {

        // Initialize Database
        SQLite database = new SQLite(
                DatabaseType.SQLITE,    // Database Type
                null,                   // Host
                null,                   // Port
                ":memory:",             // Database
                null,                   // Username
                null                    // Password
        );

        // Test Database
        System.out.println("Connected: " + database.isConnected());
        System.out.println("Row Count: " + database.getRowCount());
    }
}