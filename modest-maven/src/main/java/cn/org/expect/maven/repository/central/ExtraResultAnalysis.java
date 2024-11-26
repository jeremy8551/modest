package cn.org.expect.maven.repository.central;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.impl.MavenArtifactImpl;
import org.json.JSONObject;

/**
 * 用于解析精确查询返回的 Json 字符串
 */
public class ExtraResultAnalysis extends PatternResultAnalysis {

    public MavenArtifact build(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("latestVersion");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        int versionCount = json.getInt("versionCount");

        return new MavenArtifactImpl(groupId, artifactId, version, packaging, timestamp, versionCount);
    }
}
