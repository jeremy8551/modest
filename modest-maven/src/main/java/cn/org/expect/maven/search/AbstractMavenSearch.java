package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.maven.ArtifactOption;
import cn.org.expect.maven.ArtifactSearchIoc;
import cn.org.expect.maven.MavenRuntimeException;
import cn.org.expect.maven.concurrent.ArtifactSearchExecutorService;
import cn.org.expect.maven.concurrent.ArtifactSearchInputJob;
import cn.org.expect.maven.impl.SimpleArtifactOption;
import cn.org.expect.maven.pom.PomRepository;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;

public abstract class AbstractMavenSearch implements ArtifactSearch {

    /** 容器上下文信息 */
    private final ArtifactSearchIoc ioc;

    /** 仓库接口 */
    private ArtifactRepository repository;

    /** 选择的仓库 */
    private ArtifactOption selectRepository;

    /** 本地Maven仓库接口 */
    private volatile LocalRepository localRepository;

    /** 文本处理器 */
    private final ArtifactSearchPattern pattern;

    public AbstractMavenSearch(ArtifactSearchIoc ioc) {
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

    public ArtifactSearchIoc getIoc() {
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
            this.execute(job);

            // 等待任务启动
            Throwable e = Dates.waitFor(() -> this.getService().isRunning(ArtifactSearchInputJob.class, command -> !command.isRunning()), 100, 20 * 1000);
            if (e != null) {
                throw new MavenRuntimeException(e, e.getLocalizedMessage());
            }
        }
        return job;
    }

    public ArtifactRepositoryDatabase getDatabase() {
        return this.getRepository().getDatabase();
    }

    public PomRepository getPomRepository() {
        return this.ioc.getBean(PomRepository.class);
    }
}
