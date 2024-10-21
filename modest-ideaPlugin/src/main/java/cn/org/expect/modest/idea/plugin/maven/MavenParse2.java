package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenFinderItem;
import org.json.JSONArray;
import org.json.JSONObject;

public class MavenParse2 implements JsonParse {

    @Override
    public MavenFinderItem execute(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("v");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        JSONArray ecArray = json.getJSONArray("ec");
        String[] ec = new String[ecArray.length()];
        for (int j = 0; j < ec.length; j++) {
            ec[j] = ecArray.get(j).toString();
        }

        return new MavenFinderItem(groupId, artifactId, version, packaging, "", timestamp, -1, new String[0], ec);
    }
}
