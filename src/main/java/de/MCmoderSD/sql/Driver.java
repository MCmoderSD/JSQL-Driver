package de.MCmoderSD.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static de.MCmoderSD.sql.Driver.DatabaseType.*;

@SuppressWarnings({"unused", "UnusedReturnValue", "BusyWait"})
public abstract class Driver {

    // Constants
    private final String url;
    private final String username;
    private final String password;
    private final DatabaseType databaseType;

    // Attributes
    protected Connection connection;

    // Variables
    private boolean autoReconnect;   // Default: false
    private int reconnectAttempts;   // Default: 0 (infinite)
    private long reconnectDelay;     // Default: 1000ms

    // Constructor
    protected Driver(Builder builder) {

        // Check Builder
        if (builder == null) throw new IllegalArgumentException("Builder cannot be null");

        // Set Attributes
        this.databaseType = builder.databaseType;
        this.username = builder.username;
        this.password = builder.password;

        // Build URL
        if (databaseType == SQLITE) this.url = databaseType.getUrl(builder.database);
        else this.url = databaseType.getUrl(builder.host, builder.port, builder.database);

        // Set Default
        autoReconnect = false;
        reconnectAttempts = 0;
        reconnectDelay = 1000;
    }

    // Auto Reconnect Method
    private void autoReconnect() {

        // Start Auto Reconnect Thread
        new Thread(() -> {
            var attempts = 0;
            while (autoReconnect && (reconnectAttempts == 0 || attempts < reconnectAttempts)) {

                // Try to reconnect if not connected
                if (!isConnected()) {
                    connect();
                    attempts++;
                } else attempts = 0;

                // Wait before next attempt
                try {
                    Thread.sleep(reconnectDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    // Connection Methods
    public boolean connect() {
        try {
            if (isConnected()) return true;
            databaseType.registerDriver();
            connection = DriverManager.getConnection(url, username, password);

            // Enable SQLite-specific features
            if (databaseType == SQLITE && connection != null) {
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

    // Setters
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        if (autoReconnect) autoReconnect();
    }

    public void setAutoReconnectSettings(int attempts, long delay) {
        this.reconnectAttempts = attempts;
        this.reconnectDelay = delay;
    }

    // Getters
    public boolean isConnected() {
        try {
            return connection != null && connection.isValid(0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    // Database Type Enum
    public enum DatabaseType {

        // Constants
        MARIADB("jdbc:mariadb://%s:%d/%s", "org.mariadb.jdbc.Driver"),
        MYSQL("jdbc:mysql://%s:%d/%s", "com.mysql.cj.jdbc.Driver"),
        POSTGRESQL("jdbc:postgresql://%s:%d/%s", "org.postgresql.Driver"),
        SQLITE("jdbc:sqlite:%s", "org.sqlite.JDBC");

        // Attributes
        private final String urlPattern;
        private final String classPath;

        // Constructor
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

        // Driver Registration Method
        private Class<?> registerDriver() throws ClassNotFoundException {
            return Class.forName(classPath);
        }

        // URL Builder Methods
        private String getUrl(String host, Integer port, String database) {
            return String.format(urlPattern, host, port, database);
        }

        private String getUrl(String database) {
            return String.format(urlPattern, database);
        }
    }

    // Static Builder Method
    public static Builder builder() {
        return new Builder();
    }

    // Builder Class
    public static class Builder {

        // Attributes
        private DatabaseType databaseType;
        private String host;
        private Integer port;
        private String database;
        private String username;
        private String password;

        // Constructor
        private Builder() {
            databaseType = null;
            host = null;
            port = null;
            database = null;
            username = null;
            password = null;
        }

        // Builder Methods
        public Builder withType(DatabaseType databaseType) {
            if (databaseType == null) throw new IllegalArgumentException("Database type cannot be null");
            if (databaseType == SQLITE && (host != null || port != null || username != null || password != null)) throw new IllegalArgumentException("Host, Port, Username and Password are not required for SQLite databases");
            this.databaseType = databaseType;
            return this;
        }

        public Builder withHost(String host) {
            if (databaseType == SQLITE) throw new IllegalArgumentException("Host is not required for SQLite databases");
            if (host == null || host.isBlank()) throw new IllegalArgumentException("Host cannot be null or blank");
            this.host = host;
            return this;
        }

        public Builder withPort(Integer port) {
            if (databaseType == SQLITE) throw new IllegalArgumentException("Port is not required for SQLite databases");
            if (port == null || port < 1 || port > 65535) throw new IllegalArgumentException("Port must be between 1 and 65535");
            this.port = port;
            return this;
        }

        public Builder withDatabase(String database) {
            if (database == null || database.isBlank()) throw new IllegalArgumentException("Database cannot be null or blank");
            this.database = database;
            return this;
        }

        public Builder withUsername(String username) {
            if (databaseType == SQLITE) throw new IllegalArgumentException("Username is not required for SQLite databases");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be null or blank");
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            if (databaseType == SQLITE) throw new IllegalArgumentException("Password is not required for SQLite databases");
            if (password == null || password.isBlank()) throw new IllegalArgumentException("Password cannot be null or blank");
            this.password = password;
            return this;
        }
    }
}