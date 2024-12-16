package cn.org.expect.maven.concurrent;

import java.util.function.Predicate;

import cn.org.expect.maven.MavenRuntimeException;
import cn.org.expect.util.Dates;

/**
 * 线程池
 */
public interface MavenService {

    /**
     * 设置参数
     *
     * @param name  参数名
     * @param value 参数值
     */
    void setParameter(String name, Object value);

    /**
     * 返回第一个匹配的任务
     *
     * @param type      任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     * @return 任务
     */
    <T> T getFirst(Class<T> type, Predicate<T> condition);

    /**
     * 判断是否正在运行某个任务
     *
     * @param type      任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     * @return 返回true表示正在运行
     */
    <T> boolean isRunning(Class<T> type, Predicate<T> condition);

    /**
     * 判断是否正在运行某个任务
     *
     * @param type 任务的 Class 信息
     * @param <T>  任务类型
     * @return 返回true表示正在运行
     */
    default <T> boolean isRunning(Class<T> type) {
        return this.isRunning(type, job -> true);
    }

    /**
     * 等待任务执行完毕
     *
     * @param type      任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param timeout   超时时间（单位：毫秒），小于等于零表示不设置超时时间
     */
    default <T> void waitFor(Class<T> type, Predicate<T> condition, long timeout) {
        Throwable e = Dates.waitFor(() -> this.isRunning(type, condition), 200, timeout);
        if (e != null) {
            throw new MavenRuntimeException(e, e.getLocalizedMessage());
        }
    }

    /**
     * 终止正在运行的任务
     *
     * @param type      任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     */
    <T> void terminate(Class<T> type, Predicate<T> condition);

    /**
     * 删除任务
     *
     * @param command 任务
     */
    void removeJob(Object command);
}
