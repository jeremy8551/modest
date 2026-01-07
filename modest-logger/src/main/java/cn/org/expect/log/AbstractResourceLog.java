package cn.org.expect.log;

import cn.org.expect.message.ResourceMessageBundle;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ErrorUtils;
import cn.org.expect.util.StringUtils;

public abstract class AbstractResourceLog extends AbstractLog {

    /** 日志上下文信息 */
    protected LogContext context;

    /** 国际化资源 */
    protected ResourceMessageBundle resourceBundle;

    public AbstractResourceLog(LogContext context, Class<?> type) {
        this.context = Ensure.notNull(context);
        this.type = Ensure.notNull(type);
        this.resourceBundle = Ensure.notNull(context.getResourceBundle());
    }

    /**
     * 判断是否是国际化信息
     *
     * @param key 资源编号
     * @return 返回true表示是国际化信息，false表示不是
     */
    public boolean isResourceBundle(String key) {
        return this.resourceBundle.contains(key);
    }

    /**
     * 返回国际化资源信息
     *
     * @param key  资源编号
     * @param args 参数数组
     * @return 国际化信息
     */
    public String getResourceBundle(String key, Object... args) {
        String value = this.resourceBundle.get(key);
        return StringUtils.replacePlaceHolder(value, args);
    }

    public void trace(Object message, Object... args) {
        if (this.isTraceEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.trace(text, cause);
        }
    }

    public void debug(Object message, Object... args) {
        if (this.isDebugEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.debug(text, cause);
        }
    }

    public void info(Object message, Object... args) {
        if (this.isInfoEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.info(text, cause);
        }
    }

    public void warn(Object message, Object... args) {
        if (this.isWarnEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.warn(text, cause);
        }
    }

    public void error(Object message, Object... args) {
        if (this.isErrorEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.error(text, cause);
        }
    }

    public void fatal(Object message, Object... args) {
        if (this.isFatalEnabled()) {
            String key = StringUtils.toString(message);
            String text;
            if (this.isResourceBundle(key)) {
                text = this.getResourceBundle(key, args);
            } else {
                text = StringUtils.replaceEmptyHolder(key, args);
            }

            Throwable cause = ErrorUtils.getThrowable(args);
            this.fatal(text, cause);
        }
    }

    /**
     * 输出跟踪信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void trace(String message, Throwable cause);

    /**
     * 输出调试信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void debug(String message, Throwable cause);

    /**
     * 输出一般信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void info(String message, Throwable cause);

    /**
     * 输出警告信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void warn(String message, Throwable cause);

    /**
     * 输出错误信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void error(String message, Throwable cause);

    /**
     * 输出严重错误信息
     *
     * @param message 字符串
     * @param cause   异常信息
     */
    public abstract void fatal(String message, Throwable cause);
}
