package cn.org.expect.maven.repository;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.impl.SimpleArtifactRepositoryDatabase;

public abstract class AbstractArtifactRepository extends HttpClient implements ArtifactRepository {

    /** 容器上下文信息 */
    private final EasyContext ioc;

    /** 数据库接口 */
    protected ArtifactRepositoryDatabase database;

    public AbstractArtifactRepository(EasyContext ioc) {
        this.ioc = ioc;
        this.database = new SimpleArtifactRepositoryDatabase(this.getClass(), ioc);
    }

    public EasyContext getEasyContext() {
        return this.ioc;
    }

    public ArtifactRepositoryDatabase getDatabase() {
        return this.database;
    }
}
