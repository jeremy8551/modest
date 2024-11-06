package cn.org.expect.intellijidea.plugin.maven.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.intellijidea.plugin.maven.MavenSearchResult;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public class LocalMavenRepository implements MavenRepository {

    private String mavenHomePath;

    private File userSettingFile;

    private File m2;

    private File repository;

    private boolean useMavenConfig;

    public LocalMavenRepository() {
        this.mavenHomePath = "";
        this.m2 = new File(Settings.getUserHome(), ".m2");
        this.repository = new File(this.m2, "repository");
        this.userSettingFile = new File(this.m2, "settings.xml");
        this.useMavenConfig = false;
    }

    @Override
    public String getAddress() {
        return this.repository.getAbsolutePath();
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
        if (this.repository.exists() && this.repository.isDirectory()) {
            List<String> list = new ArrayList<>();
            list.add(this.repository.getAbsolutePath());
            StringUtils.split(artifact.getGroupId(), '.', list);
            list.add(artifact.getArtifactId());
            list.add(artifact.getVersion());

            String filepath = FileUtils.joinPath(list.toArray(new String[0]));
            File artifactDir = new File(filepath);
            if (artifactDir.exists() && artifactDir.isDirectory()) {
                File[] files = artifactDir.listFiles((dir, name) -> name.equalsIgnoreCase(artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar"));
                return files != null && files.length > 0;
            }
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
