package cn.org.expect.maven.repository.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class LocalRepository implements MavenRepository {

    private final LocalRepositoryConfig config;

    public LocalRepository(LocalRepositoryConfig config) {
        this.config = Ensure.notNull(config);
    }

    public String getAddress() {
        File file = this.config.getRepository();
        return file == null ? "" : file.getAbsolutePath();
    }

    public MavenSearchResult query(String pattern, int start) throws Exception {
        throw new UnsupportedOperationException();
    }

    public MavenSearchResult query(String groupId, String artifactId) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断工件是否存在
     *
     * @param artifact 工件信息
     * @return 返回true表示工件存在 false表示不存在
     */
    public boolean exists(MavenArtifact artifact) {
        File repository = this.config.getRepository();
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
        File repository = this.config.getRepository();
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
