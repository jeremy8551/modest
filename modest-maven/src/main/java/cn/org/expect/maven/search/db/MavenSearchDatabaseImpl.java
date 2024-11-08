package cn.org.expect.maven.search.db;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MavenSearchDatabaseImpl implements MavenSearchDatabase {

    private LocalRepository localRepository;

    /**
     * 模糊搜索词 pattern 与 {@linkplain MavenSearchResult} 的映射
     */
    protected final Map<String, MavenSearchResult> patternMap;

    /**
     * groupid、artifactId 与 {@linkplain MavenSearchResult} 的映射
     */
    protected final Map<String, Map<String, MavenSearchResult>> extraMap;

    public MavenSearchDatabaseImpl(LocalRepository localRepository) {
        this.localRepository = Ensure.notNull(localRepository);
        this.patternMap = new ConcurrentHashMap<String, MavenSearchResult>();
        this.extraMap = new ConcurrentHashMap<String, Map<String, MavenSearchResult>>();
//        this.load();
    }

    @Override
    public void insert(String id, MavenSearchResult resultSet) {
        this.patternMap.put(id, resultSet);
        this.store();
    }

    @Override
    public MavenSearchResult select(String id) {
        return this.patternMap.get(id);
    }

    @Override
    public void insert(String groupId, String artifactId, MavenSearchResult result) {
        Map<String, MavenSearchResult> group = this.extraMap.computeIfAbsent(groupId, k -> new HashMap<String, MavenSearchResult>());
        group.put(artifactId, result);
        this.store();
    }

    @Override
    public MavenSearchResult select(String groupId, String artifactId) {
        Map<String, MavenSearchResult> map = this.extraMap.get(groupId);
        if (map != null) {
            return map.get(artifactId);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        if (StringUtils.isNotBlank(id)) {
            this.patternMap.remove(id);
            this.store();
        }
    }

    @Override
    public void clear() {
        this.patternMap.clear();
        this.extraMap.clear();
        this.store();
    }

    @Override
    public void save() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String patternMap = mapper.writeValueAsString(this.patternMap);
        String extraMap = mapper.writeValueAsString(this.extraMap);

        String filepath = this.localRepository.getAddress();
        if (FileUtils.isDirectory(filepath)) {
            File dir = new File(new File(filepath), ".search");
            if (FileUtils.createDirectory(dir)) {
                File file1 = new File(dir, "patternMap.json");
                FileUtils.write(file1, CharsetName.UTF_8, false, patternMap);

                File file2 = new File(dir, "extraMap.json");
                FileUtils.write(file2, CharsetName.UTF_8, false, extraMap);
            }
        }
    }

    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String filepath = this.localRepository.getAddress();
            if (FileUtils.isDirectory(filepath)) {
                File dir = new File(new File(filepath), ".search");
                if (dir.exists() && dir.isDirectory()) {
                    File file1 = new File(dir, "patternMap.json");
                    Map<String, MavenSearchResult> patternMap = mapper.readValue(FileUtils.readline(file1, CharsetName.UTF_8, 0), Map.class);
                    this.patternMap.putAll(patternMap);
                    System.out.println("load patternMap size: " + this.patternMap.size());

                    File file2 = new File(dir, "extraMap.json");
                    Map<String, Map<String, MavenSearchResult>> extraMap = mapper.readValue(FileUtils.readline(file2, CharsetName.UTF_8, 0), Map.class);
                    this.extraMap.putAll(extraMap);
                    System.out.println("load extraMap size: " + this.extraMap.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void store() {
        try {
            this.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
