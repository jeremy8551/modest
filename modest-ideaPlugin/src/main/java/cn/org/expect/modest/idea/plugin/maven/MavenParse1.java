package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenArtifact;
import org.json.JSONObject;

public class MavenParse1 implements JsonParse {

    @Override
    public MavenArtifact execute(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("latestVersion");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        int versionCount = json.getInt("versionCount");

        return new MavenArtifact(groupId, artifactId, version, packaging, timestamp, versionCount);
    }
}
