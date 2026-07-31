package cn.org.expect.os.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.org.expect.util.ShutdownHook;

/**
 * 管理操作系统命令使用的共享异步执行器
 */
public final class OSCommandExecutors {

    /** 适合阻塞式流读取任务的共享线程池 */
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    static {
        ShutdownHook.register(new Callable<String>() {
            public String call() {
                EXECUTOR.shutdownNow();
                return "";
            }
        });
    }

    /**
     * 禁止实例化工具类
     */
    private OSCommandExecutors() {
    }

    /**
     * 返回由进程生命周期统一管理的异步执行器
     *
     * @return 异步执行器
     */
    public static ExecutorService getExecutorService() {
        return EXECUTOR;
    }
}
