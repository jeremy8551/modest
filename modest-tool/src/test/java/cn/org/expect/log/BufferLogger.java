package cn.org.expect.log;

import cn.org.expect.util.ErrorUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class BufferLogger implements Logger {

    private StringBuilder buf = new StringBuilder();

    public boolean isTraceEnabled() {
        return true;
    }

    public boolean isDebugEnabled() {
        return true;
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

    public void trace(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    public void debug(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    public void info(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    public void warn(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    public void error(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    public void fatal(Object message, Object... args) {
        buf.append(this.toString(message, args));
    }

    /**
     * 转为字符串
     *
     * @param message 日志信息
     * @param args    参数数组
     * @return 字符串
     */
    public String toString(Object message, Object[] args) {
        String key = message.toString();
        Throwable cause = ErrorUtils.getThrowable(args);

        String text = StringUtils.replaceEmptyHolder(key, args);
        if (cause == null) {
            return text + FileUtils.LINE_SEPARATOR_UNIX;
        } else {
            return StringUtils.joinLineSeparator(text, ErrorUtils.toString(cause)) + FileUtils.LINE_SEPARATOR_UNIX;
        }
    }

    public String toString() {
        return buf.toString();
    }
}
