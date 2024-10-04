package cn.org.expect.log.console;

import cn.org.expect.log.AbstractLogger;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogLevel;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;

public class ConsoleLog extends AbstractLogger {

    protected MessageFormatter formater;

    /**
     * 构造方法
     *
     * @param context 日志工厂的上下文信息
     * @param type    日志关联类
     * @param level   日志级别
     */
    public ConsoleLog(LogContext context, Class<?> type, LogLevel level) {
        super(context, type, level);
        this.formater = new MessageFormatter();
    }

    public void trace(String message, Object... args) {
        System.out.println(this.formater.format(message, args));
    }

    public void trace(String message, Throwable e) {
        System.out.println(this.toMessage(message, e));
    }

    public void debug(String message, Object... args) {
        System.out.println(this.formater.format(message, args));
    }

    public void debug(String message, Throwable e) {
        System.out.println(this.toMessage(message, e));
    }

    public void info(String message, Object... args) {
        System.out.println(this.formater.format(message, args));
    }

    public void info(String message, Throwable e) {
        System.out.println(this.toMessage(message, e));
    }

    public void warn(String message, Object... args) {
        System.out.println(this.formater.format(message, args));
    }

    public void warn(String message, Throwable e) {
        System.out.println(this.toMessage(message, e));
    }

    public void error(String message, Object... args) {
        System.err.println(this.formater.format(message, args));
    }

    public void error(String message, Throwable e) {
        System.err.println(this.toMessage(message, e));
    }

    public void fatal(String message, Object... args) {
        System.err.println(this.formater.format(message, args));
    }

    public void fatal(String message, Throwable e) {
        System.err.println(this.toMessage(message, e));
    }

    protected String toMessage(String message, Throwable e) {
        String error = StringUtils.toString(e);
        StringBuilder buf = new StringBuilder(message.length() + error.length() + 4);
        buf.append(message);
        buf.append(FileUtils.lineSeparator);
        buf.append(error);
        buf.append(FileUtils.lineSeparator);
        return buf.toString();
    }
}
