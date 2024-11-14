package cn.org.expect.maven.intellij.idea.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.EDTJob;
import cn.org.expect.util.Ensure;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.Alarm;
import com.intellij.util.concurrency.EdtExecutorService;
import org.jetbrains.annotations.NotNull;

@EasyBean(singleton = true)
public class MavenSearchExecutorServiceImpl implements MavenSearchExecutorService {
    private final static Log log = LogFactory.getLog(MavenSearchExecutorServiceImpl.class);

    private volatile Alarm service;

    public MavenSearchExecutorServiceImpl() {
    }

    @Override
    public void setSearchEverywhereService(Alarm service) {
        this.service = Ensure.notNull(service);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        if (command instanceof EDTJob) {
            if (this.service != null && !this.service.isDisposed()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} run {} ..", this.service.getClass().getSimpleName(), command.getClass().getName());
                }
                this.service.addRequest(command, 0);
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("{} run {} ..", EdtExecutorService.class.getSimpleName(), command.getClass().getName());
            }
            EdtExecutorService.getInstance().execute(command);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("{} run {} ..", ApplicationManager.class.getSimpleName(), command.getClass().getName());
        }
        ApplicationManager.getApplication().executeOnPooledThread(command);
    }

    @Override
    public @NotNull <T> Future<T> submit(@NotNull Callable<T> command) {
        if (this.service != null && command instanceof EDTJob) {
            return EdtExecutorService.getInstance().submit(command);
        } else {
            return ApplicationManager.getApplication().executeOnPooledThread(command);
        }
    }

    @Override
    public @NotNull Future<?> submit(@NotNull Runnable command) {
        if (this.service != null && command instanceof EDTJob) {
            return EdtExecutorService.getInstance().submit(command);
        } else {
            return ApplicationManager.getApplication().executeOnPooledThread(command);
        }
    }

    @Override
    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.stream().map(command -> {
            if (this.service != null && command instanceof EDTJob) {
                return EdtExecutorService.getInstance().submit(command);
            } else {
                return ApplicationManager.getApplication().executeOnPooledThread(command);
            }
        }).toList();
    }

    @Override
    public @NotNull <T> Future<T> submit(@NotNull Runnable task, T result) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @NotNull <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public @NotNull List<Runnable> shutdownNow() {
        return List.of();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }
}
