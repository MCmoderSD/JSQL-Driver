package de.MCmoderSD.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings({"unused", "UnusedReturnValue", "BusyWait"})
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
    protected Thread reconnectThread;

    // Variables
    protected boolean autoReconnect;   // Default: false
    protected int reconnectAttempts;   // Default: 0 (infinite)
    protected long reconnectDelay;     // Default: 1000ms

    // Constructor
    protected Driver(Builder builder) {

        // Check Builder
        if (builder == null) throw new IllegalArgumentException("Builder cannot be null");

        // Set Attributes
        this.databaseType = builder.databaseType;
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;

        // Build URL
        if (databaseType == DatabaseType.SQLITE) this.url = databaseType.getUrl(database);
        else this.url = databaseType.getUrl(host, port, database);

        // Set Default
        autoReconnect = false;
        reconnectAttempts = 0;
        reconnectDelay = 1000;
    }

    private void autoReconnect() {

        // Create the thread
        reconnectThread = new Thread(() -> {
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
        });

        // Start the thread
        reconnectThread.start();
    }

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

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        if (autoReconnect) autoReconnect();
    }

    public void setAutoReconnectSettings(int attempts, long delay) {
        this.reconnectAttempts = attempts;
        this.reconnectDelay = delay;
    }

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

        // Driver Registration
        private Class<?> registerDriver() throws ClassNotFoundException {
            return Class.forName(classPath);
        }

        // Get URL Methods
        private String getUrl(String host, Integer port, String database) {
            return String.format(urlPattern, host, port, database);
        }

        private String getUrl(String database) {
            return String.format(urlPattern, database);
        }
    }

    // Builder Class
    public static class Builder {

        // Attributes
        protected DatabaseType databaseType;
        protected String host;
        protected Integer port;
        protected String database;
        protected String username;
        protected String password;

        // Constructor
        private Builder(DatabaseType databaseType) {
            this.databaseType = databaseType;
        }

        // Initialize Builder
        public static Builder withType(DatabaseType databaseType) {
            return new Builder(databaseType);
        }

        // Builder Methods
        public Builder withHost(String host) {
            if (databaseType == DatabaseType.SQLITE) throw new IllegalArgumentException("Host is not required for SQLite databases");
            if (host == null || host.isBlank()) throw new IllegalArgumentException("Host cannot be null or blank");
            this.host = host;
            return this;
        }

        public Builder withPort(Integer port) {
            if (databaseType == DatabaseType.SQLITE) throw new IllegalArgumentException("Port is not required for SQLite databases");
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
            if (databaseType == DatabaseType.SQLITE) throw new IllegalArgumentException("Username is not required for SQLite databases");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be null or blank");
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            if (databaseType == DatabaseType.SQLITE) throw new IllegalArgumentException("Password is not required for SQLite databases");
            if (password == null || password.isBlank()) throw new IllegalArgumentException("Password cannot be null or blank");
            this.password = password;
            return this;
        }
    }
}