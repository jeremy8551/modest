package cn.org.expect.log;

import cn.org.expect.util.ObjectUtils;

public abstract class LevelLogger extends LogMessageBundle implements Log, LogLevelAware {

    /** 日志级别 */
    protected volatile boolean trace;
    protected volatile boolean debug;
    protected volatile boolean info;
    protected volatile boolean warn;
    protected volatile boolean error;
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

    public void trace(String message, Object... args) {
        if (this.trace) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printTrace(message, args);
        }
    }

    public void trace(String message, Throwable e) {
        if (this.trace) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printTrace(message, e);
        }
    }

    public void debug(String message, Object... args) {
        if (this.debug) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printDebug(message, args);
        }
    }

    public void debug(String message, Throwable e) {
        if (this.debug) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printDebug(message, e);
        }
    }

    public void info(String message, Object... args) {
        if (this.info) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printInfo(message, args);
        }
    }

    public void info(String message, Throwable e) {
        if (this.info) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printInfo(message, e);
        }
    }

    public void warn(String message, Object... args) {
        if (this.warn) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printWarn(message, args);
        }
    }

    public void warn(String message, Throwable e) {
        if (this.warn) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printWarn(message, e);
        }
    }

    public void error(String message, Object... args) {
        if (this.error) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printError(message, args);
        }
    }

    public void error(String message, Throwable e) {
        if (this.error) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printError(message, e);
        }
    }

    public void fatal(String message, Object... args) {
        if (this.fatal) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message, args);
                args = ObjectUtils.of();
            }

            this.printFatal(message, args);
        }
    }

    public void fatal(String message, Throwable e) {
        if (this.fatal) {
            if (this.isResourceBundle(message)) {
                message = this.getResourceBundle(message);
            }

            this.printFatal(message, e);
        }
    }

    /**
     * 输出跟踪信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printTrace(String message, Object... args);

    /**
     * 输出跟踪信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printTrace(String message, Throwable e);

    /**
     * 输出调试信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printDebug(String message, Object... args);

    /**
     * 输出调试信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printDebug(String message, Throwable e);

    /**
     * 输出一般信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printInfo(String message, Object... args);

    /**
     * 输出一般信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printInfo(String message, Throwable e);

    /**
     * 输出警告信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printWarn(String message, Object... args);

    /**
     * 输出警告信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printWarn(String message, Throwable e);

    /**
     * 输出错误信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printError(String message, Object... args);

    /**
     * 输出错误信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printError(String message, Throwable e);

    /**
     * 输出严重错误信息
     *
     * @param message 字符串
     * @param args    参数数组
     */
    public abstract void printFatal(String message, Object... args);

    /**
     * 输出严重错误信息
     *
     * @param message 字符串
     * @param e       异常信息
     */
    public abstract void printFatal(String message, Throwable e);
}
