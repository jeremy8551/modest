package cn.org.expect.intellijidea.plugin.maven.central;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.impl.JsonResultImpl;
import cn.org.expect.intellijidea.plugin.maven.impl.MavenArtifactImpl;
import cn.org.expect.intellijidea.plugin.maven.search.JsonResult;
import com.intellij.openapi.diagnostic.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 用于解析模糊查询返回的 Json 字符串
 */
public class PatternResultAnalysis {
    protected static final Logger log = Logger.getInstance(PatternResultAnalysis.class);

    public MavenArtifact build(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("v");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");

        return new MavenArtifactImpl(groupId, artifactId, version, packaging, timestamp, -1);
    }

    public JsonResult parse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        JSONObject response = json.getJSONObject("response");
        int numFound = response.getInt("numFound"); // 总记录数
        int start = response.getInt("start"); // 起始位置，从0开始
        JSONArray docs = response.getJSONArray("docs");

        log.warn("send Response, find: " + numFound + ", return " + docs.length() + ", response: " + responseBody);
        List<MavenArtifact> list = new ArrayList<MavenArtifact>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            MavenArtifact item = this.build(doc);
            list.add(item);
        }
        return new JsonResultImpl(numFound, start, list);
    }
}
