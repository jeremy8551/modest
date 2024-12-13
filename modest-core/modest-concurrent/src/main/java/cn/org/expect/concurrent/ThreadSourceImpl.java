package cn.org.expect.concurrent;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 接口实现类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/28
 */
public class ThreadSourceImpl implements ThreadSource, Closeable {

    /** 并发任务运行容器的序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator("JobService{}", 1);
    
    /** 外部线程池工厂 */
    private volatile ExecutorServiceFactory externalFactory;

    /** 默认线程池工厂 */
    private volatile ExecutorServiceFactory factory;

    /** 线程池 */
    private volatile ExecutorService service;

    /** 线程工厂 */
    private ThreadFactory threadFactory;

    /** 线程池拒绝策略 */
    private RejectedExecutionHandler executionHandler;

    /** 核心线程数 */
    private int coreSize;

    /** 最大值 */
    private int maxSize;

    /** 闲置线程的空闲时间 */
    private long aliveTime;

    /** 队列容量 */
    private int queueSize;

    public ThreadSourceImpl() {
        this.coreSize = 12;
        this.maxSize = 40;
        this.aliveTime = 10 * 1000; // 10 second
        this.queueSize = 40;
        this.executionHandler = new ThreadPoolExecutor.AbortPolicy();

        // 注册挂钩线程
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(this)));
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public void setExecutorsFactory(ExecutorServiceFactory factory) {
        this.externalFactory = factory;
    }

    public ExecutorServiceFactory getExecutorsFactory() {
        if (this.externalFactory != null) { // 优先使用外部线程池
            return this.externalFactory;
        }

        if (this.factory == null) {
            synchronized (this) {
                if (this.factory == null) {
                    this.factory = new ExecutorsFactoryImpl();
                }
            }
        }
        return factory;
    }

    public ExecutorService getExecutorService() {
        if (this.service == null) {
            synchronized (this) {
                if (this.service == null) {
                    this.service = this.getExecutorsFactory().create( //
                            this.coreSize //
                            , this.maxSize //
                            , this.aliveTime //
                            , TimeUnit.MILLISECONDS //
                            , new LinkedBlockingQueue<Runnable>(this.queueSize) //
                            , this.threadFactory == null ? Executors.defaultThreadFactory() : this.threadFactory //
                            , this.executionHandler);
                }
            }
        }
        return this.service;
    }

    public EasyJobService getJobService(int n) {
        Ensure.fromOne(n);
        String id = UNIQUE.nextString();
        ExecutorService service = this.getExecutorService();
        return new EasyJobServiceImpl(id, service, n);
    }

    public void execute(EasyScheduleJob job) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        this.externalFactory = null;
        this.factory = null;
        this.threadFactory = null;

        // 关闭线程池
        if (this.service != null) {
            this.service.shutdown();
            this.service = null;
        }
    }

    /**
     * 挂钩线程任务：关闭线程池
     */
    private static class ShutdownHook implements Runnable {

        private ThreadSourceImpl source;

        public ShutdownHook(ThreadSourceImpl source) {
            this.source = Ensure.notNull(source);
        }

        public void run() {
            this.source.close();
        }
    }
}
