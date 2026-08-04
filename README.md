# JSQL-Driver

## Description
A simple Java SQL driver for connecting to a SQL databases. 

## Supported Databases
- [x] MariaDB
- [x] MySQL
- [x] PostgreSQL
- [x] SQLite

## Usage

### Maven
Make sure you have my Sonatype Nexus OSS repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>Nexus</id>
        <name>Sonatype Nexus</name>
        <url>https://mcmodersd.de/nexus/repository/maven-releases/</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>de.MCmoderSD</groupId>
    <artifactId>JSQL-Driver</artifactId>
    <version>3.1.4</version>
</dependency>
```


## Usage Example

### MySQL/MariaDB/PostgreSQL
```java
import de.MCmoderSD.sql.Driver;

import java.sql.SQLException;

import static de.MCmoderSD.sql.Driver.DatabaseType.MARIADB;
import static java.lang.IO.println;

void main() {

    // Build Database Configuration
    var builder = Database.builder()    // Create Builder
            .withType(MARIADB)          // Database Type
            .withHost("localhost")      // Host
            .withPort(3306)             // Port
            .withDatabase("database")   // Database
            .withUsername("username")   // Username
            .withPassword("password");  // Password

    // Initialize Database Connection
    var database = new Database(builder);
    database.setAutoReconnectSettings(5, 10000);    // Auto Reconnect Settings (5 Attempts, 10s Delay)
    database.setAutoReconnect(true);                // Enable Auto Reconnect
    database.connect();                             // Connect to Database

    // Test Database
    println("Connected: " + database.isConnected());
    println("Row Count: " + database.getRowCount());
}

// Database Driver Implementation
private static class Database extends Driver {

    // Constructor
    public Database(Builder builder) {
        super(builder); // Initialize Driver
    }

    // Get Row Count Method
    public Integer getRowCount() {

        // Initialize Statement
        try (var statement = connection.prepareStatement("SELECT COUNT(*) FROM `table`")) {

            // Execute Query
            var resultSet = statement.executeQuery();

            // Return Result
            if (resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}
```

### SQLite
```java
import de.MCmoderSD.sql.Driver;

import java.sql.SQLException;

import static de.MCmoderSD.sql.Driver.DatabaseType.SQLITE;
import static java.lang.IO.println;

void main() {

    // Build SQLite Configuration
    var builder = SQLite.builder()
            .withType(SQLITE)               // Database Type
            .withDatabase("Database.db");   // Database File

    // Initialize Database Connection
    var database = new SQLite(builder);
    database.connect();

    // Test Database
    println("Connected: " + database.isConnected());
    println("Row Count: " + database.getRowCount());
}

// SQLite Driver Implementation
private static class SQLite extends Driver {

    // Constructor
    public SQLite(Builder builder) {
        super(builder); // Initialize Driver
    }

    // Get Row Count Method
    public Integer getRowCount() {

        // Initialize Statement
        try (var statement = connection.prepareStatement("SELECT COUNT(*) FROM `table`")) {

            // Execute Query
            var resultSet = statement.executeQuery();

            // Return Result
            if (resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}
```