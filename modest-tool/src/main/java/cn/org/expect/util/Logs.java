package cn.org.expect.util;

import cn.org.expect.log.JUL;
import cn.org.expect.log.Log;

/**
 * JDK日志系统
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/24
 */
public class Logs {

    /** 日志接口 */
    private volatile static Log out = JUL.out;

    /**
     * 设置日志接口
     *
     * @param log 日志接口
     */
    public static synchronized void setLogger(Log log) {
        Logs.out = Ensure.notNull(log);
    }

    /**
     * 返回日志接口
     *
     * @return 日志接口
     */
    public static Log getLog() {
        return out;
    }

    /**
     * 转为字符串
     *
     * @param message 日志信息
     * @param args    参数数组
     * @return 字符串
     */
    public static String toString(Object message, Object[] args) {
        String key = message == null ? "null" : message.toString();
        Throwable cause = ErrorUtils.getThrowable(args);

        String text;
        if (ResourcesUtils.existsMessage(key)) {
            text = ResourcesUtils.getMessage(key, args);
        } else {
            text = StringUtils.replaceEmptyHolder(key, args);
        }

        if (cause == null) {
            return text;
        } else {
            return StringUtils.joinLineSeparator(text, ErrorUtils.toString(cause));
        }
    }

    public static boolean isTraceEnabled() {
        return getLog().isTraceEnabled();
    }

    public static boolean isDebugEnabled() {
        return getLog().isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return getLog().isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        return getLog().isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return getLog().isErrorEnabled();
    }

    public static boolean isFatalEnabled() {
        return getLog().isFatalEnabled();
    }

    public static void trace(Object msg, Object... args) {
        getLog().trace(msg, args);
    }

    public static void debug(Object msg, Object... args) {
        getLog().debug(msg, args);
    }

    public static void info(Object msg, Object... args) {
        getLog().info(msg, args);
    }

    public static void warn(Object msg, Object... args) {
        getLog().warn(msg, args);
    }

    public static void error(Object msg, Object... args) {
        getLog().error(msg, args);
    }

    public static void fatal(Object msg, Object... args) {
        getLog().fatal(msg, args);
    }
}
