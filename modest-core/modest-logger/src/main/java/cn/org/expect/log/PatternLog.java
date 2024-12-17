package cn.org.expect.log;

import java.util.List;

import cn.org.expect.log.apd.LogEvent;
import cn.org.expect.log.apd.LogEventImpl;

/**
 * 带格式的日志接口
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-06-28
 */
public class PatternLog extends AbstractLogger {

    /** 日志事件 */
    private final LogEventImpl template;

    /** 日志记录器集合 */
    private final List<Appender> appenderList;

    /** 用于定位输出日志的代码位置信息的标识符 */
    public static String FQCN = LevelLogger.class.getName();

    public PatternLog(LogContext context, Class<?> type, LogLevel level, String fqcn, boolean dynamicCategory) {
        super(context, type, level);
        this.appenderList = context.getAppenders();
        this.template = new LogEventImpl(fqcn == null ? FQCN : fqcn, this, type.getName(), context, dynamicCategory, null, null, null, null);
    }

    public void setFqcn(String fqcn) {
        this.template.setFqcn(fqcn == null ? FQCN : fqcn);
    }

    public void printTrace(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.TRACE, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printTrace(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.TRACE, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printDebug(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.DEBUG, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printDebug(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.DEBUG, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printInfo(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.INFO, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printInfo(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.INFO, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printWarn(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.WARN, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printWarn(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.WARN, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printError(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.ERROR, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printError(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.ERROR, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printFatal(String message, Object... args) {
        LogEvent event = this.template.clone(LogLevel.FATAL, message, args, null);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void printFatal(String message, Throwable e) {
        LogEvent event = this.template.clone(LogLevel.FATAL, message, null, e);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public String toString() {
        return super.toString() + ", fqcn=" + this.template.getFqcn() + ", dynamicCategory=" + this.template.isDynamicCategory() + "}";
    }
}