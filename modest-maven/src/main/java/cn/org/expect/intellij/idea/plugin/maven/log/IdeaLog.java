package cn.org.expect.intellij.idea.plugin.maven.log;

import cn.org.expect.log.LevelLogger;
import cn.org.expect.log.LogContext;
import cn.org.expect.util.StringUtils;
import com.intellij.openapi.diagnostic.Logger;

public class IdeaLog extends LevelLogger {

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

    public void printTrace(String message, Object... args) {
        log.trace(StringUtils.replacePlaceholder(message, args));
    }

    public void printTrace(String message, Throwable e) {
        log.trace(StringUtils.toString(message, e));
    }

    public void printDebug(String message, Object... args) {
        log.debug(StringUtils.replacePlaceholder(message, args));
    }

    public void printDebug(String message, Throwable e) {
        log.debug(StringUtils.toString(message, e));
    }

    public void printInfo(String message, Object... args) {
        log.info(StringUtils.replacePlaceholder(message, args));
    }

    public void printInfo(String message, Throwable e) {
        log.info(StringUtils.toString(message, e));
    }

    public void printWarn(String message, Object... args) {
        log.warn(StringUtils.replacePlaceholder(message, args));
    }

    public void printWarn(String message, Throwable e) {
        log.warn(StringUtils.toString(message, e));
    }

    public void printError(String message, Object... args) {
        log.error(StringUtils.replacePlaceholder(message, args));
    }

    public void printError(String message, Throwable e) {
        log.error(StringUtils.toString(message, e));
    }

    public void printFatal(String message, Object... args) {
        log.error(StringUtils.replacePlaceholder(message, args));
    }

    public void printFatal(String message, Throwable e) {
        log.error(StringUtils.toString(message, e));
    }
}
