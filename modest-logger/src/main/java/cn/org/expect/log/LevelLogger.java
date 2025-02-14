package cn.org.expect.log;

import cn.org.expect.util.ErrorUtils;
import cn.org.expect.util.StringUtils;

public abstract class LevelLogger extends LogMessageBundle implements Log, LogLevelAware {

    /** 日志级别 */
    protected volatile boolean trace;

    /** 日志级别 */
    protected volatile boolean debug;

    /** 日志级别 */
    protected volatile boolean info;

    /** 日志级别 */
    protected volatile boolean warn;

    /** 日志级别 */
    protected volatile boolean error;

    /** 日志级别 */
    protected volatile boolean fatal;

    public LevelLogger(LogContext context) {
        super(context);
    }

    public void setLevel(LogLevel level) {
        switch (level) {
            case TRACE:
                this.trace = true;
                this.debug = true;
                this.info = true;
                this.warn = true;
                this.error = true;
                this.fatal = true;
                break;

            case DEBUG:
                this.trace = false;
                this.debug = true;
                this.info = true;
                this.warn = true;
                this.error = true;
                this.fatal = true;
                break;

            case INFO:
                this.trace = false;
                this.debug = false;
                this.info = true;
                this.warn = true;
                this.error = true;
                this.fatal = true;
                break;

            case WARN:
                this.trace = false;
                this.debug = false;
                this.info = false;
                this.warn = true;
                this.error = true;
                this.fatal = true;
                break;

            case ERROR:
                this.trace = false;
                this.debug = false;
                this.info = false;
                this.warn = false;
                this.error = true;
                this.fatal = true;
                break;

            case FATAL:
                this.trace = false;
                this.debug = false;
                this.info = false;
                this.warn = false;
                this.error = false;
                this.fatal = true;
                break;

            case OFF:
                this.trace = false;
                this.debug = false;
                this.info = false;
                this.warn = false;
                this.error = false;
                this.fatal = false;
                break;

            default:
                throw new UnsupportedOperationException(String.valueOf(level));
        }
    }

    public boolean isTraceEnabled() {
        return trace;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean isInfoEnabled() {
        return info;
    }

    public boolean isWarnEnabled() {
        return warn;
    }

    public boolean isErrorEnabled() {
        return error;
    }

    public boolean isFatalEnabled() {
        return fatal;
    }

    public void trace(Object message, Object... args) {
        if (this.isTraceEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.trace(text, cause);
        }
    }

    public void debug(Object message, Object... args) {
        if (this.isDebugEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.debug(text, cause);
        }
    }

    public void info(Object message, Object... args) {
        if (this.isInfoEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.info(text, cause);
        }
    }

    public void warn(Object message, Object... args) {
        if (this.isWarnEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.warn(text, cause);
        }
    }

    public void error(Object message, Object... args) {
        if (this.isErrorEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.error(text, cause);
        }
    }

    public void fatal(Object message, Object... args) {
        if (this.isFatalEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.fatal(text, cause);
        }
    }

    /**
     * 输出跟踪信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void trace(String message, Throwable cause);

    /**
     * 输出调试信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void debug(String message, Throwable cause);

    /**
     * 输出一般信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void info(String message, Throwable cause);

    /**
     * 输出警告信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void warn(String message, Throwable cause);

    /**
     * 输出错误信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void error(String message, Throwable cause);

    /**
     * 输出严重错误信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void fatal(String message, Throwable cause);
}
