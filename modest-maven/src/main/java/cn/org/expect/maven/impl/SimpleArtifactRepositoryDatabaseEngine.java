package cn.org.expect.maven.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabaseEngine;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import cn.org.expect.maven.search.ArtifactSearchSettings;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleArtifactRepositoryDatabaseEngine implements ArtifactRepositoryDatabaseEngine {
    protected final static Log log = LogFactory.getLog(SimpleArtifactRepositoryDatabaseEngine.class);

    /** 模糊搜索表名 */
    protected String patternTable;

    /** 精确搜索表名 */
    protected String artifactTable;

    /** 表文件存储的目录 */
    protected File parent;

    /** 模糊搜索结果 */
    protected final Map<String, ArtifactSearchResult> pattern;

    /** 精确搜索结果 */
    protected final Map<String, Map<String, ArtifactSearchResult>> artifact;

    public SimpleArtifactRepositoryDatabaseEngine(ArtifactSearchSettings settings, String patternTableName, String artifactTableName) {
        this.pattern = new ConcurrentHashMap<>();
        this.artifact = new ConcurrentHashMap<>();
        this.parent = settings.getWorkHome();
        this.patternTable = patternTableName;
        this.artifactTable = artifactTableName;
        this.loadFile(this.pattern, this.artifact);
    }

    public Map<String, ArtifactSearchResult> getPattern() {
        return pattern;
    }

    public Map<String, Map<String, ArtifactSearchResult>> getArtifact() {
        return artifact;
    }

    public void clear() {
        this.pattern.clear();
        this.artifact.clear();
        this.save();
    }

    public void save() {
        this.save(this.pattern, this.artifact);
    }

    public void save(Map<String, ArtifactSearchResult> pattern, Map<String, Map<String, ArtifactSearchResult>> artifact) {
        ObjectMapper mapper = new ObjectMapper();
        File dir = this.parent;
        if (dir != null) {
            File file1 = new File(dir, this.patternTable);
            File file2 = new File(dir, this.artifactTable);
            if (log.isDebugEnabled()) {
                log.debug("save database files: {}, {} ..", file1.getAbsolutePath(), file2.getAbsolutePath());
            }

            try {
                String json1 = mapper.writeValueAsString(pattern);
                FileUtils.write(file1, CharsetName.UTF_8, false, json1);

                String json2 = mapper.writeValueAsString(artifact);
                FileUtils.write(file2, CharsetName.UTF_8, false, json2);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void loadFile(Map<String, ArtifactSearchResult> pattern, Map<String, Map<String, ArtifactSearchResult>> artifact) {
        try {
            File dir = this.parent;
            if (dir != null) {
                File file1 = new File(dir, this.patternTable);
                boolean loadfile1 = false;
                if (file1.exists() && file1.isFile()) {
                    String jsonStr = FileUtils.readline(file1, CharsetName.UTF_8, 0);
                    pattern.clear();
                    pattern.putAll(this.deserializePatternTable(jsonStr));
                    loadfile1 = true;
                }

                boolean loadfile2 = false;
                File file2 = new File(dir, this.artifactTable);
                if (file2.exists() && file2.isFile()) {
                    String jsonStr = FileUtils.readline(file2, CharsetName.UTF_8, 0);
                    artifact.clear();
                    artifact.putAll(this.deserializeArtifactTable(jsonStr));
                    loadfile2 = true;
                }

                if (log.isDebugEnabled()) {
                    log.debug("load database file: {}({}), {}({})", file1.getAbsolutePath(), loadfile1 ? pattern.size() : 0, file2.getAbsolutePath(), loadfile2 ? artifact.size() : 0);
                }
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private Map<String, Map<String, ArtifactSearchResult>> deserializeArtifactTable(String jsonStr) {
        Map<String, Map<String, ArtifactSearchResult>> map = new ConcurrentHashMap<>();
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
                long queryTime = aObj.getLong("queryTime");
                boolean hasMore = aObj.getBoolean("hasMore");
                ArtifactSearchResultType resultType = aObj.getEnum(ArtifactSearchResultType.class, "type");

                List<Artifact> list = new ArrayList<>();
                JSONArray listArray = aObj.getJSONArray("list");
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject item = listArray.getJSONObject(i);
                    String groupId = item.getString("groupId");
                    String artifactId = item.getString("artifactId");
                    String version = item.getString("version");
                    String type = item.getString("type");
                    long timestamp = item.optLong("timestamp", -1);
                    int versionCount = item.getInt("versionCount");

                    list.add(new SimpleArtifact(groupId, artifactId, version, type, timestamp == -1 ? null : new Date(timestamp), versionCount));
                }

                Map<String, ArtifactSearchResult> groupMap = map.computeIfAbsent(gid, k -> new LinkedHashMap<>());
                groupMap.put(aid, new SimpleArtifactSearchResult(resultType, list, start, foundNumber, queryTime, hasMore));
            }
        }
        return map;
    }

    private Map<String, ArtifactSearchResult> deserializePatternTable(String jsonStr) {
        Map<String, ArtifactSearchResult> map = new LinkedHashMap<>();
        JSONObject json = new JSONObject(jsonStr);
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String pattern = keys.next();

            JSONObject obj = json.getJSONObject(pattern);
            int start = obj.getInt("start");
            int foundNumber = obj.getInt("foundNumber");
            long queryTime = obj.getLong("queryTime");
            boolean hasMore = obj.getBoolean("hasMore");
            ArtifactSearchResultType resultType = obj.getEnum(ArtifactSearchResultType.class, "type");

            List<Artifact> list = new ArrayList<>();
            JSONArray listArray = obj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                JSONObject item = listArray.getJSONObject(i);
                String groupId = item.getString("groupId");
                String artifactId = item.getString("artifactId");
                String version = item.getString("version");
                String type = item.getString("type");
                long timestamp = item.optLong("timestamp", -1);
                int versionCount = item.getInt("versionCount");

                list.add(new SimpleArtifact(groupId, artifactId, version, type, timestamp == -1 ? null : new Date(timestamp), versionCount));
            }

            map.put(pattern, new SimpleArtifactSearchResult(resultType, list, start, foundNumber, queryTime, hasMore));
        }
        return map;
    }
}
