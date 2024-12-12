package cn.org.expect.maven.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

public interface ArtifactSearchExecutorService extends ExecutorService {

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
     * @param cls       任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     * @return 任务
     */
    <T> T getFirst(Class<T> cls, Predicate<T> condition);

    /**
     * 判断是否正在运行某个任务
     *
     * @param cls       任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     * @return 返回true表示正在运行
     */
    <T> boolean isRunning(Class<T> cls, Predicate<T> condition);

    /**
     * 判断是否正在运行某个任务
     *
     * @param cls 任务的 Class 信息
     * @param <T> 任务类型
     * @return 返回true表示正在运行
     */
    default <T> boolean isRunning(Class<T> cls) {
        return this.isRunning(cls, job -> true);
    }

    /**
     * 终止正在运行的任务
     *
     * @param cls       任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     */
    <T> void terminate(Class<T> cls, Predicate<T> condition);

    /**
     * 删除任务
     *
     * @param runnable 任务
     */
    void removeJob(Object runnable);
}
