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
 * @author jeremy8551@qq.com
 * @createtime 2023-09-13
 */
public class Slf4jLog extends LevelLogger {

    /** 日志接口 */
    private final Logger log;

    /** 行的集合 */
    private final List<CharSequence> list;

    /** 创建日志的堆栈信息 */
    protected StackTraceElement stackTrace;

    /** 日志所属的类名 */
    private final String type;

    public Slf4jLog(LogContext context, Class<?> type) {
        super(context);
        this.type = type.getName();
        this.list = new ArrayList<CharSequence>();
        this.log = LoggerFactory.getLogger(type);
        this.stackTrace = StackTraceUtils.get(LogFactory.class.getName());
        // 如果对 Slf4j，log4j等日志进行封装，会导致日志输出代码行数、代码所在的类名、代码所在的方法，不准确，就需要用这个方法对日志中的 fqcn 字段进行调整
//        ClassUtils.setField(this.log, "FQCN.*", String.class, fqcn == null ? Slf4jLog.FQCN : fqcn);
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

    public void printTrace(String message, Object... args) {
        if (StringUtils.contains(message, '\r', '\n')) {
            synchronized (this.list) {
                StringUtils.splitLines(message, this.list);
                for (int i = 0, size = this.list.size(); i < size; i++) {
                    log.trace(this.list.get(i).toString(), args);
                }
            }
        } else {
            log.trace(message, args);
        }
    }

    public void printTrace(String message, Throwable e) {
        log.trace(message, e);
    }

    public void printDebug(String message, Object... args) {
        if (StringUtils.contains(message, '\r', '\n')) {
            synchronized (this.list) {
                StringUtils.splitLines(message, this.list);
                for (int i = 0, size = this.list.size(); i < size; i++) {
                    log.debug(this.list.get(i).toString(), args);
                }
            }
        } else {
            log.debug(message, args);
        }
    }

    public void printDebug(String message, Throwable e) {
        log.debug(message, e);
    }

    public void printInfo(String message, Object... args) {
        if (StringUtils.contains(message, '\r', '\n')) {
            synchronized (this.list) {
                StringUtils.splitLines(message, this.list);
                for (int i = 0, size = this.list.size(); i < size; i++) {
                    log.info(this.list.get(i).toString(), args);
                }
            }
        } else {
            log.info(message, args);
        }
    }

    public void printInfo(String message, Throwable e) {
        log.info(message, e);
    }

    public void printWarn(String message, Object... args) {
        if (StringUtils.contains(message, '\r', '\n')) {
            synchronized (this.list) {
                StringUtils.splitLines(message, this.list);
                for (int i = 0, size = this.list.size(); i < size; i++) {
                    log.warn(this.list.get(i).toString(), args);
                }
            }
        } else {
            log.warn(message, args);
        }
    }

    public void printWarn(String message, Throwable e) {
        log.warn(message, e);
    }

    public void printError(String message, Object... args) {
        if (StringUtils.contains(message, '\r', '\n')) {
            synchronized (this.list) {
                StringUtils.splitLines(message, this.list);
                for (int i = 0, size = this.list.size(); i < size; i++) {
                    log.error(this.list.get(i).toString(), args);
                }
            }
        } else {
            log.error(message, args);
        }
    }

    public void printError(String message, Throwable e) {
        log.error(message, e);
    }

    public void printFatal(String message, Object... args) {
        this.printError(message, args);
    }

    public void printFatal(String message, Throwable e) {
        this.printError(message, e);
    }

    public String toString() {
        String str = this.getClass().getName() + "{ level=";
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
