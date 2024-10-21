package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenArtifact;
import org.json.JSONObject;

public interface JsonParse {

    MavenArtifact execute(JSONObject json);
}
