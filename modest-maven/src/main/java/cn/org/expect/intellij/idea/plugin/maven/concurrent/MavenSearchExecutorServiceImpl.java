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
import cn.org.expect.concurrent.Terminate;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.concurrent.EDTJob;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.concurrent.MavenSearchJob;
import cn.org.expect.util.Ensure;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.EdtExecutorService;
import org.jetbrains.annotations.NotNull;

@EasyBean(singleton = true)
public class MavenSearchExecutorServiceImpl implements MavenSearchExecutorService {
    private final static Log log = LogFactory.getLog(MavenSearchExecutorServiceImpl.class);

    private volatile Alarm service;

    private final List<Runnable> list;

    public MavenSearchExecutorServiceImpl() {
        this.list = new Vector<>();
    }

    public void setParameter(String name, Object value) {
        if (value instanceof Alarm) {
            this.service = Ensure.notNull((Alarm) value);
        }
    }

    public <T> T getFirst(Class<T> cls, Predicate<T> condition) {
        for (Runnable task : this.list) {
            if (cls.isAssignableFrom(task.getClass()) && condition.test((T) task)) {
                return (T) task;
            }
        }
        return null;
    }

    public <T> boolean isRunning(Class<T> cls, Predicate<T> condition) {
        for (Runnable task : this.list) {
            if (cls.isAssignableFrom(task.getClass()) && condition.test((T) task)) {
                return true;
            }
        }
        return false;
    }

    public <T> void terminate(Class<T> cls, Predicate<T> condition) {
        for (Runnable task : this.list) {
            if (cls.isAssignableFrom(task.getClass()) && condition.test((T) task)) {
                if (task instanceof Terminate) {
                    ((Terminate) task).terminate();
                }
            }
        }
    }

    public void removeJob(Object task) {
        this.list.remove(task);
    }

    public void addJob(Object command) {
        if (command instanceof MavenSearchJob) {
            MavenSearchJob job = (MavenSearchJob) command;
            job.setService(this);
            this.list.add(job);
        }
    }

    public void execute(@NotNull Runnable command) {
        this.addJob(command);

        if (command instanceof EDTJob) {
            if (this.service != null && !this.service.isDisposed()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} execute {} ..", this.service.getClass().getSimpleName(), command.getClass().getName());
                }
                this.service.addRequest(command, 0);
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("{} execute {} ..", EdtExecutorService.class.getSimpleName(), command.getClass().getName());
            }
            EdtExecutorService.getInstance().execute(command);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("{} run {} ..", ApplicationManager.class.getSimpleName(), command.getClass().getName());
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
        throw new UnsupportedOperationException(); // TODO
    }

    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException(); // TODO
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
