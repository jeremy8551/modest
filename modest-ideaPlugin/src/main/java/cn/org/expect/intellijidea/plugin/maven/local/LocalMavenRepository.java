package cn.org.expect.intellijidea.plugin.maven.local;

import java.io.File;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenArtifactSet;
import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;

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
    public MavenArtifactSet query(String pattern, int start) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public MavenArtifactSet query(String groupId, String artifactId) throws Exception {
        throw new UnsupportedOperationException();
    }

    public boolean exists(MavenArtifact artifact) {
        if (this.repository.exists() && this.repository.isDirectory()) {
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();

            String str = groupId + "." + artifactId;
            String filepath = FileUtils.joinPath(this.repository.getAbsolutePath(), str.replace('.', File.separatorChar), version);
            File artifactDir = new File(filepath);
            if (artifactDir.exists() && artifactDir.isDirectory()) {
                File[] files = artifactDir.listFiles((dir, name) -> name.endsWith("-" + version + ".jar"));
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
