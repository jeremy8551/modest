package cn.org.expect.log;

import java.util.List;

import cn.org.expect.message.ResourceMessageBundle;

/**
 * 日志上下文信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/22
 */
public interface LogContext {

    /**
     * 重置所有日志级别与日志记录器，会保留国际化资源信息
     */
    void reset();

    /**
     * 设置日志输出级别
     *
     * @param name  包名或类名
     *              空白字符串 ：表示匹配所有日志 <br>
     *              root：表示匹配所有日志 <br>
     *              *：表示匹配所有日志 <br>
     * @param level 日志级别
     */
    void updateLevel(String name, LogLevel level);

    /**
     * 返回类的日志对应的日志级别
     *
     * @param type 类信息
     * @return 日志级别
     */
    LogLevel getLevel(Class<?> type);

    /**
     * 设置国际化资源接口
     *
     * @param resourceBundle 国际化资源接口
     */
    void setResourceBundle(ResourceMessageBundle resourceBundle);

    /**
     * 返回国际化资源接口
     *
     * @return 国际化资源接口
     */
    ResourceMessageBundle getResourceBundle();

    /**
     * 返回应用的启动时间戳
     *
     * @param millis 时间戳
     */
    void setStartMillis(long millis);

    /**
     * 返回应用的启动时间戳
     *
     * @return 时间戳
     */
    long getStartMillis();

    /**
     * 添加日志
     *
     * @param log 日志接口
     */
    void addLog(Log log);

    /**
     * 设置日志工厂
     *
     * @param builder 日志工厂
     */
    void setBuilder(LogBuilder builder);

    /**
     * 返回日志工厂
     *
     * @return 日志工厂
     */
    LogBuilder getBuilder();

    /**
     * 查找指定类的日志记录器
     *
     * @param type 记录器的类信息
     * @return 返回 null 表示不存在
     */
    <E> E findAppender(Class<E> type);

    /**
     * 添加一个记录器
     *
     * @param appender 记录器
     */
    void addAppender(Appender appender);

    /**
     * 移除记录器
     *
     * @param where 移除条件，可以是：记录器对象、记录器名字、记录器的Class
     * @return 记录器集合
     */
    List<Appender> removeAppender(Object where);

    /**
     * 返回所有记录器集合
     *
     * @return 记录器集合
     */
    List<Appender> getAppenders();
}
