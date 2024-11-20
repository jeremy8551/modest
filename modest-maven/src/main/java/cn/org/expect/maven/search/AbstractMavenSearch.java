package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;

public abstract class AbstractMavenSearch implements MavenSearch {

    /** IOC 容器 */
    private final EasyContext ioc;

    /** Maven 仓库 */
    private final MavenRepository repository;

    /** 本地 Maven 仓库接口 */
    private final LocalRepository localRepository;

    public AbstractMavenSearch(String remoteRepositoryName) {
        super();
        this.ioc = DefaultEasyContext.getInstance();
        this.localRepository = this.ioc.getBean(LocalRepository.class);
        this.repository = this.ioc.getBean(MavenRepository.class, remoteRepositoryName);
    }

    /**
     * 返回 Ioc 容器
     *
     * @return 容器
     */
    public EasyContext getEasyContext() {
        return this.ioc;
    }

    public synchronized void execute(Runnable command) {
        if (command instanceof MavenSearchAware) {
            ((MavenSearchAware) command).setSearch(this);
        }
        this.ioc.getBean(ThreadSource.class).getExecutorService().execute(command);
    }

    public MavenSearchExecutorService getService() {
        return this.ioc.getBean(MavenSearchExecutorService.class);
    }

    public MavenRepository getRepository() {
        return this.repository;
    }

    public LocalRepository getLocalRepository() {
        return this.localRepository;
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public synchronized MavenSearchInputJob getInput() {
        MavenSearchInputJob thread = this.getService().getFirst(MavenSearchInputJob.class, job -> true); // MavenSearchInputJob 对象只能是单例模式
        if (thread == null) {
            thread = new MavenSearchInputJob();
            this.getService().execute(thread);
        }
        return thread;
    }

    /**
     * 返回数据库对象
     *
     * @return 数据库对象
     */
    public MavenRepositoryDatabase getDatabase() {
        return this.getRepository().getDatabase();
    }
}
