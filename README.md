# MySQL-Driver
[![](https://jitpack.io/v/MCmoderSD/MySQL-Driver.svg)](https://jitpack.io/#MCmoderSD/MySQL-Driver)


## Description
A simple Java MySQL driver for connecting to a MySQL database.

## Usage

### Maven
Make sure you have the JitPack repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.github.MCmoderSD</groupId>
    <artifactId>MySQL-Driver</artifactId>
    <version>1.0.0</version>
</dependency>
```


## Usage Example
```java
import de.MCmoderSD.mysql.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends Driver {

    // Constructor
    public MySQL() {

        // Initialize Driver
        super("localhost", 3306, "database", "username", "password");

        System.out.println("Connected: " + isConnected());

        System.out.println("Row Count: " + getRowCount());
    }

    private int getRowCount() {

        // Initialize SQL Query
        String query = "SELECT COUNT(*) FROM table";

        // Initialize Statement
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            // Execute Query
            ResultSet resultSet = statement.executeQuery();

            // Return Result
            return resultSet.getInt(1);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return -1;
    }

    public static void main(String[] args) {
        new MySQL();
    }
}
```