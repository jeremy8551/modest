package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.linux.LinuxLocalOS;
import cn.org.expect.os.linux.LinuxRemoteOS;
import cn.org.expect.os.macos.MacOS;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.NetUtils;
import cn.org.expect.util.OSUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

@EasyBean
public class OSFactory implements EasyBeanFactory<OS> {

    /**
     * 返回操作系统接口
     *
     * @param context 容器上下文信息
     * @param args    登录参数，格式如下: <br>
     *                localhost <br>
     *                127.0.0.1 <br>
     *                localhost 22 username password <br>
     *                localhost username password <br>
     *                127.0.0.1 22 username password <br>
     *                192.168.1.2 username password <br>
     *                192.168.1.2 username <br>
     *                <br>
     *                参数为空时，返回本地操作系统的接口
     * @return 实例对象
     */
    public OS build(EasyContext context, Object... args) throws Exception {
        String host = null, username = null, password = null; // 服务器host 用户名 密码
        int port = -1; // 访问端口号

        for (Object obj : args) {
            if (obj instanceof Integer) { // 识别端口号
                port = (Integer) obj;
                continue;
            } else if (obj instanceof CharSequence) {
                String str = StringUtils.trimBlank(obj.toString());
                if (NetUtils.isHost(str) && host == null) { // 识别主机名
                    host = str;
                } else if (port == -1 && NetUtils.isPort(str)) { // 识别端口号
                    port = Integer.parseInt(str);
                } else if (username == null) { // 识别用户名
                    username = str;
                } else { // 识别登录密码
                    password = str;
                }
            } else if (obj instanceof OSConfiguration) {
                OSConfiguration config = (OSConfiguration) obj;
                if (port == -1) {
                    port = config.getSSHPort();
                }

                // 数据库服务器地址
                if (StringUtils.isNotBlank(config.getHostname())) {
                    host = config.getHostname();
                }

                // SSH 协议账号
                OSAccount ssh = config.getSSHAccount();
                if (ssh != null) {
                    username = ssh.getUsername();
                    password = ssh.getPassword();
                    continue;
                }

                // 默认使用最高账号登录
                OSAccount acct = CollectionUtils.first(config.getAccounts());
                if (acct != null) {
                    username = acct.getUsername();
                    password = acct.getPassword();
                    continue;
                }
            } else if (obj instanceof OSAccount) {
                OSAccount acct = (OSAccount) obj;
                username = acct.getUsername();
                password = acct.getPassword();
            }
        }

        if (username != null && password == null) { // 未设置登录密码时，默认使用用户名作为密码
            password = username;
        }

        if (port == -1) { // 使用ssh端口作为默认端口
            port = 22;
        }

        OS os = this.build(context, host, port, username, password);
        if (os instanceof EasyContextAware) {
            ((EasyContextAware) os).setContext(context);
        }
        return os;
    }

    /**
     * 创建操作系统接口
     *
     * @param context  容器上下文信息
     * @param host     操作系统host
     * @param port     访问端口
     * @param username 登录用户名
     * @param password 登录密码
     * @return 实例对象
     * @throws IOException 文件错误
     */
    private OS build(EasyContext context, String host, int port, String username, String password) throws IOException {
        if ((StringUtils.isBlank(host) || NetUtils.isLocalHost(host)) && (username == null || Settings.getUserName().equalsIgnoreCase(username))) {
            if (OSUtils.isLinux()) {
                return new LinuxLocalOS();
            } else if (OSUtils.isMacOsX()) {
                return new MacOS();
            } else {
                throw new UnsupportedOperationException(OSUtils.getName());
            }
        } else {
            return new LinuxRemoteOS(context, host, port, username, password);
        }
    }
}
