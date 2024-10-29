package cn.org.expect.intellijidea.plugin.maven.search;

import java.util.concurrent.LinkedBlockingQueue;

import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.util.Ensure;
import com.intellij.openapi.diagnostic.Logger;

public class MavenRepositorySearch<T> extends Thread {
    protected static final Logger log = Logger.getInstance(MavenRepositorySearch.class);

    /** Maven仓库 */
    private MavenRepository mavenRepository;

    /** 线程任务的中断标志 */
    protected volatile boolean notTerminate;

    /** 远程调用组件 */
    protected final LinkedBlockingQueue<T> queue;

    /**
     * 构造方法
     */
    public MavenRepositorySearch() {
        this.queue = new LinkedBlockingQueue<T>(10);
        this.notTerminate = true;
    }

    /**
     * 设置 Maven 仓库
     *
     * @param mavenRepository Maven仓库
     */
    public void setMavenRepository(MavenRepository mavenRepository) {
        this.mavenRepository = Ensure.notNull(mavenRepository);
    }

    /**
     * 终止线程任务
     */
    public void terminate() {
        this.notTerminate = false;
    }

    /**
     * 返回 Maven 仓库接口
     *
     * @return Maven 仓库接口
     */
    public MavenRepository getRepository() {
        return mavenRepository;
    }
}
