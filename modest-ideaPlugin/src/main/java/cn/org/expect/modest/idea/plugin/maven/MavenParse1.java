package cn.org.expect.modest.idea.plugin.maven;

import cn.org.expect.modest.idea.plugin.MavenFinderItem;
import org.json.JSONArray;
import org.json.JSONObject;

public class MavenParse1 implements JsonParse {
    
    @Override
    public MavenFinderItem execute(JSONObject json) {
        String groupId = json.getString("g");
        String artifactId = json.getString("a");
        String version = json.getString("latestVersion");
        String repositoryId = json.getString("repositoryId");
        String packaging = json.getString("p");
        long timestamp = json.getLong("timestamp");
        int versionCount = json.getInt("versionCount");
        JSONArray textArray = json.getJSONArray("text");
        JSONArray ecArray = json.getJSONArray("ec");

        String[] text = new String[textArray.length()];
        for (int j = 0; j < text.length; j++) {
            text[j] = textArray.get(j).toString();
        }

        String[] ec = new String[ecArray.length()];
        for (int j = 0; j < ec.length; j++) {
            ec[j] = ecArray.get(j).toString();
        }

        return new MavenFinderItem(groupId, artifactId, version, packaging, repositoryId, timestamp, versionCount, text, ec);
    }
}
