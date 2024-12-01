package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.concurrent.MavenSearchInputJob;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalMavenRepository;
import cn.org.expect.maven.repository.local.LocalMavenRepositorySettings;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;

public abstract class AbstractMavenSearch implements ArtifactSearch {

    /** IOC 容器 */
    private final EasyContext ioc;

    /** Maven仓库 */
    private ArtifactRepository repository;

    /** 本地Maven仓库接口 */
    private final LocalMavenRepository localRepository;

    public AbstractMavenSearch(EasyContext ioc) {
        super();
        this.ioc = Ensure.notNull(ioc);
        this.localRepository = this.ioc.getBean(LocalMavenRepository.class);
        ArtifactSearchSettings settings = this.ioc.getBean(ArtifactSearchSettings.class);
        this.setRepositoryId(settings.getRepositoryId());
    }

    /**
     * 设置仓库ID
     *
     * @param id 仓库ID
     */
    public void setRepositoryId(String id) {
        this.repository = Ensure.notNull(this.ioc.getBean(ArtifactRepository.class, id), id);
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
        this.ioc.getBean(ThreadSource.class).getExecutorService().execute(this.aware(command));
    }

    public MavenSearchExecutorService getService() {
        return this.ioc.getBean(MavenSearchExecutorService.class);
    }

    public ArtifactRepository getRepository() {
        return this.repository;
    }

    public LocalMavenRepository getLocalRepository() {
        return this.localRepository;
    }

    public LocalMavenRepositorySettings getLocalRepositorySettings() {
        return this.localRepository.getSettings();
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public synchronized MavenSearchInputJob getInput() {
        MavenSearchInputJob job = this.getService().getFirst(MavenSearchInputJob.class, first -> true); // 只能是单例模式
        if (job == null) {
            job = new MavenSearchInputJob();
            this.getService().execute(job);

            // 等待任务启动
            Throwable e = Dates.waitFor(() -> this.getService().isRunning(MavenSearchInputJob.class, task -> !task.isRunning()), 100, 20 * 1000);
            if (e != null) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }
        return job;
    }

    /**
     * 返回数据库对象
     *
     * @return 数据库对象
     */
    public ArtifactRepositoryDatabase getDatabase() {
        return this.getRepository().getDatabase();
    }
}
