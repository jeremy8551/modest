package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.intellij.idea.concurrent.MavenSearchPluginService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.maven.search.db.MavenSearchDatabaseImpl;
import cn.org.expect.util.Ensure;
import com.intellij.util.Alarm;

public abstract class AbstractMavenSearch implements MavenSearch {

    /** IOC 容器 */
    private final EasyContext ioc;

    /** IOC容器 */
    private final String remoteRepositoryName;

    /** 本地 Maven 仓库接口 */
    private final LocalRepository localRepository;

    /** 数据库 */
    private volatile static MavenSearchDatabaseImpl DATABASE;

    public AbstractMavenSearch(EasyContext ioc, String remoteRepositoryName, LocalRepositoryConfig config) {
        super();
        this.ioc = Ensure.notNull(ioc);
        this.localRepository = new LocalRepository(config);
        this.remoteRepositoryName = remoteRepositoryName;
    }

    public synchronized void execute(Runnable command) {
        if (command instanceof MavenSearchAware) {
            ((MavenSearchAware) command).setSearch(this);
        }
        this.ioc.getBean(ThreadSource.class).getExecutorService().execute(command);
    }

    public void setService(Alarm alarm) {
        this.ioc.getBean(MavenSearchPluginService.class).setSearchEverywhereService(alarm);
    }

    public MavenSearchPluginService getService() {
        return this.ioc.getBean(MavenSearchPluginService.class);
    }

    public MavenRepository getRemoteRepository() {
        return this.ioc.getBean(MavenRepository.class, this.remoteRepositoryName);
    }

    public LocalRepository getLocalRepository() {
        return localRepository;
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public synchronized MavenSearchInputJob getInput() {
        MavenSearchInputJob thread = this.getService().getFirst(MavenSearchInputJob.class, job -> true);
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
    public MavenSearchDatabase getDatabase() {
        if (DATABASE == null) {
            synchronized (MavenSearchDatabaseImpl.class) {
                if (DATABASE == null) {
                    DATABASE = new MavenSearchDatabaseImpl(this);
                }
            }
        }
        return DATABASE;
    }
}
