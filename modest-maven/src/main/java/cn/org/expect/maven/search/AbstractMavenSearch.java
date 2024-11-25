package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginApplication;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;

public abstract class AbstractMavenSearch implements MavenSearch {

    private final MavenSearchSettings settings;

    /** IOC 容器 */
    private final EasyContext ioc;

    /** Maven仓库ID */
    private volatile String repositoryId;

    /** Maven仓库 */
    private MavenRepository repository;

    /** 本地Maven仓库接口 */
    private final LocalRepository localRepository;

    public AbstractMavenSearch() {
        super();
        this.ioc = MavenSearchPluginApplication.get();
        this.settings = this.ioc.getBean(MavenSearchSettings.class);
        this.localRepository = this.ioc.getBean(LocalRepository.class);
        this.setRepositoryId(this.settings.getRepositoryId());
    }

    /**
     * 设置Maven仓库ID
     *
     * @param id Maven仓库ID
     */
    public void setRepositoryId(String id) {
        this.repository = this.ioc.getBean(MavenRepository.class, id);
        this.repositoryId = id;
    }

    /**
     * 返回Maven仓库ID
     *
     * @return Maven仓库ID
     */
    public String getRepositoryId() {
        return repositoryId;
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
        MavenSearchInputJob job = this.getService().getFirst(MavenSearchInputJob.class, first -> true); // MavenSearchInputJob 对象只能是单例模式
        if (job == null) {
            job = new MavenSearchInputJob();
            this.getService().execute(job);
        }
        return job;
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
