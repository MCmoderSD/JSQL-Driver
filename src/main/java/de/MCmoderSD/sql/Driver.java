package de.MCmoderSD.sql;

import com.fasterxml.jackson.databind.JsonNode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract class representing a universal database driver.
 * Supports multiple databases via the DatabaseType enum.
 */
@SuppressWarnings("ALL")
public abstract class Driver {

    // Constants
    protected final String url;
    protected final String host;
    protected final Integer port;
    protected final String database;
    protected final String username;
    protected final String password;
    protected final DatabaseType databaseType;

    // Attributes
    protected Connection connection;

    /**
     * Constructs a Driver instance using a configuration JSON node.
     *
     * @param databaseType The type of database.
     * @param config The JSON node containing connection details.
     */
    public Driver(DatabaseType databaseType, JsonNode config) {
        this(
                databaseType,                       // Database Type
                config.get("host").asText(),        // Host
                config.get("port").asInt(),         // Port
                config.get("database").asText(),    // Database
                config.get("username").asText(),    // Username
                config.get("password").asText()     // Password
        );
    }

    /**
     * Constructs a Driver instance using explicit connection details.
     *
     * @param databaseType The type of database.
     * @param host The database host.
     * @param port The database port.
     * @param database The database name.
     * @param username The database username.
     * @param password The database password.
     */
    public Driver(DatabaseType databaseType, String host, int port, String database, String username, String password) {

        // Set attributes
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        // Validate attributes and establish connection
        checkAttributes(host, port, database, username, password);
        url = databaseType.getUrl(host, port, database);
        connect();
    }

    /**
     * Validates the provided connection attributes.
     *
     * @param host The database host.
     * @param port The database port.
     * @param database The database name.
     * @param username The database username.
     * @param password The database password.
     * @throws IllegalArgumentException If any parameter is invalid.
     */
    private static void checkAttributes(String host, Integer port, String database, String username, String password) {
        if (host == null || host.isBlank()) throw new IllegalArgumentException("Invalid host");
        if (port == null || port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port");
        if (database == null || database.isBlank()) throw new IllegalArgumentException("Invalid database");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Invalid username");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Invalid password");
    }

    /**
     * Disconnects from the database.
     *
     * @return {@code true} if disconnected successfully, otherwise {@code false}.
     */
    public boolean disconnect() {
        try {
            if (!isConnected()) return true;
            connection.close();
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return {@code true} if connected successfully, otherwise {@code false}.
     */
    public boolean connect() {
        try {
            if (isConnected()) return true;
            connection = DriverManager.getConnection(url, username, password);
            return connection != null && connection.isValid(0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the database connection is active.
     *
     * @return {@code true} if connected, otherwise {@code false}.
     */
    public boolean isConnected() {
        try {
            return connection != null && connection.isValid(0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the current database connection.
     *
     * @return The active {@link Connection} instance.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Enumeration for different database types.
     */
    public enum DatabaseType {

        // Constants
        MARIADB("jdbc:mariadb://%s:%d/%s"),
        MYSQL("jdbc:mysql://%s:%d/%s"),
        POSTGRESQL("jdbc:postgresql://%s:%d/%s");

        private final String urlPattern;

        /**
         * Constructs a DatabaseType with a specific URL pattern.
         *
         * @param urlPattern The JDBC URL pattern.
         */
        DatabaseType(String urlPattern) {
            this.urlPattern = urlPattern;
        }

        /**
         * Generates a JDBC URL for the database type.
         *
         * @param host The database host.
         * @param port The database port.
         * @param database The database name.
         * @return The formatted JDBC URL.
         */
        public String getUrl(String host, int port, String database) {
            return String.format(this.urlPattern, host, port, database);
        }
    }
}