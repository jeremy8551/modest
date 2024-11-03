package cn.org.expect.intellijidea.plugin.maven.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.intellijidea.plugin.maven.search.MavenArtifactExtraFactory;
import cn.org.expect.intellijidea.plugin.maven.search.MavenArtifactFactory;
import cn.org.expect.intellijidea.plugin.maven.search.MavenArtifactPatternFactory;
import com.intellij.openapi.diagnostic.Logger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 中央仓库
 */
public class CentralMavenRepository implements MavenRepository {
    private static final Logger log = Logger.getInstance(CentralMavenRepository.class);

    protected MavenArtifactExtraFactory pattern;

    protected MavenArtifactPatternFactory extra;

    protected volatile Call call;

    protected volatile boolean notTerminate;

    /** 按时间戳倒序 */
    protected final static Comparator<MavenArtifact> REVERSED_COMPARATOR = Comparator.comparing(MavenArtifact::getTimestamp).reversed();

    public CentralMavenRepository() {
        this.pattern = new MavenArtifactExtraFactory();
        this.extra = new MavenArtifactPatternFactory();
        this.notTerminate = true;
    }

    @Override
    public String getAddress() {
        return "https://repo1.maven.org/maven2/";
    }

    @Override
    public List<MavenArtifact> query(String pattern) {
        this.notTerminate = true;
        String url = "https://search.maven.org/solrsearch/select?q=" + pattern + "&rows=200&wt=json"; // 构建请求 URL
        System.out.println(url);
        List<MavenArtifact> list = this.send(url, this.pattern);
        list.sort(this.getComparator().reversed());
        return list;
    }

    public @NotNull Comparator<MavenArtifact> getComparator() {
        return (o1, o2) -> {
            int vv = o1.getVersionCount() - o2.getVersionCount(); // 版本数
            if (vv != 0) {
                return vv;
            }

            int tv = o1.getTimestamp().compareTo(o2.getTimestamp()); // 最新发布
            if (tv != 0) {
                return tv;
            }

            int gv = o1.getGroupId().compareTo(o2.getGroupId());
            if (gv != 0) {
                return gv;
            }

            return o1.getArtifactId().compareTo(o2.getArtifactId());
        };
    }

    @Override
    public List<MavenArtifact> query(String groupId, String artifactId) {
        this.notTerminate = true;
        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=200&wt=json"; // 构建请求 URL
        System.out.println(url);
        List<MavenArtifact> list = this.send(url, this.extra);
        list.sort(REVERSED_COMPARATOR);
        return list;
    }

    public synchronized String sendURL(String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例
            Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build(); // 创建 Request 实例
            Call call = client.newCall(request);
            this.call = call;
            Response response = call.execute(); // 发送请求并获取响应
            return response.body().string(); // 读取响应体
        } finally {
            this.call = null;
        }
    }

    public String sendRequest(String url) {
        log.info("send Request: " + url);
        Throwable throwable = null;
        int times = 3;
        for (int i = 0; i < times && this.notTerminate; i++) {
            try {
                return this.sendURL(url);
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }

        if (this.notTerminate) {
            throw new RuntimeException("try " + times + " times send request, but fail!", throwable);
        } else {
            return null;
        }
    }

    public List<MavenArtifact> send(String url, MavenArtifactFactory factory) {
        String responseBody = this.sendRequest(url);
        if (!this.notTerminate) {
            return new ArrayList<MavenArtifact>(0);
        }

        JSONObject json = new JSONObject(responseBody);
        JSONObject responseStr = json.getJSONObject("response");
        int numFound = responseStr.getInt("numFound"); // 总记录数
        int start = responseStr.getInt("start"); // 起始位置，从0开始
        JSONArray docs = responseStr.getJSONArray("docs");
        log.info("send Response, find: " + docs.length() + ", responseBody: " + responseBody);

        List<MavenArtifact> list = new ArrayList<MavenArtifact>(docs.length());
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            MavenArtifact item = factory.build(doc);
            list.add(item);
        }

        if (numFound > list.size()) {
            int left = numFound - list.size();
            
        }

        return list;
    }

    @Override
    public boolean isTerminate() {
        return this.notTerminate;
    }

    @Override
    public void terminate() {
        this.notTerminate = false;
        if (this.call != null) {
            this.call.cancel();
        }
    }
}
