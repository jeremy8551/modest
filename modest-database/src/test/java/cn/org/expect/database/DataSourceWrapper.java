package cn.org.expect.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DataSourceWrapper implements DataSource {

    private final Connection conn;

    public DataSourceWrapper(Connection conn) {
        super();
        this.conn = conn;
    }

    public Connection getConnection() {
        return this.conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
