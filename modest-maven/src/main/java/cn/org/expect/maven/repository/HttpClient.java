package cn.org.expect.maven.repository;

import java.net.UnknownHostException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    protected final Log log = LogFactory.getLog(this.getClass());

    /** Http请求 */
    protected volatile Call call;

    /** 任务终止标志 */
    protected volatile boolean terminate;

    public HttpClient() {
        this.terminate = false;
    }

    public synchronized String sendRequest(String url) throws UnknownHostException {
        Throwable throwable = null;
        int times = 3;
        for (int i = 0; i < times; i++) {
            if (this.terminate) {
                break;
            }

            try {
                return this.sendURL(url);
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
            throw new RuntimeException("try " + times + " times send request, but fail!", throwable);
        }
    }

    private String sendURL(String url) throws Exception {
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
