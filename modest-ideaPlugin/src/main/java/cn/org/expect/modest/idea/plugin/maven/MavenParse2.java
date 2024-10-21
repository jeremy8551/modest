package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenArtifact;
import org.json.JSONObject;

public class MavenParse2 implements JsonParse {

    @Override
    public MavenArtifact execute(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("v");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");

        return new MavenArtifact(groupId, artifactId, version, packaging, timestamp, -1);
    }
}
