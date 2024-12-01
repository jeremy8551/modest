package cn.org.expect.maven.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.ExecutorServiceFactory;

@EasyBean
public class ExecutorServiceFactoryImpl implements ExecutorServiceFactory {

    private final MavenSearchExecutorService service;

    public ExecutorServiceFactoryImpl(MavenSearchExecutorService service) {
        this.service = service;
    }

    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return this.service;
    }
}
