package cn.org.expect.maven.repository.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenArtifactOperation;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 本地仓库
 */
@EasyBean(value = "local", priority = Integer.MAX_VALUE - 1)
public class LocalMavenRepository implements MavenRepository {

    private final LocalMavenRepositoryDatabase database;

    private final LocalMavenRepositorySettings settings;

    public LocalMavenRepository(LocalMavenRepositorySettings settings) {
        this.settings = Ensure.notNull(settings);
        File dir = this.settings.getRepository();
        this.database = new LocalMavenRepositoryDatabase(dir);
    }

    public MavenArtifactOperation getSupported() {
        return new MavenArtifactOperation() {

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

    public LocalMavenRepositorySettings getSettings() {
        return this.settings;
    }

    public MavenRepositoryDatabase getDatabase() {
        return this.database;
    }

    public String getAddress() {
        File file = this.settings.getRepository();
        return file == null ? "" : file.getAbsolutePath();
    }

    public MavenSearchResult query(String pattern, int start) throws Exception {
        return this.database.select(pattern);
    }

    public MavenSearchResult query(String groupId, String artifactId) throws Exception {
        MavenSearchResult result = this.database.select(groupId, artifactId);
        return result == null ? new SimpleMavenSearchResult() : result;
    }

    /**
     * 判断工件是否存在
     *
     * @param artifact 工件信息
     * @return 返回true表示工件存在 false表示不存在
     */
    public boolean exists(MavenArtifact artifact) {
        File repository = this.settings.getRepository();
        if (repository != null && repository.exists() && repository.isDirectory()) {
            List<String> list = new ArrayList<>();
            list.add(repository.getAbsolutePath());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());

            String filepath = FileUtils.joinPath(list.toArray(new String[0]));
            String filename = artifact.getArtifactId() + "-" + artifact.getVersion();
            File parent = new File(filepath);
            File[] files = parent.listFiles(file -> file.exists() && file.isFile() && FileUtils.getFilenameNoExt(file.getName()).equalsIgnoreCase(filename) && StringUtils.inArrayIgnoreCase(FileUtils.getFilenameExt(file.getName()), "jar", artifact.getType()));
            return files != null && files.length > 0;
        }
        return false;
    }

    public File getJarfile(MavenArtifact artifact) {
        File repository = this.settings.getRepository();
        if (repository != null && repository.exists() && repository.isDirectory()) {
            List<String> list = new ArrayList<>();
            list.add(repository.getAbsolutePath());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());

            String filepath = FileUtils.joinPath(list.toArray(new String[0]));
            String filename = artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar";
            File parent = new File(filepath);
            File[] array = parent.listFiles(file -> file.exists() && file.isFile() && file.getName().equalsIgnoreCase(filename));
            if (array != null && array.length == 1) {
                return array[0];
            }
        }
        return null;
    }

    public boolean isTerminate() {
        return false;
    }

    public void terminate() {
    }
}
