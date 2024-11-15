package cn.org.expect.maven.intellij.idea.log;

import cn.org.expect.log.Log;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.MessageFormatter;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class IdeaLog implements Log {

    protected Class<?> type;

    protected Logger log;

    public IdeaLog(Class<?> type) {
        this.type = type;
        this.log = Logger.getInstance(type);
    }

    public String getName() {
        return this.type.getName();
    }

    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isFatalEnabled() {
        return true;
    }

    public void trace(String message, Object... args) {
        log.trace(new MessageFormatter(message).fill(args));
    }

    public void trace(String message, Throwable e) {
        log.trace(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void debug(String message, Object... args) {
        log.debug(new MessageFormatter(message).fill(args));
    }

    public void debug(String message, Throwable e) {
        log.debug(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void info(String message, Object... args) {
        log.info(new MessageFormatter(message).fill(args));
    }

    public void info(String message, Throwable e) {
        log.info(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void warn(String message, Object... args) {
        log.warn(new MessageFormatter(message).fill(args));
    }

    public void warn(String message, Throwable e) {
        log.warn(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void error(String message, Object... args) {
        log.error(new MessageFormatter(message).fill(args));
    }

    public void error(String message, Throwable e) {
        log.error(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void fatal(String message, Object... args) {
        log.error(new MessageFormatter(message).fill(args));
    }

    public void fatal(String message, Throwable e) {
        log.error(message + FileUtils.lineSeparator + StringUtils.toString(e));
    }
}
