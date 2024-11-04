package cn.org.expect.intellijidea.plugin.maven.central;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenRepository;
import cn.org.expect.intellijidea.plugin.maven.search.JsonResult;
import com.intellij.openapi.diagnostic.Logger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 中央仓库
 */
public class CentralRepository implements MavenRepository {
    private static final Logger log = Logger.getInstance(CentralRepository.class);

    protected ExtraResultAnalysis pattern;

    protected PatternResultAnalysis extra;

    protected volatile Call call;

    protected volatile boolean notTerminate;

    public CentralRepository() {
        this.pattern = new ExtraResultAnalysis();
        this.extra = new PatternResultAnalysis();
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
        List<MavenArtifact> list = this.send(url, this.pattern);
        list.sort(PATTERN_RESULT_COMPARATOR.reversed());
        return list;
    }

    @Override
    public List<MavenArtifact> query(String groupId, String artifactId) {
        this.notTerminate = true;
        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=200&wt=json"; // 构建请求 URL
        List<MavenArtifact> list = this.send(url, this.extra);
        list.sort(EXTRA_RESULT_COMPARATOR);
        return list;
    }

    public List<MavenArtifact> send(String url, PatternResultAnalysis analysis) {
        String responseBody = this.sendRequest(url);
        JsonResult result = analysis.parse(responseBody);

        List<MavenArtifact> list = new ArrayList<MavenArtifact>(10);
        list.addAll(result.getList());

        if (result.getNumFound() > result.getList().size()) {
            int begin = result.getStart() + result.getList().size(); // 起始位置
            do {
                responseBody = this.sendRequest(url + "&start=" + begin);
                result = analysis.parse(responseBody);
                begin += result.getList().size();
                list.addAll(result.getList());
            } while (this.notTerminate && result.getNumFound() - begin > 0);
        }
        return list;
    }

    public String sendRequest(String url) {
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

    public synchronized String sendURL(String url) throws IOException {
        log.warn("send URL: " + url);
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

    /** 模糊查询结果的排序规则：按时间戳倒序 */
    protected final static Comparator<MavenArtifact> PATTERN_RESULT_COMPARATOR = (o1, o2) -> {
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

    /** 精确查询结果的排序规则：按版本数、最新发布时间等排序 */
    protected final static Comparator<MavenArtifact> EXTRA_RESULT_COMPARATOR = Comparator.comparing(MavenArtifact::getTimestamp).reversed();
}
