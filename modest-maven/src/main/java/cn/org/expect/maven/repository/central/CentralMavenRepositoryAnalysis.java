package cn.org.expect.maven.repository.central;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.impl.SimpleArtifact;
import cn.org.expect.maven.impl.SimpleArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.ArtifactSearchResultType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于解析模糊查询返回的 Json 字符串
 */
public class CentralMavenRepositoryAnalysis {
    protected final static Log log = LogFactory.getLog(CentralMavenRepositoryAnalysis.class);

    public ArtifactSearchResult parseExtraResult(String responseBody) {
        try {
            return this.parse0(responseBody, false);
        } catch (Exception e) {
            log.error("responseBody: {}", responseBody);
            throw e;
        }
    }

    public ArtifactSearchResult parsePatternResult(String responseBody) {
        try {
            return this.parse0(responseBody, true);
        } catch (Exception e) {
            log.error("responseBody: {}", responseBody);
            throw e;
        }
    }

    private SimpleArtifactSearchResult parse0(String responseBody, boolean patternOrExtra) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject response = jsonObject.getJSONObject("response");
        int numFound = response.getInt("numFound"); // 总记录数
        int start = response.getInt("start"); // 起始位置，从0开始
        JSONArray docs = response.getJSONArray("docs");

        if (log.isDebugEnabled()) {
            log.debug("send Response, find: {}, return {}, response: {}", numFound, docs.length(), responseBody);
        }

        List<Artifact> list = new ArrayList<>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject json = docs.getJSONObject(i);
            Artifact item = patternOrExtra ? parsePatternResult(json) : parseExtraResult(json);
            list.add(item);
        }
        return new SimpleArtifactSearchResult(CentralMavenRepository.class.getName(), ArtifactSearchResultType.LIMIT_PAGE, list, start + list.size() + 1, numFound, System.currentTimeMillis(), numFound > list.size());
    }

    public static Artifact parseExtraResult(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        String version;
        try {
            version = json.getString("v");
        } catch (JSONException e) {
            version = json.getString("latestVersion");
        }

        return new SimpleArtifact(groupId, artifactId, version, packaging, new Date(timestamp), -1);
    }

    public static Artifact parsePatternResult(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("latestVersion");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        int versionCount = json.getInt("versionCount");

        return new SimpleArtifact(groupId, artifactId, version, packaging, new Date(timestamp), versionCount);
    }
}
