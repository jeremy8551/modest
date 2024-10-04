package cn.org.expect.log;

import java.io.IOException;

import cn.org.expect.log.apd.Layout;
import cn.org.expect.log.apd.LogEvent;
import cn.org.expect.log.apd.LogPattern;
import cn.org.expect.log.apd.NoPatternLayout;
import cn.org.expect.log.apd.PatternLayout;

/**
 * 控制台输出日志
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/22
 */
public class PatternConsoleAppender implements Appender {

    protected Layout layout;

    public PatternConsoleAppender(String pattern) {
        this.pattern(pattern);
    }

    public String getName() {
        return PatternConsoleAppender.class.getSimpleName();
    }

    public Appender setup(LogContext context) {
        context.removeAppender(PatternConsoleAppender.class);
        context.addAppender(this);
        return this;
    }

    /**
     * 设置日志格式
     *
     * @param pattern 日志格式, {@linkplain LogPattern}
     */
    public PatternConsoleAppender pattern(String pattern) {
        if (pattern != null && pattern.length() > 0) {
            this.layout = new PatternLayout(pattern);
        } else {
            this.layout = new NoPatternLayout();
        }
        return this;
    }

    /**
     * 返回日志格式
     *
     * @return 日志格式
     */
    public String getPattern() {
        return layout.getPattern();
    }

    public void append(LogEvent event) {
        switch (event.getLevel()) {
            case TRACE:
            case DEBUG:
            case INFO:
            case WARN:
                System.out.print(this.layout.format(event));
                return;

            case ERROR:
            case FATAL:
                System.err.print(this.layout.format(event));
                return;

            case OFF:
                return;
        }
    }

    public void close() throws IOException {
    }
}
