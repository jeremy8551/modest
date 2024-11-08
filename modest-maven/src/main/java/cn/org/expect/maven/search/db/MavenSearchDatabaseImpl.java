package cn.org.expect.maven.search.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

public class MavenSearchDatabaseImpl implements MavenSearchDatabase {
    private final static Log log = LogFactory.getLog(MavenSearchDatabaseImpl.class);

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
        this.load();
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
        String filepath = this.localRepository.getAddress();
        if (FileUtils.isDirectory(filepath)) {
            File dir = new File(new File(filepath).getParentFile(), ".search");
            if (FileUtils.createDirectory(dir)) {
                File file1 = new File(dir, "patternMap.json");
                String map1 = mapper.writeValueAsString(this.patternMap);
                log.info("save: ", file1.getAbsolutePath());
                FileUtils.write(file1, CharsetName.UTF_8, false, map1);

                File file2 = new File(dir, "extraMap.json");
                String map2 = mapper.writeValueAsString(this.extraMap);
                log.info("save: ", file2.getAbsolutePath());
                FileUtils.write(file2, CharsetName.UTF_8, false, map2);
            }
        }
    }

    public void load() {
        try {
            String filepath = this.localRepository.getAddress();
            if (FileUtils.isDirectory(filepath)) {
                File dir = new File(new File(filepath).getParentFile(), ".search");
                if (dir.exists() && dir.isDirectory()) {
                    File file1 = new File(dir, "patternMap.json");
                    if (file1.exists() && file1.isFile()) {
                        String jsonStr = FileUtils.readline(file1, CharsetName.UTF_8, 0);
                        this.parse1(jsonStr);
                    }

                    File file2 = new File(dir, "extraMap.json");
                    if (file2.exists() && file2.isFile()) {
                        String jsonStr = FileUtils.readline(file2, CharsetName.UTF_8, 0);
                        this.parse2(jsonStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse2(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String gid = keys.next();

            JSONObject obj = json.getJSONObject(gid);
            Iterator<String> groupKeys = obj.keys();
            while (groupKeys.hasNext()) {
                String aid = groupKeys.next();

                JSONObject aObj = obj.getJSONObject(aid);
                int start = aObj.getInt("start");
                int foundNumber = aObj.getInt("foundNumber");
                List<MavenArtifact> list = new ArrayList<>();
                JSONArray listArray = aObj.getJSONArray("list");
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject item = listArray.getJSONObject(i);
                    String groupId = item.getString("groupId");
                    String artifactId = item.getString("artifactId");
                    String version = item.getString("version");
                    String type = item.getString("type");
                    long timestamp = item.getLong("timestamp");
                    int versionCount = item.getInt("versionCount");

                    list.add(new MavenArtifactImpl(groupId, artifactId, version, type, timestamp, versionCount));
                }

                Map<String, MavenSearchResult> groupMap = this.extraMap.computeIfAbsent(gid, k -> new HashMap<String, MavenSearchResult>());
                groupMap.put(aid, new SimpleMavenSearchResult(list, start, foundNumber));
            }
        }

        log.info("load extraMap size: {}", this.extraMap.size());
    }

    private void parse1(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String pattern = keys.next();

            JSONObject obj = json.getJSONObject(pattern);
            int start = obj.getInt("start");
            int foundNumber = obj.getInt("foundNumber");

            List<MavenArtifact> list = new ArrayList<>();
            JSONArray listArray = obj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                JSONObject item = listArray.getJSONObject(i);
                String groupId = item.getString("groupId");
                String artifactId = item.getString("artifactId");
                String version = item.getString("version");
                String type = item.getString("type");
                long timestamp = item.getLong("timestamp");
                int versionCount = item.getInt("versionCount");

                list.add(new MavenArtifactImpl(groupId, artifactId, version, type, timestamp, versionCount));
            }

            this.patternMap.put(pattern, new SimpleMavenSearchResult(list, start, foundNumber));
        }

        log.info("load patternMap size: {}", this.patternMap.size());
    }

    public void store() {
        try {
            this.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
