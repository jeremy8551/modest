package cn.org.expect.log;

public abstract class AbstractLog implements Log, LogLevelAware {

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

    /** 日志所属类 */
    protected Class<?> type;

    public String getName() {
        return this.type.getName();
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
}
