package cn.org.expect.util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * 提供 6 版本对应的 Java 平台能力适配
 */
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

    public boolean isTypeElement(Object obj) {
        TypeElement typeElement = (TypeElement) obj;
        return typeElement.getKind() == ElementKind.CLASS //
            || typeElement.getKind() == ElementKind.INTERFACE //
            || typeElement.getKind() == ElementKind.ENUM //
            || typeElement.getKind() == ElementKind.ANNOTATION_TYPE //
            ;
    }
}
