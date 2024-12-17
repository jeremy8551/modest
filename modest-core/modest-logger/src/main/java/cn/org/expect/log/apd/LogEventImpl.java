package cn.org.expect.log.apd;

import cn.org.expect.log.FqcnAware;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogLevel;
import cn.org.expect.util.StackTraceUtils;

/**
 * 日志事件实现类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/21
 */
public class LogEventImpl implements LogEvent {

    private final Log log;
    private final LogContext context;
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;
    private final Object[] args;
    private final String category;

    /** true表示动态生成日志接口归属的类名, false表示使用 {@code type} 作为日志接口归属的类名 */
    private final boolean dynamicCategory;

    /** 用于定位输出日志的代码位置信息的标识符，详见 {@linkplain FqcnAware#setFqcn(String)} */
    private String fqcn;

    /** 堆栈信息 */
    private StackTraceElement stackTraceElement;

    public LogEventImpl(String fqcn, Log log, String category, LogContext context, boolean dynamicCategory, LogLevel level, String message, Object[] args, Throwable throwable) {
        this.fqcn = fqcn;
        this.log = log;
        this.context = context;
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.args = args;
        this.category = category;
        this.dynamicCategory = dynamicCategory;
    }

    public LogEvent clone(String fqcn, LogLevel level, String message, Object[] args, Throwable throwable) {
        return new LogEventImpl(fqcn, this.log, this.category, this.context, this.dynamicCategory, level, message, args, throwable);
    }

    public LogEvent clone(String message, Object[] args, Throwable throwable) {
        return new LogEventImpl(this.fqcn, this.log, this.category, this.context, this.dynamicCategory, this.level, message, args, throwable);
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public String getFqcn() {
        return fqcn;
    }

    public StackTraceElement getStackTraceElement() {
        if (this.stackTraceElement == null) {
            this.setStackTraceElement(StackTraceUtils.get(this.getFqcn()));
        }
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public String getCategory() {
        if (this.dynamicCategory) {
            return this.getStackTraceElement().getClassName();
        } else {
            return category;
        }
    }

    public Log getLog() {
        return log;
    }

    public LogContext getContext() {
        return context;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object[] getArgs() {
        return args;
    }

    public boolean isDynamicCategory() {
        return dynamicCategory;
    }
}
