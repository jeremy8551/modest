package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenFinderContext;
import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.intellijidea.plugin.maven.central.CentralRepository;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.intellijidea.plugin.maven.local.LocalRepository;
import cn.org.expect.intellijidea.plugin.maven.local.LocalRepositoryConfig;
import org.jetbrains.annotations.NotNull;

public class AsyncDatabaseSearch {

    /** 远程调用组件 */
    private final MavenRepository mavenRepository;

    private final LocalRepository localMavenRepository;

    private volatile static MavenRepositoryInputSearch INPUT_SEARCH;

    private volatile static MavenRepositorySearch SEARCH;

    private volatile static MavenArtifactDatabase DATABASE;

    public AsyncDatabaseSearch(MavenFinderContext context) {
        LocalRepositoryConfig config = LocalRepositoryConfig.getInstance(context.getActionEvent());
        this.localMavenRepository = new LocalRepository(config);
        this.mavenRepository = new CentralRepository();
    }

    /**
     * 返回 Maven 仓库信息
     *
     * @return Maven 仓库信息
     */
    public MavenRepository getMavenRepository() {
        return mavenRepository;
    }

    /**
     * 返回本地 Maven 仓库信息
     *
     * @return 本地 Maven 仓库信息
     */
    public LocalRepository getLocalMavenRepository() {
        return localMavenRepository;
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public @NotNull MavenRepositoryInputSearch getInputSearch() {
        if (INPUT_SEARCH == null) {
            synchronized (MavenRepositoryInputSearch.class) {
                if (INPUT_SEARCH == null) {
                    INPUT_SEARCH = new MavenRepositoryInputSearch();
                    INPUT_SEARCH.setRepository(this.mavenRepository);
                    INPUT_SEARCH.setDaemon(true);
                    INPUT_SEARCH.setName(MavenRepositoryInputSearch.class.getSimpleName());
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
    public @NotNull MavenRepositorySearch getSearch() {
        if (SEARCH == null) {
            synchronized (MavenRepositorySearch.class) {
                if (SEARCH == null) {
                    SEARCH = new MavenRepositorySearch();
                    SEARCH.setRepository(this.mavenRepository);
                    SEARCH.setDaemon(true);
                    SEARCH.setName(MavenRepositorySearch.class.getSimpleName());
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
    public @NotNull MavenArtifactDatabase getDatabase() {
        if (DATABASE == null) {
            synchronized (MavenArtifactDatabase.class) {
                if (DATABASE == null) {
                    DATABASE = new MavenArtifactDatabase();
                }
            }
        }
        return DATABASE;
    }
}
