package cn.org.expect.maven.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.maven.concurrent.ArtifactSearchExecutorService;

@EasyBean
public class SimpleExecutorServiceFactory implements ExecutorServiceFactory {

    private final ArtifactSearchExecutorService service;

    public SimpleExecutorServiceFactory(ArtifactSearchExecutorService service) {
        this.service = service;
    }

    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return this.service;
    }
}
