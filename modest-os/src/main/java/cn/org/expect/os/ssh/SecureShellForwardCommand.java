package cn.org.expect.os.ssh;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.os.OSConnectCommand;
import cn.org.expect.os.OSException;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;

/**
 * SSH 端口转发协议的实现类
 *
 * @author jeremy8551@qq.com
 * @createtime 2018-08-10
 */
public class SecureShellForwardCommand implements OSConnectCommand {
    private final static Log log = LogFactory.getLog(SecureShellForwardCommand.class);

    /** 超时时间，单位秒 */
    public static int CLOSE_PORTFORWARDLOCAL_TIMEOUT = 120;

    /** JSch 组件 */
    protected JSch jsch = new JSch();

    /** ssh connection transaction */
    protected Session session;

    /** 代理服务器配置 */
    protected String proxySSHHost;
    protected int proxySSHPort;
    protected String proxySSHUsername;
    protected String proxySSHPassword;

    /** 分配的本地端口 */
    protected int localport;

    protected String charsetName;
    protected OutputStream stdout;
    protected OutputStream stderr;

    /**
     * 初始化
     */
    public SecureShellForwardCommand() {
    }

    /**
     * 设置错误信息输出接口
     *
     * @param out 错误信息输出接口
     */
    public void setStderr(OutputStream out) {
        this.stderr = out;
    }

    /**
     * 设置标准信息输出接口
     *
     * @param out 标准信息输出接口
     */
    public void setStdout(OutputStream out) {
        this.stdout = out;
    }

    /**
     * 设置输出字符串的字符集编码
     *
     * @param charsetName 字符集编码
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * 输出标准信息
     *
     * @param str 字符串
     * @throws IOException 向输出流写入数据发生错误
     */
    public void stdout(String str) throws IOException {
        if (this.stdout == null) {
            log.info(str);
        } else {
            this.stdout.write(StringUtils.toBytes(str, this.charsetName));
            this.stdout.flush();
        }
    }

    /**
     * 输出错误信息
     *
     * @param str 字符串
     * @param o   异常信息
     */
    public void stderr(String str, Throwable o) {
        if (this.stderr == null) {
            if (log.isErrorEnabled()) {
                log.error(str);
            }
        } else {
            try {
                this.stderr.write(StringUtils.toBytes(str, this.charsetName));
                if (o != null) {
                    this.stderr.write(StringUtils.toBytes(StringUtils.toString(o), this.charsetName));
                }
                this.stderr.flush();
            } catch (Throwable e) {
                throw new OSException(str, e);
            }
        }
    }

    public boolean connect(String proxyHost, int proxySSHPort, String proxySSHUsername, String proxySSHPassword) {
        if (log.isDebugEnabled()) {
            log.debug("ssh " + proxySSHUsername + "@" + proxyHost + ":" + proxySSHPort + "?password=" + proxySSHPassword);
        }

        try {
            if (this.session != null && this.session.isConnected()) {
                this.close();
            }

            this.session = this.jsch.getSession(proxySSHUsername, proxyHost, proxySSHPort);
            this.session.setPassword(proxySSHPassword);

            // Set the prompt when logging in for the first time, optional value: (ask | yes | no)
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();

            this.stdout(this.session.getServerVersion());

            this.proxySSHHost = proxyHost;
            this.proxySSHPort = proxySSHPort;
            this.proxySSHUsername = proxySSHUsername;
            this.proxySSHPassword = proxySSHPassword;
            return true;
        } catch (Throwable e) {
            this.stderr("ssh " + proxySSHUsername + "@" + proxyHost + ":" + proxySSHPort + "?password=" + proxySSHPassword + " fail!", e);
            return false;
        }
    }

    /**
     * 使用本地端口建立转发隧道
     *
     * @param localPort  本地端口
     * @param remoteHost 目标服务器
     * @param remotePort 目标服务器的SSH端口
     * @return 端口号
     */
    public int localPortForward(int localPort, String remoteHost, int remotePort) {
        Ensure.notBlank(remoteHost);
        Ensure.fromOne(remotePort);

        if (this.session == null || !this.session.isConnected()) {
            throw new OSException("use connect() establish an connection first!");
        }

        if (this.localport > 0) {
            try {
                this.session.delPortForwardingL(this.localport);
            } catch (Throwable e) {
                this.stderr("delete Port Forward Local fail!", e);
            }
        }

        if (localPort <= 0) {
            ServerSocket server = null;
            try {
                server = new ServerSocket(0);
                localPort = server.getLocalPort();
            } catch (Throwable e) {
                throw new OSException("get localhost port fail!", e);
            } finally {
                IO.closeQuiet(server);
                IO.closeQuiet(server);
                IO.closeQuiet(server);
            }
        }

        try {
            // register local port forward
            int port = this.session.setPortForwardingL(localPort, remoteHost, remotePort);
            this.localport = port;

            // remote port forward
            // assinged_port = session.setPortForwardingR(990, "", 990);
            // delete forward port
            // session.delPortForwardingL(localPort);
            // session.disconnect();

            String localServer = String.valueOf(port);
            String proxyServer = this.proxySSHUsername + "@" + this.proxySSHHost + ":" + proxySSHPort;
            String remotServer = remoteHost + ":" + remotePort;
            this.stdout(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg016", localServer, proxyServer, remotServer));
            return port;
        } catch (Throwable e) {
            this.stderr("establishing an SSH tunnel fail!", e);
            this.localport = -1;
            return -1;
        }
    }

    public void close() {
        if (this.session != null) {
            TimeWatch watch = new TimeWatch();
            while (true) {
                try {
                    if (this.session != null) {
                        this.session.disconnect(); // disconnect ssh server
                        this.session = null;
                        break;
                    }
                } catch (Throwable e) {
                    log.error("shutdown port forward local error!", e);
                    if (watch.useSeconds() <= SecureShellForwardCommand.CLOSE_PORTFORWARDLOCAL_TIMEOUT) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public boolean isConnected() {
        return this.session != null && this.session.isConnected();
    }

}
