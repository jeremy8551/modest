package cn.org.expect.maven.concurrent.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.maven.concurrent.MavenExecutorService;
import cn.org.expect.util.Ensure;

@EasyBean
public class SimpleExecutorServiceFactory implements ExecutorServiceFactory {

    private final MavenExecutorService service;

    public SimpleExecutorServiceFactory(MavenExecutorService service) {
        this.service = Ensure.notNull(service);
    }

    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return this.service;
    }
}
