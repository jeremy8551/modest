package cn.org.expect.script.method;

import java.io.IOException;
import java.net.UnknownHostException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient extends Terminator {
    protected final static Log log = LogFactory.getLog(HttpClient.class);

    /** Http请求 */
    private volatile Call call;

    /** 请求地址 */
    protected String httpUrl;

    /**
     * 发送 Http 请求
     *
     * @param url   HTTP请求地址
     * @param array HTTP请求地址的参数
     * @return 响应结果，返回null表示失败!
     * @throws UnknownHostException 不能访问域名
     */
    public synchronized String sendRequest(String url, Object... array) throws UnknownHostException {
        this.httpUrl = StringUtils.replaceIndexHolder(url, array);

        Throwable throwable = null;
        int times = 3;
        for (int i = 0; i < times; i++) {
            if (this.terminate) {
                break;
            }

            try {
                String result = this.sendURL(this.httpUrl);
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
            throw new RuntimeException(this.httpUrl, throwable);
        }
    }

    protected String sendURL(String url) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} send URL: {}", this.getClass().getSimpleName(), url);
        }

        try {
            return this.sendHttp(url);
        } finally {
            this.call = null;
        }
    }

    protected String sendHttp(String url) throws IOException {
        OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build(); // 创建 Request 实例
        Call call = client.newCall(request);
        this.call = call;
        Response response = call.execute(); // 发送请求并获取响应
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void terminate() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} terminated!", this.getClass().getSimpleName());
        }

        super.terminate();
        if (this.call != null) {
            this.call.cancel();
        }
    }
}
