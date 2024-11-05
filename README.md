# MySQL-Driver

## Description
A simple Java MySQL driver for connecting to a MySQL database.

## Usage

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.MCmoderSD</groupId>
        <artifactId>mysql</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
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