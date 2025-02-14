package cn.org.expect.jdk;

import java.io.File;
import java.lang.Character.UnicodeBlock;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class JDK6 extends JDK5 {

    public int getNetworkTimeout(Connection conn) throws SQLException {
        return 0;
    }

    public String toLongname(File file) {
        return "";
    }

    public void setClientInfo(Connection conn, Properties p) throws SQLException {
        try {
            conn.setClientInfo(p);
        } catch (Throwable e) {
        }
    }

    public Properties getClientInfo(Connection conn) throws SQLException {
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

    public boolean isChineseLetter(UnicodeBlock ub) {
        return false;
    }

    public String getLink(File file) {
        return null;
    }

    public Date getCreateTime(String filepath) {
        return null;
    }
}
