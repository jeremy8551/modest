package cn.org.expect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 挂钩线程
 */
public class ShutdownHook extends Thread {

    private final static ShutdownHook instance = new ShutdownHook();

    /**
     * 注册挂钩线程任务
     *
     * @param task 任务
     */
    public synchronized static void register(Callable<String> task) {
        if (task != null) {
            if (instance.list.isEmpty()) {
                Runtime.getRuntime().addShutdownHook(ShutdownHook.instance);
            }
            instance.list.add(task);
        }
    }

    private final List<Callable<String>> list;

    private ShutdownHook() {
        super(ShutdownHook.class.getSimpleName());
        this.list = new ArrayList<Callable<String>>();
    }

    public void run() {
        for (int i = 0; i < this.list.size(); i++) {
            try {
                this.list.get(i).call();
            } catch (Throwable e) {
                Logs.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
