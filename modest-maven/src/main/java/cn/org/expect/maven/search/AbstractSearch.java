package cn.org.expect.maven.search;

import cn.org.expect.maven.intellij.idea.MavenPluginContext;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.central.CentralRepository;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositoryConfig;
import cn.org.expect.maven.search.db.MavenArtifactDatabase;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSearch {

    /** 远程调用组件 */
    private final MavenRepository mavenRepository;

    private final LocalRepository localMavenRepository;

    private volatile static InputSearchThread INPUT_SEARCH;

    private volatile static SearchThread SEARCH;

    private volatile static MavenArtifactDatabase DATABASE;

    public AbstractSearch(MavenPluginContext context) {
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
    public @NotNull InputSearchThread getInputSearch() {
        if (INPUT_SEARCH == null) {
            synchronized (InputSearchThread.class) {
                if (INPUT_SEARCH == null) {
                    INPUT_SEARCH = new InputSearchThread();
                    INPUT_SEARCH.setRepository(this.mavenRepository);
                    INPUT_SEARCH.setDaemon(true);
                    INPUT_SEARCH.setName(InputSearchThread.class.getSimpleName());
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
    public @NotNull SearchThread getSearch() {
        if (SEARCH == null) {
            synchronized (SearchThread.class) {
                if (SEARCH == null) {
                    SEARCH = new SearchThread();
                    SEARCH.setRepository(this.mavenRepository);
                    SEARCH.setDaemon(true);
                    SEARCH.setName(SearchThread.class.getSimpleName());
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
