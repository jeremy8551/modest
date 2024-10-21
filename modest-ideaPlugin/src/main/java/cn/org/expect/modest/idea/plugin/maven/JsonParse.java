package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenFinderItem;
import org.json.JSONObject;

public interface JsonParse {

    MavenFinderItem execute(JSONObject json);
}
