package cn.org.expect.modest.idea.plugin.maven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.modest.idea.plugin.db.MavenFinderQuery;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import com.intellij.openapi.diagnostic.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class MavenFinderQueryByCentral implements MavenFinderQuery {
    private static final Logger log = Logger.getInstance(MavenFinderQueryByCentral.class);

    protected MavenParse1 parse1;

    protected MavenParse2 parse2;

    public MavenFinderQueryByCentral() {
        this.parse1 = new MavenParse1();
        this.parse2 = new MavenParse2();
    }

    @Override
    public String getRepositoryUrl() {
        return "https://repo1.maven.org/maven2/";
    }

    @Override
    public List<MavenArtifact> execute(String pattern) throws IOException {
        String url = "https://search.maven.org/solrsearch/select?q=" + pattern + "&rows=99&wt=json"; // 构建请求 URL
        return this.send(url, this.parse1);
    }

    @Override
    public List<MavenArtifact> execute(String groupId, String artifactId) throws IOException {
        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=99&wt=json"; // 构建请求 URL
        List<MavenArtifact> list = this.send(url, this.parse2);
        list.sort(Comparator.comparing(MavenArtifact::getTimestamp));
        return list;
    }

    public String sendURL(String url) throws IOException {
        OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build(); // 创建 Request 实例
        Response response = client.newCall(request).execute(); // 发送请求并获取响应
        return response.body().string(); // 读取响应体
    }

    public String sendRequest(String url) {
        log.warn("send Request: " + url);
        Throwable throwable = null;
        for (int i = 0; i < 3; i++) {
            try {
                return this.sendURL(url);
            } catch (Throwable e) {
                log.error("send request fail, url: " + url + "\nretry send request ..");
                if (throwable == null) {
                    throwable = e;
                }
            }
        }
        throw new RuntimeException("try 3 times send request, but fail!", throwable);
    }

    public List<MavenArtifact> send(String url, JsonParse parse) {
        String responseBody = this.sendRequest(url);
        JSONObject json = new JSONObject(responseBody);
        JSONObject responseStr = json.getJSONObject("response");
        JSONArray docs = responseStr.getJSONArray("docs");
        log.warn("send Response, find: " + docs.length() + ", responseBody: " + responseBody);

        List<MavenArtifact> list = new ArrayList<MavenArtifact>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            MavenArtifact item = parse.execute(doc);
            item.setRepositoryUrl(this.getRepositoryUrl());
            list.add(item);
        }

        Comparator<MavenArtifact> comparator = (o1, o2) -> {
            int gv = o1.getGroupId().compareTo(o2.getGroupId());
            if (gv != 0) {
                return gv;
            }

            int av = o1.getArtifactId().compareTo(o2.getArtifactId());
            if (av != 0) {
                return av;
            }

            return o1.getTimestamp().compareTo(o2.getTimestamp());
        };
        list.sort(comparator);
        return list;
    }
}
