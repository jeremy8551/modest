package cn.org.expect.os;

/**
 * 此接口用于描述远程连接功能 <br>
 * 操作系统可以是本地操作系统，也可以是远程linux，windows，unix，macos
 *
 * @author jeremy8551@gmail.com
 * @createtime 2020-09-06
 */
public interface OSConnectCommand {

    String HOST = "host";

    String PORT = "port";

    String USERNAME = "username";

    String PASSWORD = "password";

    /**
     * 执行登录认证
     *
     * @param host     服务的 host 或 ip
     * @param port     服务的端口号
     * @param username 用户名
     * @param password 登录密码
     * @return 返回true表示成功登录 返回false表示登录失败
     */
    boolean connect(String host, int port, String username, String password);

    /**
     * 判断是否已登录
     *
     * @return 返回true表示已登录成功 返回false表示还未登录
     */
    boolean isConnected();

    /**
     * 退出登录
     */
    void close();
}
