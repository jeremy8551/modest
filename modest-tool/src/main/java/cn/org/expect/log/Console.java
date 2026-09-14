package cn.org.expect.log;

import cn.org.expect.util.Logs;
import cn.org.expect.util.StringUtils;

/**
 * 控制台输出日志
 */
public class Console extends AbstractLog implements Log, LogLevelAware {

    /** 实例对象 */
    public final static Console out = new Console();

    private Console() {
        String logLevel = System.getProperty(Log.PROPERTY_LOGGER);
        LogLevel level = StringUtils.isEmpty(logLevel) ? LogLevel.of(logLevel) : LogLevel.INFO;
        this.setLevel(level);
    }

    public String getName() {
        return Console.class.getName();
    }

    public void trace(Object message, Object... args) {
        System.out.println(Logs.toString(message, args));
    }

    public void debug(Object message, Object... args) {
        System.out.println(Logs.toString(message, args));
    }

    public void info(Object message, Object... args) {
        System.out.println(Logs.toString(message, args));
    }

    public void warn(Object message, Object... args) {
        System.out.println(Logs.toString(message, args));
    }

    public void error(Object message, Object... args) {
        System.err.println(Logs.toString(message, args));
    }

    public void fatal(Object message, Object... args) {
        System.err.println(Logs.toString(message, args));
    }
}
