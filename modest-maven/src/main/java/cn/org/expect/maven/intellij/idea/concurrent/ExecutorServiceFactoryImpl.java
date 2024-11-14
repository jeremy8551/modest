package cn.org.expect.maven.intellij.idea.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.org.expect.concurrent.ExecutorServiceFactory;

public class ExecutorServiceFactoryImpl implements ExecutorServiceFactory {

    private MavenSearchExecutorService service;

    public ExecutorServiceFactoryImpl(MavenSearchExecutorService service) {
        this.service = service;
    }

    @Override
    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return this.service;
    }
}
