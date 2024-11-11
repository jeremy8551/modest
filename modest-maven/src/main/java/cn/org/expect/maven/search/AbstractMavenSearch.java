package cn.org.expect.maven.search;

import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.central.CentralRepository;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.maven.search.db.MavenSearchDatabase;
import cn.org.expect.maven.search.db.MavenSearchDatabaseImpl;

public abstract class AbstractMavenSearch implements MavenSearch {

    /** 远程 Maven 仓库接口 */
    private final MavenRepository remoteRepository;

    /** 本地 Maven 仓库接口 */
    private final LocalRepository localRepository;

    /** 用来搜索用户输入的文本 */
    private volatile static MavenSearchInputThread INPUT_SEARCH;

    /** 后台搜索线程 */
    private volatile static MavenSearchServiceThread SEARCH;

    /** 数据库 */
    private volatile static MavenSearchDatabaseImpl DATABASE;

    public AbstractMavenSearch(LocalRepositoryConfig config) {
        this.localRepository = new LocalRepository(config);
        this.remoteRepository = new CentralRepository();
    }

    /**
     * 返回 Maven 仓库信息
     *
     * @return Maven 仓库信息
     */
    public MavenRepository getRemoteRepository() {
        return remoteRepository;
    }

    /**
     * 返回本地 Maven 仓库信息
     *
     * @return 本地 Maven 仓库信息
     */
    public LocalRepository getLocalRepository() {
        return localRepository;
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public MavenSearchInputThread getInputSearch() {
        if (INPUT_SEARCH == null) {
            synchronized (MavenSearchInputThread.class) {
                if (INPUT_SEARCH == null) {
                    INPUT_SEARCH = new MavenSearchInputThread();
                    INPUT_SEARCH.setRepository(this.remoteRepository);
                    INPUT_SEARCH.setDaemon(true);
                    INPUT_SEARCH.setName(MavenSearchInputThread.class.getSimpleName());
                    INPUT_SEARCH.start();
                }
            }
        }
        return INPUT_SEARCH;
    }

    /**
     * 返回精确查询工具
     *
     * @return 精确查询工具
     */
    public MavenSearchServiceThread getServiceSearch() {
        if (SEARCH == null) {
            synchronized (MavenSearchServiceThread.class) {
                if (SEARCH == null) {
                    SEARCH = new MavenSearchServiceThread();
                    SEARCH.setRepository(this.remoteRepository);
                    SEARCH.setDaemon(true);
                    SEARCH.setName(MavenSearchServiceThread.class.getSimpleName());
                    SEARCH.start();
                }
            }
        }
        return SEARCH;
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
                    DATABASE = new MavenSearchDatabaseImpl(this.localRepository);
                }
            }
        }
        return DATABASE;
    }
}
