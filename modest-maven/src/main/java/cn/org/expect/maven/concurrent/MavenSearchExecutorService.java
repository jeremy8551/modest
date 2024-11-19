package cn.org.expect.maven.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

public interface MavenSearchExecutorService extends ExecutorService {

    /**
     * 设置线程池
     *
     * @param service 线程池
     */
    void setSearchService(Object service);

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
     * 终止正在运行的任务
     *
     * @param cls       任务的 Class 信息
     * @param condition 判断任务运行的规则
     * @param <T>       任务类型
     */
    <T> void terminate(Class<T> cls, Predicate<T> condition);

    /**
     * 如果任务还没有执行，则删除任务
     *
     * @param runnable 任务
     */
    void removeJob(Object runnable);
}
