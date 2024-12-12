package cn.org.expect.log.console;

import cn.org.expect.log.AbstractLogger;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogLevel;
import cn.org.expect.util.StringUtils;

public class ConsoleLog extends AbstractLogger {

    /**
     * 构造方法
     *
     * @param context 日志工厂的上下文信息
     * @param type    日志关联类
     * @param level   日志级别
     */
    public ConsoleLog(LogContext context, Class<?> type, LogLevel level) {
        super(context, type, level);
    }

    public void printTrace(String message, Object... args) {
        System.out.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printTrace(String message, Throwable e) {
        System.out.println(StringUtils.toString(message, e));
    }

    public void printDebug(String message, Object... args) {
        System.out.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printDebug(String message, Throwable e) {
        System.out.println(StringUtils.toString(message, e));
    }

    public void printInfo(String message, Object... args) {
        System.out.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printInfo(String message, Throwable e) {
        System.out.println(StringUtils.toString(message, e));
    }

    public void printWarn(String message, Object... args) {
        System.out.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printWarn(String message, Throwable e) {
        System.out.println(StringUtils.toString(message, e));
    }

    public void printError(String message, Object... args) {
        System.err.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printError(String message, Throwable e) {
        System.err.println(StringUtils.toString(message, e));
    }

    public void printFatal(String message, Object... args) {
        System.err.println(StringUtils.replaceEmptyHolder(message, args));
    }

    public void printFatal(String message, Throwable e) {
        System.err.println(StringUtils.toString(message, e));
    }
}
