package cn.org.expect.log;

import java.io.IOException;

import cn.org.expect.log.apd.LogEvent;

/**
 * 日志记录器
 */
public interface Appender {

    /**
     * 将当前日志记录器安装到日志模块中
     *
     * @param context 日志模块的上下文信息
     * @return 日志记录器
     */
    Appender setup(LogContext context);

    /**
     * 日志记录器名称
     *
     * @return 字符串
     */
    String getName();

    /**
     * 输出跟踪信息
     *
     * @param event 记录日志事件
     */
    void append(LogEvent event);

    /**
     * 释放资源
     */
    void close() throws IOException;
}
