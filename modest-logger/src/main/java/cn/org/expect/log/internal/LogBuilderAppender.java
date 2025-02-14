package cn.org.expect.log.internal;

import cn.org.expect.log.Appender;
import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogEvent;
import cn.org.expect.log.LogFactory;

/**
 * 记录器，用于获取输出的日志内容
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/26
 */
public class LogBuilderAppender extends PatternConsoleAppender {

    /** 日志内容 */
    private final StringBuilder buffer;

    public LogBuilderAppender(String pattern) {
        super(pattern);
        this.buffer = new StringBuilder();
    }

    public LogBuilderAppender() {
        this(LogFactory.SOUT_PATTERN);
    }

    public Appender setup(LogContext context) {
        context.removeAppender(LogBuilderAppender.class);
        context.addAppender(this);
        return this;
    }

    public String getName() {
        return LogBuilderAppender.class.getSimpleName();
    }

    public void append(LogEvent event) {
        this.buffer.append(this.layout.format(event));
    }

    public void close() {
        this.buffer.setLength(0);
    }

    public String toString() {
        return this.buffer.toString();
    }
}
