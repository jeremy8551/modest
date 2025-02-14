package cn.org.expect.log;

import java.util.concurrent.Executor;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/12/1
 */
public class ExecutorImpl implements Executor {

    protected Thread thread;

    protected static volatile int NUMBER = 0;

    public ExecutorImpl() {
        this(null);
    }

    public ExecutorImpl(Thread thread) {
        this.thread = thread;
    }

    public void execute(Runnable command) {
        if (this.thread == null) {
            this.thread = new Thread(command);
            this.thread.setName(ExecutorImpl.class.getSimpleName() + "-thread-" + NUMBER++);
        }
        this.thread.start();
    }

    public Thread getThread() {
        return thread;
    }
}
