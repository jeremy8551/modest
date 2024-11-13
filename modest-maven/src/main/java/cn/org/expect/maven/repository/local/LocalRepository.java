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

    @Override
    public String getAddress() {
        File file = this.config.getRepository();
        return file == null ? "" : file.getAbsolutePath();
    }

    @Override
    public MavenSearchResult query(String pattern, int start) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public MavenSearchResult query(String groupId, String artifactId) throws Exception {
        throw new UnsupportedOperationException();
    }

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

    @Override
    public boolean isTerminate() {
        return false;
    }

    @Override
    public void terminate() {
    }
}
