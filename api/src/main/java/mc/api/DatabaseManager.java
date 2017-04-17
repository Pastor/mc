package mc.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {
    Connection connection() throws SQLException;
}
