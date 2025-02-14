package cn.org.expect.log.internal;

import java.util.List;

import cn.org.expect.log.AbstractLogger;
import cn.org.expect.log.Appender;
import cn.org.expect.log.FQCNAware;
import cn.org.expect.log.LevelLogger;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogEvent;
import cn.org.expect.log.LogLevel;

/**
 * 带格式的日志接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-06-28
 */
public class PatternLog extends AbstractLogger implements FQCNAware {

    /** 用于定位输出日志的代码位置信息的标识符 */
    public static String FQCN = LevelLogger.class.getName();

    /** 日志事件 */
    private final LogEventImpl template;

    /** 日志记录器集合 */
    private final List<Appender> appenderList;

    public PatternLog(LogContext context, Class<?> type, LogLevel level, String fqcn, boolean dynamicCategory) {
        super(context, type, level);

        if (fqcn == null) {
            fqcn = FQCN;
        }

        this.appenderList = context.getAppenders();
        this.template = new LogEventImpl(fqcn, this, type.getName(), context, dynamicCategory, null, null, null);
        this.setFQCN(fqcn);
    }

    public void trace(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.TRACE, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void debug(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.DEBUG, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void info(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.INFO, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void warn(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.WARN, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void error(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.ERROR, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public void fatal(String message, Throwable cause) {
        LogEvent event = this.template.clone(this.getFqcn(), LogLevel.FATAL, message, cause);
        for (int i = 0; i < this.appenderList.size(); i++) {
            this.appenderList.get(i).append(event);
        }
    }

    public String toString() {
        return super.toString() + ", fqcn=" + this.template.getFqcn() + ", dynamicCategory=" + this.template.isDynamicCategory() + "}";
    }
}
