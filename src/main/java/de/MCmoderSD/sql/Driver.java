package de.MCmoderSD.sql;

import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract class representing a universal database driver.
 * Supports multiple databases via the {@link DatabaseType} enum.
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
                databaseType,                                                       // Database Type
                config.has("host") ? config.get("host").asString() : null,          // Host
                config.has("port") ? config.get("port").asInt() : null,             // Port
                config.get("database").asString(),                                  // Database
                config.has("username") ? config.get("username").asString() : null,  // Username
                config.has("password") ? config.get("password").asString() : null   // Password
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
    public Driver(DatabaseType databaseType, @Nullable String host, @Nullable Integer port, String database, @Nullable String username, @Nullable String password) {

        // Set attributes
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        // Validate attributes and establish connection
        checkAttributes(databaseType, host, port, database, username, password);
        url = databaseType.getUrl(host, port, database);
        connect();
    }

    /**
     * Validates the connection attributes.
     *
     * @param databaseType The type of database.
     * @param host The database host.
     * @param port The database port.
     * @param database The database name.
     * @param username The database username.
     * @param password The database password.
     */
    private static void checkAttributes(DatabaseType databaseType, @Nullable String host, @Nullable Integer port, String database, @Nullable String username, @Nullable String password) {
        if (database == null || database.isBlank()) throw new IllegalArgumentException("Invalid database");
        if (databaseType == DatabaseType.SQLITE) return; // SQLite does not require host and port
        if (host == null || host.isBlank()) throw new IllegalArgumentException("Invalid host");
        if (port == null || port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port");
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
     * @return {@code true} if the connection was established successfully, otherwise {@code false}.
     */
    public boolean connect() {
        try {
            if (isConnected()) return true;
            databaseType.registerDriver();
            connection = DriverManager.getConnection(url, username, password);

            // Enable SQLite-specific features
            if (databaseType == DatabaseType.SQLITE && connection != null) {
                try (var stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }

            return connection != null && connection.isValid(0);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Establishes a connection to the database with additional parameters.
     *
     * @param pragma Additional parameters for the connection.
     * @return {@code true} if the connection was established successfully, otherwise {@code false}.
     */
    public boolean connect(String... pragma) {
        try {
            if (isConnected()) return true;
            databaseType.registerDriver();
            connection = DriverManager.getConnection(url, username, password);

            // Enable SQLite-specific features
            if (databaseType == DatabaseType.SQLITE && connection != null) try (var stmt = connection.createStatement()) {
                for (var p : pragma) stmt.execute(p);
            }

            return connection != null && connection.isValid(0);
        } catch (SQLException | ClassNotFoundException e) {
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
        MARIADB("jdbc:mariadb://%s:%d/%s", "org.mariadb.jdbc.Driver"),
        MYSQL("jdbc:mysql://%s:%d/%s", "com.mysql.cj.jdbc.Driver"),
        POSTGRESQL("jdbc:postgresql://%s:%d/%s", "org.postgresql.Driver"),
        SQLITE("jdbc:sqlite:%s", "org.sqlite.JDBC");

        // Attributes
        private final String urlPattern;
        private final String classPath;

        /**
         * Constructs a DatabaseType with a specific JDBC URL pattern and driver class.
         *
         * @param urlPattern The JDBC URL pattern.
         * @param classPath The fully qualified name of the database driver class.
         */
        DatabaseType(String urlPattern, String classPath) {

            // Set attributes
            this.urlPattern = urlPattern;
            this.classPath = classPath;

            // Register driver
            try {
                registerDriver();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Generates a JDBC URL for the database type.
         *
         * @param host The database host.
         * @param port The database port.
         * @param database The database name.
         * @return The formatted JDBC URL.
         */
        private String getUrl(@Nullable String host, @Nullable Integer port, String database) {
            if (this == SQLITE) return String.format(urlPattern, database);
            return String.format(urlPattern, host, port, database);
        }

        /**
         * Registers the JDBC driver class.
         *
         * @return The loaded driver class.
         * @throws ClassNotFoundException If the driver class cannot be found.
         */
        private Class<?> registerDriver() throws ClassNotFoundException {
            return Class.forName(classPath);
        }
    }
}