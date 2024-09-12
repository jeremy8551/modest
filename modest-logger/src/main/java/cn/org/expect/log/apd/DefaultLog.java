package cn.org.expect.log.apd;

import java.util.List;

import cn.org.expect.log.AbstractLogger;
import cn.org.expect.log.Appender;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogLevel;

/**
 * 带格式的日志接口
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-06-28
 */
public class DefaultLog extends AbstractLogger {

    /** 日志事件 */
    private LogEventImpl template;

    /** 日志记录器集合 */
    private List<Appender> appenders;

    /** 用于定位输出日志的代码位置信息的标识符 */
    public static String FQCN = DefaultLog.class.getName();

    public DefaultLog(LogContext context, Class<?> type, LogLevel level, String fqcn, boolean dynamicCategory) {
        super(context, type, level);
        this.appenders = context.getAppenders();
        this.template = new LogEventImpl(fqcn == null ? FQCN : fqcn, this, type.getName(), context, dynamicCategory, null, null, null, null);
    }

    public void setFqcn(String fqcn) {
        this.template.setFqcn(fqcn == null ? FQCN : fqcn);
    }

    public void trace(String msg, Object... args) {
        if (this.trace) {
            LogEvent event = this.template.clone(LogLevel.TRACE, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void trace(String msg, Throwable e) {
        if (this.trace) {
            LogEvent event = this.template.clone(LogLevel.TRACE, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void debug(String msg, Object... args) {
        if (this.debug) {
            LogEvent event = this.template.clone(LogLevel.DEBUG, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void debug(String msg, Throwable e) {
        if (this.debug) {
            LogEvent event = this.template.clone(LogLevel.DEBUG, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void info(String msg, Object... args) {
        if (this.info) {
            LogEvent event = this.template.clone(LogLevel.INFO, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void info(String msg, Throwable e) {
        if (this.info) {
            LogEvent event = this.template.clone(LogLevel.INFO, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void warn(String msg, Object... args) {
        if (this.warn) {
            LogEvent event = this.template.clone(LogLevel.WARN, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void warn(String msg, Throwable e) {
        if (this.warn) {
            LogEvent event = this.template.clone(LogLevel.WARN, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void error(String msg, Object... args) {
        if (this.error) {
            LogEvent event = this.template.clone(LogLevel.ERROR, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void error(String msg, Throwable e) {
        if (this.error) {
            LogEvent event = this.template.clone(LogLevel.ERROR, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void fatal(String msg, Object... args) {
        if (this.fatal) {
            LogEvent event = this.template.clone(LogLevel.FATAL, msg, args, null);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public void fatal(String msg, Throwable e) {
        if (this.fatal) {
            LogEvent event = this.template.clone(LogLevel.FATAL, msg, null, e);
            for (Appender appender : this.appenders) {
                appender.append(event);
            }
        }
    }

    public String toString() {
        return super.toString() + ", fqcn=" + this.template.getFqcn() + ", dynamicCategory=" + this.template.isDynamicCategory() + "}";
    }
}