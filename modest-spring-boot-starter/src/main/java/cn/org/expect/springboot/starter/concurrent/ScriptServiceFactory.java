package cn.org.expect.springboot.starter.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import cn.org.expect.concurrent.ExecutorServiceFactory;
import cn.org.expect.util.Ensure;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池工厂的实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/12/6
 */
public class ScriptServiceFactory implements ExecutorServiceFactory {

    /** 线程池 */
    private final ExecutorService service;

    /**
     * 线程池工厂
     *
     * @param service 线程池
     */
    public ScriptServiceFactory(ExecutorService service) {
        this.service = Ensure.notNull(service);
    }

    /**
     * 线程池工厂
     *
     * @param service Springboot线程池
     */
    public ScriptServiceFactory(ThreadPoolTaskExecutor service) {
        this.service = Ensure.notNull(service).getThreadPoolExecutor();
    }

    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return this.service;
    }
}
