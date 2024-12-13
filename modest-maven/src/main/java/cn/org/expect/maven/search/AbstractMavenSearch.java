package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.maven.ArtifactOption;
import cn.org.expect.maven.concurrent.ArtifactSearchExecutorService;
import cn.org.expect.maven.concurrent.ArtifactSearchInputJob;
import cn.org.expect.maven.impl.SimpleArtifactOption;
import cn.org.expect.maven.ioc.MavenSearchIoc;
import cn.org.expect.maven.pom.PomInfoRepository;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;

public abstract class AbstractMavenSearch implements ArtifactSearch {

    /** 容器上下文信息 */
    private final MavenSearchIoc ioc;

    /** 仓库接口 */
    private ArtifactRepository repository;

    /** 选择的仓库 */
    private ArtifactOption selectRepository;

    /** 本地Maven仓库接口 */
    private volatile LocalRepository localRepository;

    /** 文本处理器 */
    private final ArtifactSearchPattern pattern;

    public AbstractMavenSearch(MavenSearchIoc ioc) {
        this.pattern = new ArtifactSearchPattern();
        this.ioc = Ensure.notNull(ioc);
    }

    public ArtifactSearchPattern getPattern() {
        return this.pattern;
    }

    public void setRepository(String repositoryId) {
        this.repository = Ensure.notNull(this.ioc.getBean(ArtifactRepository.class, repositoryId), repositoryId);
        this.selectRepository = new SimpleArtifactOption(repositoryId);
    }

    /**
     * 返回 Ioc 容器
     *
     * @return 容器
     */
    public MavenSearchIoc getIoc() {
        return this.ioc;
    }

    public synchronized void execute(Runnable command) {
        this.ioc.getBean(ThreadSource.class).getExecutorService().execute(this.aware(command));
    }

    public ArtifactSearchExecutorService getService() {
        return this.ioc.getBean(ArtifactSearchExecutorService.class);
    }

    public ArtifactOption getRepositoryInfo() {
        return this.selectRepository;
    }

    public ArtifactRepository getRepository() {
        return this.repository;
    }

    public LocalRepository getLocalRepository() {
        if (this.localRepository == null) {
            synchronized (this) {
                if (this.localRepository == null) {
                    this.localRepository = this.ioc.getBean(LocalRepository.class);
                }
            }
        }
        return this.localRepository;
    }

    public LocalRepositorySettings getLocalRepositorySettings() {
        return this.localRepository.getSettings();
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public synchronized ArtifactSearchInputJob getInput() {
        ArtifactSearchInputJob job = this.getService().getFirst(ArtifactSearchInputJob.class, first -> true); // 只能是单例模式
        if (job == null) {
            job = new ArtifactSearchInputJob();
            this.getService().execute(job);

            // 等待任务启动
            Throwable e = Dates.waitFor(() -> this.getService().isRunning(ArtifactSearchInputJob.class, task -> !task.isRunning()), 100, 20 * 1000);
            if (e != null) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }
        return job;
    }

    public ArtifactRepositoryDatabase getDatabase() {
        return this.getRepository().getDatabase();
    }

    public PomInfoRepository getPomInfoRepository() {
        return this.ioc.getBean(PomInfoRepository.class);
    }
}
