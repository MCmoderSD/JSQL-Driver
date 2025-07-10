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
    <version>2.2.1</version>
</dependency>
```


## Usage Example

### MySQL/MariaDB/PostgreSQL
```java
import de.MCmoderSD.sql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database extends Driver {

    public Database(DatabaseType databaseType, String host, Integer port, String database, String username, String password) {
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
```

### SQLite
```java
import de.MCmoderSD.sql.Driver;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
```