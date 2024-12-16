package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.BaseJob;
import cn.org.expect.concurrent.Terminate;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.concurrent.MavenExecutorService;
import cn.org.expect.maven.concurrent.MavenJob;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.EdtExecutorService;
import org.jetbrains.annotations.NotNull;

@EasyBean(singleton = true)
public class MavenPluginExecutorService implements MavenExecutorService {
    private final static Log log = LogFactory.getLog(MavenPluginExecutorService.class);

    /** SearchEverywhereUI 线程池标志 */
    public final static String PARAMETER = "Alarm";

    /** 公用 SearchEverywhereUI 使用的线程池，防止多线程并发修改 JList 中的数据 */
    private volatile Alarm service;

    /** 还未执行完毕的任务集合 */
    private final List<Runnable> list;

    public MavenPluginExecutorService() {
        this.list = new Vector<>();
    }

    public void setParameter(String name, Object value) {
        if (PARAMETER.equals(name)) {
            this.service = (Alarm) value;
        }
    }

    public <T> T getFirst(Class<T> type, Predicate<T> condition) {
        for (int i = 0; i < this.list.size(); i++) {
            Runnable command = this.list.get(i);
            if (type.isAssignableFrom(command.getClass()) && condition.test((T) command)) {
                return (T) command;
            }
        }
        return null;
    }

    public <T> boolean isRunning(Class<T> type, Predicate<T> condition) {
        for (int i = 0; i < this.list.size(); i++) {
            Runnable command = this.list.get(i);
            if (type.isAssignableFrom(command.getClass()) && condition.test((T) command)) {
                return true;
            }
        }
        return false;
    }

    public <T> void terminate(Class<T> type, Predicate<T> condition) {
        for (int i = 0; i < this.list.size(); i++) {
            Runnable command = this.list.get(i);
            if (type.isAssignableFrom(command.getClass()) && condition.test((T) command)) {
                if (command instanceof Terminate) {
                    ((Terminate) command).terminate();
                }
            }
        }
    }

    public void removeJob(Object command) {
        this.list.remove(command);
    }

    public void addJob(Object command) {
        if (command instanceof MavenJob) {
            MavenJob job = (MavenJob) command;
            job.setService(this);
            this.list.add(job);
        }
    }

    public void execute(@NotNull Runnable command) {
        this.addJob(command);

        // 任务名
        String taskName;
        if (command instanceof BaseJob) {
            taskName = ((BaseJob) command).getName();
        } else {
            taskName = command.getClass().getName();
        }

        if (command instanceof EDTJob) {
            if (this.service != null && !this.service.isDisposed()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} execute {} ..", this.service.getClass().getSimpleName(), taskName);
                }
                this.service.addRequest(command, 0);
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("{} execute {} ..", EdtExecutorService.class.getSimpleName(), taskName);
            }

            EdtExecutorService.getInstance().execute(command);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("{} run {} ..", ApplicationManager.class.getSimpleName(), taskName);
        }

        ApplicationManager.getApplication().executeOnPooledThread(command);
    }

    public @NotNull <T> Future<T> submit(@NotNull Callable<T> command) {
        this.addJob(command);

        if (command instanceof EDTJob) {
            if (log.isDebugEnabled()) {
                log.debug("{} submit {} ..", EdtExecutorService.class.getSimpleName(), command.getClass().getName());
            }
            return EdtExecutorService.getInstance().submit(command);
        }

        if (log.isDebugEnabled()) {
            log.debug("{} submit {} ..", ApplicationManager.class.getSimpleName(), command.getClass().getName());
        }
        return ApplicationManager.getApplication().executeOnPooledThread(command);
    }

    public @NotNull Future<?> submit(@NotNull Runnable command) {
        this.addJob(command);

        if (command instanceof EDTJob) {
            if (log.isDebugEnabled()) {
                log.debug("{} submit {} ..", EdtExecutorService.class.getSimpleName(), command.getClass().getName());
            }
            return EdtExecutorService.getInstance().submit(command);
        }

        if (log.isDebugEnabled()) {
            log.debug("{} submit {} ..", ApplicationManager.class.getSimpleName(), command.getClass().getName());
        }
        return ApplicationManager.getApplication().executeOnPooledThread(command);
    }

    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.stream().map(command -> this.submit(command)).toList();
    }

    public @NotNull <T> Future<T> submit(@NotNull Runnable task, T result) {
        throw new UnsupportedOperationException();
    }

    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public @NotNull <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    public void shutdown() {
    }

    public @NotNull List<Runnable> shutdownNow() {
        return List.of();
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }
}
