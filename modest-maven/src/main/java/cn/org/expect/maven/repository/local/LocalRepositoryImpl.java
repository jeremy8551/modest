package cn.org.expect.maven.repository.local;

import java.io.File;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactOperation;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.util.Ensure;

/**
 * 本地仓库
 */
@EasyBean(value = "query.use.local", priority = Integer.MAX_VALUE - 1)
public class LocalRepositoryImpl implements LocalRepository {

    /** 本地仓库 */
    private final LocalRepositoryDatabase database;

    /** 本地仓库的配置信息 */
    private final LocalRepositorySettings settings;

    public LocalRepositoryImpl(LocalRepositorySettings settings) {
        this.settings = Ensure.notNull(settings);
        this.database = new LocalRepositoryDatabase(settings.getRepository());
    }

    public ArtifactOperation getSupported() {
        return new ArtifactOperation() {

            public boolean supportOpenInCentralRepository() {
                return true;
            }

            public boolean supportDownload() {
                return false;
            }

            public boolean supportDelete() {
                return false;
            }

            public boolean supportOpenInFileSystem() {
                return true;
            }
        };
    }

    public LocalRepositorySettings getSettings() {
        return this.settings;
    }

    public ArtifactRepositoryDatabase getDatabase() {
        return this.database;
    }

    public String getAddress() {
        File file = this.settings.getRepository();
        return file == null ? "" : file.getAbsolutePath();
    }

    public ArtifactSearchResult query(String pattern, int start) throws Exception {
        return this.database.select(pattern);
    }

    public ArtifactSearchResult query(String groupId, String artifactId) throws Exception {
        ArtifactSearchResult result = this.database.select(groupId, artifactId);
        return result == null ? new SimpleArtifactSearchResult(LocalRepository.class.getName()) : result;
    }

    public boolean isTerminate() {
        return false;
    }

    public void terminate() {
    }
}
