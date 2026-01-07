package cn.org.expect.util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Java6Dialect extends Java5Dialect {

    public void setClientInfo(Connection conn, Properties p) {
        try {
            conn.setClientInfo(p);
        } catch (Throwable e) {
        }
    }

    public Properties getClientInfo(Connection conn) {
        try {
            return conn.getClientInfo();
        } catch (Throwable e) {
            return new Properties();
        }
    }

    public boolean canExecute(File file) {
        return file.canExecute();
    }

    public boolean isStatementClosed(Statement statement) throws SQLException {
        return statement == null || statement.isClosed();
    }
}
