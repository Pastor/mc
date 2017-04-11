package mc.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mc.api.DatabaseManager;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

public final class DefaultDatabaseManager implements DatabaseManager, Closeable {
    private final HikariDataSource dataSource;

    private DefaultDatabaseManager(String url) throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        dataSource = new HikariDataSource(config);
    }

    public DefaultDatabaseManager() throws ClassNotFoundException {
        this("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
