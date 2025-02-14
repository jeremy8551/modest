package cn.org.expect.log.slf4j;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.LevelLogger;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.StackTraceUtils;
import cn.org.expect.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用 Slf4j 输出日志信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-09-13
 */
public class Slf4jLog extends LevelLogger {

    /** 日志接口 */
    private final Logger log;

    /** 行的集合 */
    private final List<String> list;

    /** 创建日志的堆栈信息 */
    protected StackTraceElement stackTrace;

    /** 日志所属的类名 */
    private final String type;

    public Slf4jLog(LogContext context, Class<?> type) {
        super(context);
        this.type = type.getName();
        this.list = new ArrayList<String>();
        this.log = LoggerFactory.getLogger(type);
        this.stackTrace = StackTraceUtils.get(LogFactory.class.getName());
    }

    public String getName() {
        return log.getName();
    }

    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isErrorEnabled();
    }

    public void trace(String message, Throwable cause) {
        boolean hasLines = StringUtils.contains(message, '\r', '\n');
        if (hasLines) {
            synchronized (this.list) {
                this.list.clear();
                StringUtils.splitLines(message, this.list);
                int size = this.list.size() - 1;
                for (int i = 0; i < size; i++) {
                    log.trace(this.list.get(i));
                }
                message = this.list.get(size);
            }
        }

        if (cause == null) {
            log.trace(message);
        } else {
            log.trace(message, cause);
        }
    }

    public void debug(String message, Throwable cause) {
        boolean hasLines = StringUtils.contains(message, '\r', '\n');
        if (hasLines) {
            synchronized (this.list) {
                this.list.clear();
                StringUtils.splitLines(message, this.list);
                int size = this.list.size() - 1;
                for (int i = 0; i < size; i++) {
                    log.debug(this.list.get(i));
                }
                message = this.list.get(size);
            }
        }

        if (cause == null) {
            log.debug(message);
        } else {
            log.debug(message, cause);
        }
    }

    public void info(String message, Throwable cause) {
        boolean hasLines = StringUtils.contains(message, '\r', '\n');
        if (hasLines) {
            synchronized (this.list) {
                this.list.clear();
                StringUtils.splitLines(message, this.list);
                int size = this.list.size() - 1;
                for (int i = 0; i < size; i++) {
                    log.info(this.list.get(i));
                }
                message = this.list.get(size);
            }
        }

        if (cause == null) {
            log.info(message);
        } else {
            log.info(message, cause);
        }
    }

    public void warn(String message, Throwable cause) {
        boolean hasLines = StringUtils.contains(message, '\r', '\n');
        if (hasLines) {
            synchronized (this.list) {
                this.list.clear();
                StringUtils.splitLines(message, this.list);
                int size = this.list.size() - 1;
                for (int i = 0; i < size; i++) {
                    log.warn(this.list.get(i));
                }
                message = this.list.get(size);
            }
        }

        if (cause == null) {
            log.warn(message);
        } else {
            log.warn(message, cause);
        }
    }

    public void error(String message, Throwable cause) {
        boolean hasLines = StringUtils.contains(message, '\r', '\n');
        if (hasLines) {
            synchronized (this.list) {
                this.list.clear();
                StringUtils.splitLines(message, this.list);
                int size = this.list.size() - 1;
                for (int i = 0; i < size; i++) {
                    log.error(this.list.get(i));
                }
                message = this.list.get(size);
            }
        }

        if (cause == null) {
            log.error(message);
        } else {
            log.error(message, cause);
        }
    }

    public void fatal(String message, Throwable cause) {
        this.error(message, cause);
    }

    public String toString() {
        String str = this.getClass().getName() + "{level=";
        if (log.isInfoEnabled()) {
            str += "info";
        } else if (log.isDebugEnabled()) {
            str += "debug";
        } else if (log.isWarnEnabled()) {
            str += "warn";
        } else if (log.isErrorEnabled()) {
            str += "error";
        } else if (log.isTraceEnabled()) {
            str += "trace";
        }
        str += ", class=" + this.type;
        str += ", line=(" + stackTrace.getFileName() + ":" + this.stackTrace.getLineNumber() + ")";
        str += ", logClass=" + log.getClass().getName();
        str += '}';
        return str;
    }
}
