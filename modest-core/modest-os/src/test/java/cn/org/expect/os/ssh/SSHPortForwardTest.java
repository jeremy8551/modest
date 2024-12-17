package cn.org.expect.os.ssh;

import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.os.OSFile;
import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.os.OSSecureShellCommand;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunIf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 端口转发测试，本地连接到一个转发服务器，通过转发服务器连接一个sftp服务器
 */
@RunWith(ModestRunner.class)
@RunIf(values = {"forward.host", "forward.port", "forward.username", "forward.password", "forward.remote.host", "forward.remote.port", "forward.remote.username", "forward.remote.password"})
public class SSHPortForwardTest {

    /**
     * 转发服务器 host
     */
    @EasyBean("${forward.host}")
    private String host;

    @EasyBean("${forward.port}")
    private int port;

    @EasyBean("${forward.username}")
    private String username;

    @EasyBean("${forward.password}")
    private String password;

    /**
     * 目标服务器 host、端口、用户、密码
     */
    @EasyBean("${forward.remote.host}")
    private String remoteHost;

    @EasyBean("${forward.remote.port}")
    private int remotePort;

    @EasyBean("${forward.remote.username}")
    private String remoteUsername;

    @EasyBean("${forward.remote.password}")
    private String remotePassword;

    @EasyBean
    private EasyContext context;

    /** 本地服务器host与端口 */
    private String localHost = "127.0.0.1";

    /** 本地端口 */
    private int localPort = 0;

    @Test
    public void test() throws Exception {
        OSSecureShellCommand ssh = this.context.getBean(OSSecureShellCommand.class);
        try {
            Assert.assertTrue(ssh.connect(this.host, this.port, this.username, this.password));
            this.localPort = ssh.localPortForward(0, this.remoteHost, this.remotePort);
            this.sftp();
        } finally {
            ssh.close();
        }
    }

    public void sftp() throws Exception {
        OSFtpCommand sftp = this.context.getBean(OSFtpCommand.class, "sftp");
        try {
            Assert.assertTrue(sftp.connect(this.localHost, this.localPort, this.remoteUsername, this.remotePassword));
            System.out.println("pwd " + sftp.pwd());
            List<OSFile> list = sftp.ls("/");
            for (OSFile file : list) {
                System.out.println(file.getLongname());
            }
        } finally {
            sftp.close();
        }
    }
}
