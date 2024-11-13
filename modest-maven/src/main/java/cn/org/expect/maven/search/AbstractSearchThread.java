package cn.org.expect.maven.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.util.Ensure;

public abstract class AbstractSearchThread<T> extends Thread {
    protected final static Log log = LogFactory.getLog(AbstractSearchThread.class);

    /** Maven仓库 */
    private MavenRepository mavenRepository;

    /** 线程任务的中断标志 */
    protected volatile boolean notTerminate;

    /** 远程调用组件 */
    protected final BlockingQueue<T> queue;

    /** 正在搜索的任务 */
    protected volatile T searching;

    /**
     * 构造方法
     */
    public AbstractSearchThread() {
        this.queue = new LinkedTransferQueue<>();
        this.notTerminate = true;
        this.setName(this.getClass().getSimpleName());
    }

    /**
     * 设置 Maven 仓库
     *
     * @param repository Maven仓库
     */
    public void setRepository(MavenRepository repository) {
        this.mavenRepository = Ensure.notNull(repository);
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
