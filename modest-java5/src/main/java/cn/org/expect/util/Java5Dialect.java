package cn.org.expect.util;

import java.io.File;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class Java5Dialect implements JavaDialect {

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
        Field field = this.findField(obj, fieldName);
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
        Field field = this.findField(obj, fieldName);
        if (field == null) {
            return null;
        } else {
            return this.getField(obj, field);
        }
    }

    /**
     * 在实例对象中搜索字段信息
     *
     * @param obj       实例对象
     * @param fieldName 字段名，大小写敏感
     * @return 字段信息
     */
    public Field findField(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }

        Class<?> cls = obj.getClass();
        Field field;
        while ((field = this.getField(cls, fieldName)) == null) {
            Class<?> superclass = cls.getSuperclass();
            if (superclass == null) {
                return null;
            } else {
                cls = superclass;
            }
        }
        return field;
    }

    public Field getField(Class<?> cls, String fieldName) {
        if (cls == null) {
            throw new NullPointerException(fieldName);
        }

        try {
            return cls.getDeclaredField(fieldName);
        } catch (Throwable e) {
            return null;
        }
    }
}
