package cn.org.expect.concurrent.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.org.expect.concurrent.ExecutorServiceFactory;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/28
 */
public class DefaultServiceFactory implements ExecutorServiceFactory {

    public DefaultServiceFactory() {
    }

    public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}
