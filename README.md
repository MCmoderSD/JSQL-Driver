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
    <version>3.0.1</version>
</dependency>
```


## Usage Example

### MySQL/MariaDB/PostgreSQL
```java
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
        Driver.Builder builder = Driver.Builder // Create Builder
                .withType(DatabaseType.MARIADB) // Database Type
                .withHost("localhost")          // Host
                .withPort(3306)                 // Port
                .withDatabase("database")       // Database
                .withUsername("username")       // Username
                .withPassword("password");      // Password

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
```

### SQLite
```java
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
```