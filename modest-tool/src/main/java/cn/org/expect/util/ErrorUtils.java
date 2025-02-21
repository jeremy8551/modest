package cn.org.expect.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class ErrorUtils {

    /**
     * 判断最后一个元素是否为异常
     *
     * @param array 参数
     * @return 异常信息
     */
    public static Throwable getThrowable(Object[] array) {
        Object last = array.length > 0 ? array[array.length - 1] : null;
        if (last instanceof Throwable) {
            return (Throwable) last;
        } else {
            return null;
        }
    }

    /**
     * 转为字符串
     *
     * @param e 异常信息
     * @return 字符串
     */
    public static String toString(Throwable e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        e.printStackTrace(stream);
        try {
            stream.close();
        } catch (Exception ignored) {
        }

        StringBuilder buf;
        try {
            buf = new StringBuilder(out.toString(CharsetName.UTF_8));
        } catch (UnsupportedEncodingException uee) {
            buf = new StringBuilder(out.toString());
        }

        if (e instanceof SQLException) {
            SQLException sql = (SQLException) e;
            while (sql != null) {
                if (!StringUtils.endWithLineSeparator(buf)) {
                    buf.append(Settings.getLineSeparator());
                }
                buf.append(sql.getClass().getName());
                buf.append("[");
                buf.append("SQLSTATE = ");
                buf.append(sql.getSQLState());
                buf.append(", ERRORCODE = ");
                buf.append(sql.getErrorCode());
                buf.append(", MESSAGE = ");
                buf.append(sql.getMessage());
                buf.append("]");

                sql = sql.getNextException();
            }
        }
        return buf.toString();
    }
}
