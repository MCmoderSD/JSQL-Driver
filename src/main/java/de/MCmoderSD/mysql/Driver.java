package de.MCmoderSD.mysql;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract class representing a MySQL database driver.
 * Provides methods to connect to and disconnect from a MySQL database.
 */
@SuppressWarnings("ALL")
public abstract class Driver {

    // Attributes
    protected final String host;
    protected final Integer port;
    protected final String database;
    protected final String username;
    protected final String password;

    // Connection
    protected Connection connection;

    /**
     * Constructs a Driver instance using the provided JSON configuration.
     *
     * @param config A JsonNode containing the configuration parameters: host, port, database, username, and password.
     */
    public Driver(JsonNode config) {
        this(config.get("host").asText(), config.get("port").asInt(), config.get("database").asText(), config.get("username").asText(), config.get("password").asText());
    }

    /**
     * Constructs a Driver instance with the specified parameters.
     *
     * @param host     The hostname of the MySQL server.
     * @param port     The port number on which the MySQL server is running.
     * @param database The name of the database to connect to.
     * @param username The username to use for authentication.
     * @param password The password to use for authentication.
     */
    public Driver(String host, int port, String database, String username, String password) {
        // Set Attributes
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        // Check Attributes
        checkAttributes(host, port, database, username, password);

        // Connect
        connect();
    }

    /**
     * Validates the provided connection parameters.
     *
     * @param host     The hostname.
     * @param port     The port number.
     * @param database The database name.
     * @param username The username.
     * @param password The password.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    private static void checkAttributes(String host, Integer port, String database, String username, String password) {
        if (host == null || host.isEmpty() || host.isBlank()) throw new IllegalArgumentException("Invalid host");
        if (port == null || port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port");
        if (database == null || database.isEmpty() || database.isBlank()) throw new IllegalArgumentException("Invalid database");
        if (username == null || username.isEmpty() || username.isBlank()) throw new IllegalArgumentException("Invalid username");
        if (password == null || password.isEmpty() || password.isBlank()) throw new IllegalArgumentException("Invalid password");
    }

    /**
     * Disconnects from the MySQL database.
     *
     * @return true if the disconnection was successful; false otherwise.
     */
    public boolean disconnect() {
        try {
            if (!isConnected()) return true; // already disconnected
            connection.close(); // disconnect
            return connection.isClosed();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Connects to the MySQL database.
     *
     * @return true if the connection was successful; false otherwise.
     */
    public boolean connect() {
        try {
            if (isConnected()) return true; // already connected
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", host, port, database), username, password); // connect
            return connection.isValid(0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if there is an active connection to the MySQL database.
     *
     * @return true if connected; false otherwise.
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
     * Gets the host of the MySQL server.
     *
     * @return The host of the MySQL server.
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port number of the MySQL server.
     *
     * @return The port number of the MySQL server.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Gets the name of the database.
     *
     * @return The name of the database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the username used for authentication.
     *
     * @return The username used for authentication.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password used for authentication.
     *
     * @return The password used for authentication.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the active connection to the MySQL database.
     *
     * @return The active connection, or null if not connected.
     */
    public Connection getConnection() {
        return connection;
    }
}