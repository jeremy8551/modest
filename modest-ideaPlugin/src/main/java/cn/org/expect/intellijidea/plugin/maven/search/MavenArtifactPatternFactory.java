package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.impl.MavenArtifactImpl;
import org.json.JSONObject;

/**
 * 用于解析精确查询返回的 Json 字符串
 */
public class MavenArtifactPatternFactory implements MavenArtifactFactory {

    @Override
    public MavenArtifact build(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("v");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");

        return new MavenArtifactImpl(groupId, artifactId, version, packaging, timestamp, -1);
    }
}
