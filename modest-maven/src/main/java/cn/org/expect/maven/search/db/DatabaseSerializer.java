package cn.org.expect.maven.search.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseSerializer {
    private final static Log log = LogFactory.getLog(DatabaseSerializer.class);

    public final static String PATTERN_TABLE = "PATTERN_TABLE.json";

    public final static String ARTIFACT_TABLE = "ARTIFACT_TABLE.json";

    public DatabaseSerializer(File repository, Map<String, MavenSearchResult> pattern, Map<String, Map<String, MavenSearchResult>> artifact) {
        this.load(repository, pattern, artifact);
    }

    public void save(File repository, Map<String, MavenSearchResult> pattern, Map<String, Map<String, MavenSearchResult>> artifact) {
        ObjectMapper mapper = new ObjectMapper();
        if (repository.exists() && repository.isDirectory()) {
            File dir = new File(repository.getParentFile(), ".search");
            if (FileUtils.createDirectory(dir)) {
                File file1 = new File(dir, DatabaseSerializer.PATTERN_TABLE);
                File file2 = new File(dir, DatabaseSerializer.ARTIFACT_TABLE);
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
    }

    public void load(File repository, Map<String, MavenSearchResult> pattern, Map<String, Map<String, MavenSearchResult>> artifact) {
        try {
            if (repository.exists() && repository.isDirectory()) {
                File dir = new File(repository.getParentFile(), ".search");
                if (dir.exists() && dir.isDirectory()) {
                    File file1 = new File(dir, DatabaseSerializer.PATTERN_TABLE);
                    boolean loadfile1 = false;
                    if (file1.exists() && file1.isFile()) {
                        String jsonStr = FileUtils.readline(file1, CharsetName.UTF_8, 0);
                        pattern.clear();
                        pattern.putAll(this.deserializePatternTable(jsonStr));
                        loadfile1 = true;
                    }

                    boolean loadfile2 = false;
                    File file2 = new File(dir, DatabaseSerializer.ARTIFACT_TABLE);
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
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private Map<String, Map<String, MavenSearchResult>> deserializeArtifactTable(String jsonStr) {
        Map<String, Map<String, MavenSearchResult>> map = new ConcurrentHashMap<String, Map<String, MavenSearchResult>>();
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

                Map<String, MavenSearchResult> groupMap = map.computeIfAbsent(gid, k -> new LinkedHashMap<String, MavenSearchResult>());
                groupMap.put(aid, new SimpleMavenSearchResult(list, start, foundNumber));
            }
        }
        return map;
    }

    private Map<String, MavenSearchResult> deserializePatternTable(String jsonStr) {
        Map<String, MavenSearchResult> map = new LinkedHashMap<>();
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

            map.put(pattern, new SimpleMavenSearchResult(list, start, foundNumber));
        }
        return map;
    }
}