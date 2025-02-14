package cn.org.expect.log;

import cn.org.expect.util.Logs;

/**
 * 控制台输出日志
 */
public class Console implements Logger {

    /** 实例对象 */
    public final static Console out = new Console();

    private Console() {
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isFatalEnabled() {
        return true;
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
