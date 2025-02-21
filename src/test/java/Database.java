import de.MCmoderSD.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database extends Driver {

    public Database(DatabaseType databaseType, String host, int port, String database, String username, String password) {
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
        Database database = new Database(
                DatabaseType.MYSQL,     // Database Type
                "localhost",            // Host
                3306,                   // Port
                "database",             // Database
                "username",             // Username
                "password"              // Password
        );

        // Test Database
        System.out.println("Connected: " + database.isConnected());
        System.out.println("Row Count: " + database.getRowCount());
    }
}