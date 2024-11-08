package cn.org.expect.maven.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.util.Ensure;
import com.intellij.openapi.diagnostic.Logger;

public abstract class AbstractSearchThread<T> extends Thread {
    protected static final Logger log = Logger.getInstance(AbstractSearchThread.class);

    /** Maven仓库 */
    private MavenRepository mavenRepository;

    /** 线程任务的中断标志 */
    protected volatile boolean notTerminate;

    /** 远程调用组件 */
    protected final BlockingQueue<T> queue;

    /** 正在搜索的任务 */
    protected volatile ExtraElement searching;

    /**
     * 构造方法
     */
    public AbstractSearchThread() {
        this.queue = new LinkedTransferQueue<>();
        this.notTerminate = true;
    }

    /**
     * 设置 Maven 仓库
     *
     * @param mavenRepository Maven仓库
     */
    public void setRepository(MavenRepository mavenRepository) {
        this.mavenRepository = Ensure.notNull(mavenRepository);
    }

    /**
     * 返回 Maven 仓库接口
     *
     * @return Maven仓库
     */
    public MavenRepository getRepository() {
        return mavenRepository;
    }

    /**
     * 终止线程任务
     */
    public void terminate() {
        this.notTerminate = false;
    }
}
