package cn.org.expect.jdk;

import java.io.File;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import cn.org.expect.util.ClassUtils;

public class JDK5 implements JavaDialect {

    public int getNetworkTimeout(Connection conn) throws SQLException {
        return 0;
    }

    public Properties getClientInfo(Connection conn) throws SQLException {
        return new Properties();
    }

    public boolean canExecute(File file) {
        return false;
    }

    public boolean isStatementClosed(Statement statement) throws SQLException {
        return true;
    }

    public void setClientInfo(Connection conn, Properties p) throws SQLException {
    }

    public boolean isChineseLetter(UnicodeBlock ub) {
        return false;
    }

    public String toLongname(File file) {
        return "";
    }

    public String getLink(File file) {
        return null;
    }

    public Date getCreateTime(String filepath) {
        return null;
    }

    public void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(obj, value);
        } catch (Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public void setField(Object obj, String fieldName, Object value) {
        Field field = ClassUtils.findField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException(fieldName);
        } else {
            this.setField(obj, field, value);
        }
    }

    public <E> E getField(Object obj, Field field) {
        try {
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            return (E) field.get(obj);
        } catch (Throwable e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public <E> E getField(Object obj, String fieldName) {
        Field field = ClassUtils.findField(obj, fieldName);
        if (field == null) {
            return null;
        } else {
            return this.getField(obj, field);
        }
    }
}
