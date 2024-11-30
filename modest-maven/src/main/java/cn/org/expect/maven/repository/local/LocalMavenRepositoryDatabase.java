package cn.org.expect.maven.repository.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class LocalMavenRepositoryDatabase implements MavenRepositoryDatabase {
    private final static Log log = LogFactory.getLog(LocalMavenRepositoryDatabase.class);

    /** groupid、artifactId 与 {@linkplain MavenSearchResult} 的映射 */
    protected final Map<String, Map<String, MavenSearchResult>> map;

    protected final Set<String> groupIds;

    protected final Set<String> artifactIds;

    private final File repository;

    private final GroupID groupId;

    public LocalMavenRepositoryDatabase(File repository) {
        this.map = new LinkedHashMap<>();
        this.groupIds = new LinkedHashSet<>();
        this.artifactIds = new LinkedHashSet<>();
        this.groupId = new GroupID();
        this.repository = repository;
        this.load(repository);
//        this.print();
    }

    public MavenSearchResult select(String pattern) {
        List<String> list = StringUtils.splitByBlanks(pattern);
        List<String> parts = new ArrayList<>();
        for (String str : list) {
            String[] array = StringUtils.split(str, ':');
            for (String element : array) {
                parts.add(element);
            }
        }

        Set<MavenArtifact> mas = new HashSet<>();
        for (String key : parts) {
            Set<Map.Entry<String, Map<String, MavenSearchResult>>> entries = this.map.entrySet();
            for (Map.Entry<String, Map<String, MavenSearchResult>> entry : entries) {
                Map<String, MavenSearchResult> a2r = entry.getValue(); // artifactId - searchResult
                String groupId = entry.getKey();
                if (groupId.contains(key)) {
                    Collection<MavenSearchResult> msrs = a2r.values();
                    for (MavenSearchResult msr : msrs) {
                        List<MavenArtifact> mal = msr.getList();
                        if (mal.size() > 0) {
                            mas.add(mal.get(0));
                        }
                    }
                } else {
                    Set<String> arts = a2r.keySet();
                    for (String art : arts) {
                        if (art.contains(key)) {
                            MavenSearchResult searchResult = a2r.get(art);
                            List<MavenArtifact> mal = searchResult.getList();
                            if (mal.size() > 0) {
                                mas.add(mal.get(0));
                            }
                        }
                    }
                }
            }
        }

        return new SimpleMavenSearchResult(new ArrayList<>(mas), mas.size(), mas.size(), System.currentTimeMillis());
    }

    public void insert(String id, MavenSearchResult resultSet) {
    }

    public void delete(String id) {
    }

    public void insert(String groupId, String artifactId, MavenSearchResult result) {
    }

    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.map.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    public void clear() {
    }

    protected void print() {
        if (log.isDebugEnabled()) {
            Set<Map.Entry<String, Map<String, MavenSearchResult>>> entries = this.map.entrySet();
            for (Map.Entry<String, Map<String, MavenSearchResult>> entry : entries) {
                Set<Map.Entry<String, MavenSearchResult>> a2rs = entry.getValue().entrySet();
                for (Map.Entry<String, MavenSearchResult> a2r : a2rs) {
                    List<MavenArtifact> list = a2r.getValue().getList();
                    for (MavenArtifact artifact : list) {
                        log.debug("artifact: {} {} {}", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
                    }
                }
            }
        }
    }

    /**
     * 加载本地仓库中的工件
     *
     * @param dir 本地仓库目录
     */
    protected void load(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                this.load(file);
                continue;
            }

            if (file.isFile()) {
                this.process(file);
            }
        }
    }

    public void process(File file) {
        File parent = file.getParentFile(); // 目录应该是 version
        if (parent == null || parent.equals(this.repository)) {
            return;
        }
        String version = parent.getName();

        File parent1 = parent.getParentFile(); // 目录应该是 artifactId
        if (parent1 == null || parent1.equals(this.repository)) {
            return;
        }
        String artifactId = parent1.getName();

        File parent2 = parent1.getParentFile(); // 目录应该是 groupId，不应为仓库根目录
        if (parent2 == null || parent2.equals(this.repository)) {
            return;
        }

        String name = FileUtils.getFilenameNoExt(file.getName());
        if (name.equals(artifactId + "-" + version)) {
            String groupId = this.groupId.toString(parent2, this.repository);
            this.groupIds.add(groupId);
            this.artifactIds.add(artifactId);

            // 保存到缓存
            String ext = FileUtils.getFilenameExt(file.getName());
            MavenArtifact artifact = new MavenArtifactImpl(groupId, artifactId, version, ext, new Date(file.lastModified()), 0);
            Map<String, MavenSearchResult> group = this.map.computeIfAbsent(groupId, k -> new LinkedHashMap<>());
            MavenSearchResult searchResult = group.computeIfAbsent(artifactId, key -> new SimpleMavenSearchResult());
            searchResult.addArtifact(artifact);

            if (log.isTraceEnabled()) {
                log.debug("scan: {}  {}  {} file: {}", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), file.getAbsolutePath());
            }
        }
    }

    public static class GroupID extends ArrayList<String> {

        public String toString(File parent, File repository) {
            this.clear();
            this.add(parent.getName());
            File groupParent = parent.getParentFile();
            while (!groupParent.equals(repository)) {
                this.add(groupParent.getName());
                groupParent = groupParent.getParentFile();
            }

            StringBuilder buffer = new StringBuilder();
            for (int i = this.size() - 1; i >= 1; i--) {
                buffer.append(this.get(i)).append('.');
            }
            buffer.append(this.get(0));
            return buffer.toString();
        }
    }
}
