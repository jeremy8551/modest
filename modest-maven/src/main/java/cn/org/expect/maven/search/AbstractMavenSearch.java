package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.maven.MavenOption;
import cn.org.expect.maven.MavenEasyContext;
import cn.org.expect.maven.concurrent.SearchInputJob;
import cn.org.expect.maven.concurrent.MavenService;
import cn.org.expect.maven.impl.SimpleArtifactOption;
import cn.org.expect.maven.pom.PomRepository;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;
import cn.org.expect.util.Ensure;

public abstract class AbstractMavenSearch implements ArtifactSearch {

    /** 容器上下文信息 */
    private final MavenEasyContext ioc;

    /** 仓库接口 */
    private ArtifactRepository repository;

    /** 选择的仓库 */
    private MavenOption selectRepository;

    /** 本地Maven仓库接口 */
    private volatile LocalRepository localRepository;

    /** 文本处理器 */
    private final ArtifactSearchPattern pattern;

    public AbstractMavenSearch(MavenEasyContext ioc) {
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

    public MavenEasyContext getIoc() {
        return this.ioc;
    }

    public synchronized void execute(Runnable command) {
        this.ioc.getBean(ThreadSource.class).getExecutorService().execute(this.aware(command));
    }

    public MavenService getService() {
        return this.ioc.getBean(MavenService.class);
    }

    public MavenOption getRepositoryInfo() {
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
    public synchronized SearchInputJob getInput() {
        SearchInputJob job = this.getService().getFirst(SearchInputJob.class, first -> true); // 只能是单例模式
        if (job == null) {
            job = new SearchInputJob();
            this.execute(job);

            // 等待任务启动
            this.getService().waitFor(SearchInputJob.class, command -> !command.isRunning(), 10 * 1000);
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
