package cn.org.expect.maven.search;

import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.maven.search.db.MavenSearchDatabaseImpl;
import cn.org.expect.util.Ensure;

public abstract class AbstractMavenSearch implements MavenSearch {

    /** 插件的ID */
    private static String ID;

    /** 插件名 */
    private static String NAME;

    /** IOC 容器 */
    private static volatile EasyContext IOC;

    /** IOC容器 */
    private final String remoteRepositoryName;

    /** 本地 Maven 仓库接口 */
    private final LocalRepository localRepository;

    /** 数据库 */
    private volatile static MavenSearchDatabaseImpl DATABASE;

    public AbstractMavenSearch(String remoteRepositoryName, LocalRepositoryConfig config) {
        super();
        this.localRepository = new LocalRepository(config);
        this.remoteRepositoryName = remoteRepositoryName;
    }

    public static void setEasyContext(EasyContext ioc) {
        AbstractMavenSearch.IOC = Ensure.notNull(ioc);
    }

    public static EasyContext getEasyContext() {
        return AbstractMavenSearch.IOC;
    }

    public static void setID(String ID) {
        AbstractMavenSearch.ID = ID;
    }

    public static void setName(String NAME) {
        AbstractMavenSearch.NAME = NAME;
    }

    /**
     * 返回域名id
     *
     * @return 字符串
     */
    public String getGroupId() {
        return ID;
    }

    public String getName() {
        return NAME;
    }

    public synchronized void execute(Runnable command) {
        if (command instanceof MavenSearchAware) {
            ((MavenSearchAware) command).setSearch(this);
        }
        IOC.getBean(ThreadSource.class).getExecutorService().execute(command);
    }

    /**
     * 设置线程池
     *
     * @param service 线程池
     */
    public void setService(Object service) {
        IOC.getBean(MavenSearchExecutorService.class).setSearchService(service);
    }

    public MavenSearchExecutorService getService() {
        return IOC.getBean(MavenSearchExecutorService.class);
    }

    public MavenRepository getRemoteRepository() {
        return IOC.getBean(MavenRepository.class, this.remoteRepositoryName);
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
