package cn.org.expect.maven.concurrent;

import java.util.function.Predicate;

import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchArtifactJob;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.MavenRuntimeException;
import cn.org.expect.util.Dates;

/**
 * 线程池
 */
public interface ArtifactSearchExecutorService {

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
     * 判断是否正在执行任务
     *
     * @param type     任务的Class信息
     * @param artifact 工件信息
     * @return 返回true表示正在运行 false表示没有运行
     */
    default boolean existsCommand(Class<?> type, Artifact artifact) {
        if (ArtifactSearchExtraJob.class.equals(type)) {
            return this.isRunning(ArtifactSearchExtraJob.class, job -> job.getGroupId().equals(artifact.getGroupId()) && job.getArtifactId().equals(artifact.getArtifactId()));
        } else if (MavenSearchArtifactJob.class.isAssignableFrom(type)) {
            return this.isRunning(type, job -> ((MavenSearchArtifactJob) job).getArtifact().equalMavenId(artifact));
        } else {
            throw new UnsupportedOperationException(type.getName());
        }
    }

    /**
     * 等待任务执行完毕
     *
     * @param type     任务的Class信息
     * @param artifact 工件信息
     */
    default void waitFor(Class<?> type, Artifact artifact) {
        Throwable e = Dates.waitFor(() -> this.existsCommand(type, artifact), 200, 10 * 1000);
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
