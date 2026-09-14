package cn.org.expect.log;

import cn.org.expect.util.Settings;

/**
 * 日志输出接口
 */
public interface Log {

    /** JVM参数名，日志级别，详见 {@linkplain LogLevel} */
    String PROPERTY_LOGGER = Settings.getPropertyName("log");

    /**
     * 返回日志所属类的名字
     *
     * @return 字符串
     */
    String getName();

    /**
     * 判断日志级别是否是跟踪模式
     *
     * @return 是Trace这个级别
     */
    boolean isTraceEnabled();

    /**
     * 判断日志级别是否是调试模式
     *
     * @return 是Debug这个级别
     */
    boolean isDebugEnabled();

    /**
     * 判断日志级别是否是正常模式
     *
     * @return 是Info这个级别
     */
    boolean isInfoEnabled();

    /**
     * 判断日志级别是否是警告模式
     *
     * @return 是Warn这个级别
     */
    boolean isWarnEnabled();

    /**
     * 判断日志级别是否是错误模式
     *
     * @return 是Error这个级别
     */
    boolean isErrorEnabled();

    /**
     * 判断日志级别是否是严重级别
     *
     * @return 是Fatal这个级别
     */
    boolean isFatalEnabled();

    /**
     * 输出跟踪信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void trace(Object message, Object... args);

    /**
     * 输出调试信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void debug(Object message, Object... args);

    /**
     * 输出一般信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void info(Object message, Object... args);

    /**
     * 输出警告信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void warn(Object message, Object... args);

    /**
     * 输出错误信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void error(Object message, Object... args);

    /**
     * 输出严重错误信息
     *
     * @param message 字符串，可以是国际化资源信息的编号
     * @param args    参数数组，异常信息必须在最右侧
     */
    void fatal(Object message, Object... args);
}
