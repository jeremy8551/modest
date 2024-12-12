package cn.org.expect.maven.repository;

import java.net.UnknownHostException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenRuntimeException;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    protected final static Log log = LogFactory.getLog(HttpClient.class);

    /** Http请求 */
    protected volatile Call call;

    /** 任务终止标志 */
    protected volatile boolean terminate;

    public HttpClient() {
        this.terminate = false;
    }

    /**
     * 发送 Http 请求
     *
     * @param url Http请求地址
     * @return 响应结果，返回null表示失败!
     * @throws UnknownHostException 不能访问域名
     */
    public synchronized String sendRequest(String url) throws UnknownHostException {
        Throwable throwable = null;
        int times = 3;
        for (int i = 0; i < times; i++) {
            if (this.terminate) {
                break;
            }

            try {
                String result = this.sendURL(url);
                return StringUtils.isBlank(result) ? null : result;
            } catch (Throwable e) {
                UnknownHostException cause = ClassUtils.getCause(e, UnknownHostException.class);
                if (cause != null) {
                    throw cause;
                }

                if (throwable == null) {
                    throwable = e;
                }
            }
        }

        if (this.terminate) {
            return null;
        } else {
            throw new MavenRuntimeException(throwable, "maven.search.error.cannot.send.request.url", url);
        }
    }

    protected String sendURL(String url) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} send URL: {}", this.getClass().getSimpleName(), url);
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
}
