package cn.org.expect.log;

import java.util.logging.Level;

import cn.org.expect.util.Logs;

public class JUL implements Logger {

    /** JDK 日志接口实例 */
    public final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JUL.class.getName());

    /** 实例对象 */
    public final static JUL out = new JUL();

    static {
        JULHandler.reset(logger);
    }

    private JUL() {
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.CONFIG);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public boolean isFatalEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public void trace(Object msg, Object... args) {
        logger.log(Level.FINEST, Logs.toString(msg, args));
    }

    public void debug(Object msg, Object... args) {
        logger.log(Level.CONFIG, Logs.toString(msg, args));
    }

    public void info(Object msg, Object... args) {
        logger.log(Level.INFO, Logs.toString(msg, args));
    }

    public void warn(Object msg, Object... args) {
        logger.log(Level.WARNING, Logs.toString(msg, args));
    }

    public void error(Object msg, Object... args) {
        logger.log(Level.SEVERE, Logs.toString(msg, args));
    }

    public void fatal(Object msg, Object... args) {
        logger.log(Level.SEVERE, Logs.toString(msg, args));
    }
}
