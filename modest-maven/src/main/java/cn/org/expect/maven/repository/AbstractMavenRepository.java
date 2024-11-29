package cn.org.expect.maven.repository;

import java.io.IOException;
import java.util.Comparator;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.impl.SimpleMavenRepositoryDatabase;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractMavenRepository implements MavenRepository {
    protected final static Log log = LogFactory.getLog(AbstractMavenRepository.class);

    /** Http请求 */
    protected volatile Call call;

    /** 任务终止标志 */
    protected volatile boolean terminate;

    /** 数据库接口 */
    protected MavenRepositoryDatabase database;

    public AbstractMavenRepository(EasyContext ioc) {
        this.terminate = false;
        this.database = new SimpleMavenRepositoryDatabase(this.getClass(), ioc);
    }

    public MavenRepositoryDatabase getDatabase() {
        return this.database;
    }

    public String sendRequest(String url) {
        Throwable throwable = null;
        int times = 3;
        for (int i = 0; i < times; i++) {
            if (this.terminate) {
                break;
            }

            try {
                return this.sendURL(url);
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }

        if (this.terminate) {
            return null;
        } else {
            throw new RuntimeException("try " + times + " times send request, but fail!", throwable);
        }
    }

    public synchronized String sendURL(String url) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("send URL: {}", url);
        }

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

    public boolean isTerminate() {
        return this.terminate;
    }

    public void terminate() {
        if (log.isDebugEnabled()) {
            log.debug("{} terminated!", this.getClass().getSimpleName());
        }

        this.terminate = true;
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

        int tv = MavenArtifact.TIMESTAMP_COMPARATOR.compare(o1.getTimestamp(), o2.getTimestamp()); // 最新发布
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
    protected final static Comparator<MavenArtifact> EXTRA_RESULT_COMPARATOR = (m1, m2) -> -MavenArtifact.TIMESTAMP_COMPARATOR.compare(m1.getTimestamp(), m2.getTimestamp());
}
