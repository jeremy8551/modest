package cn.org.expect.intellij.idea.plugin.maven.log;

import cn.org.expect.log.BaseLogger;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogContext;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class IdeaLog extends BaseLogger implements Log {

    protected Class<?> type;

    protected Logger log;

    public IdeaLog(LogContext context, Class<?> type) {
        super(context);
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
        if (this.isResourceBundle(message)) {
            message = this.getResourceBundle(message, args);
            args = BLANK;
        }

        log.trace(MavenMessage.toString(message, args));
    }

    public void trace(String message, Throwable e) {
        log.trace(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void debug(String message, Object... args) {
        log.debug(MavenMessage.toString(message, args));
    }

    public void debug(String message, Throwable e) {
        log.debug(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void info(String message, Object... args) {
        log.info(MavenMessage.toString(message, args));
    }

    public void info(String message, Throwable e) {
        log.info(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void warn(String message, Object... args) {
        log.warn(MavenMessage.toString(message, args));
    }

    public void warn(String message, Throwable e) {
        log.warn(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void error(String message, Object... args) {
        log.error(MavenMessage.toString(message, args));
    }

    public void error(String message, Throwable e) {
        log.error(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }

    public void fatal(String message, Object... args) {
        log.error(MavenMessage.toString(message, args));
    }

    public void fatal(String message, Throwable e) {
        log.error(MavenMessage.toString(message) + FileUtils.lineSeparator + StringUtils.toString(e));
    }
}
