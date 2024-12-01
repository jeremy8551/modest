package cn.org.expect.maven.repository.central;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import cn.org.expect.maven.repository.impl.SimpleMavenSearchResult;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 用于解析模糊查询返回的 Json 字符串
 */
public class ExtraSearchResultAnalysis {
    protected final static Log log = LogFactory.getLog(ExtraSearchResultAnalysis.class);

    public ArtifactSearchResult parse(String responseBody) {
        if (responseBody == null || responseBody.length() == 0) {
            return null;
        }

        JSONObject json = new JSONObject(responseBody);
        JSONObject response = json.getJSONObject("response");
        int numFound = response.getInt("numFound"); // 总记录数
        int start = response.getInt("start"); // 起始位置，从0开始
        JSONArray docs = response.getJSONArray("docs");

        if (log.isDebugEnabled()) {
            log.debug("send Response, find: {}, return {}, response: {}", numFound, docs.length(), responseBody);
        }

        List<Artifact> list = new ArrayList<>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            Artifact item = this.build(doc);
            list.add(item);
        }
        return new SimpleMavenSearchResult(list, start + list.size() + 1, numFound, System.currentTimeMillis());
    }

    public Artifact build(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("v");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        return new MavenArtifactImpl(groupId, artifactId, version, packaging, new Date(timestamp), -1);
    }
}
