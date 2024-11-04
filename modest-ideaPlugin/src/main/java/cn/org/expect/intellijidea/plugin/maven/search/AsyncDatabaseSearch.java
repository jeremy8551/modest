package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.intellijidea.plugin.maven.db.MavenArtifactDatabase;
import cn.org.expect.intellijidea.plugin.maven.central.CentralRepository;
import cn.org.expect.intellijidea.plugin.maven.local.LocalMavenRepository;
import org.jetbrains.annotations.NotNull;

public class AsyncDatabaseSearch {

    /** 远程调用组件 */
    private final MavenRepository mavenRepository;

    private final LocalMavenRepository localMavenRepository;

    private volatile static MavenRepositorySearchPattern searchPattern;

    private volatile static MavenRepositorySearchExtra searchExtra;

    private volatile static MavenArtifactDatabase database;

    public AsyncDatabaseSearch() {
        this.mavenRepository = new CentralRepository();
        this.localMavenRepository = new LocalMavenRepository();
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
    public LocalMavenRepository getLocalMavenRepository() {
        return localMavenRepository;
    }

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    public @NotNull MavenRepositorySearchPattern getSearchPattern() {
        if (searchPattern == null) {
            synchronized (MavenRepositorySearchPattern.class) {
                if (searchPattern == null) {
                    searchPattern = new MavenRepositorySearchPattern();
                    searchPattern.setMavenRepository(this.mavenRepository);
                    searchPattern.setDaemon(true);
                    searchPattern.setName(MavenRepositorySearchPattern.class.getSimpleName());
                    searchPattern.start();
                }
            }
        }
        return searchPattern;
    }

    /**
     * 返回精确查询工具
     *
     * @return 精确查询工具
     */
    public @NotNull MavenRepositorySearchExtra getSearchExtra() {
        if (searchExtra == null) {
            synchronized (MavenRepositorySearchExtra.class) {
                if (searchExtra == null) {
                    searchExtra = new MavenRepositorySearchExtra();
                    searchExtra.setMavenRepository(this.mavenRepository);
                    searchExtra.setDaemon(true);
                    searchExtra.setName(MavenRepositorySearchExtra.class.getSimpleName());
                    searchExtra.start();
                }
            }
        }
        return searchExtra;
    }

    /**
     * 返回数据库对象
     *
     * @return 数据库对象
     */
    public @NotNull MavenArtifactDatabase getDatabase() {
        if (database == null) {
            synchronized (MavenArtifactDatabase.class) {
                if (database == null) {
                    database = new MavenArtifactDatabase();
                }
            }
        }
        return database;
    }
}
