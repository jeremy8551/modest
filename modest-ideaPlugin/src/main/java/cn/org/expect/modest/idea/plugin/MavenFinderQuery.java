package cn.org.expect.modest.idea.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class MavenFinderQuery {

    private static final Logger log = Logger.getInstance(MavenFinderQuery.class);

    public List<MavenFinderItem> execute(String pattern) throws IOException {
        String url = "https://search.maven.org/solrsearch/select?q=" + pattern + "&rows=999&wt=json"; // 构建请求 URL
        OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build(); // 创建 Request 实例
        Response response = client.newCall(request).execute(); // 发送请求并获取响应
        String responseBody = response.body().string(); // 读取响应体
        JSONObject json = new JSONObject(responseBody);
        JSONObject responseStr = json.getJSONObject("response");
        JSONArray docs = responseStr.getJSONArray("docs");

        System.out.println("response: " + response.code() + ", pattern: " + pattern + ", docs: " + docs.length() + ", responseBody: " + responseBody);

        List<MavenFinderItem> list = new ArrayList<MavenFinderItem>();
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            MavenFinderItem item = this.parse(doc);
            list.add(item);
        }
        return list;
    }

    protected MavenFinderItem parse(JSONObject json) {
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

        return new MavenFinderItem(artifactId, groupId, version, packaging, repositoryId, timestamp, versionCount, text, ec);
    }
}
