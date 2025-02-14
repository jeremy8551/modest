package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.os.ssh.SecureShellCommand;
import cn.org.expect.os.ssh.SftpCommand;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyRunIf;
import cn.org.expect.util.StringUtils;
import com.jcraft.jsch.JSchException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@EasyRunIf(values = {"ssh.host", "ssh.port", "ssh.username", "ssh.password", "ssh.homedir"})
public class SftpClientTest {
    private final static Log log = LogFactory.getLog(SftpClientTest.class);

    @EasyBean("${ssh.host}")
    private String host;

    @EasyBean("${ssh.port}")
    private int port;

    @EasyBean("${ssh.username}")
    private String username;

    @EasyBean("${ssh.password}")
    private String password;

    @EasyBean("${ssh.homedir}")
    private String homedir;

    @Test
    public void test1() throws JSchException, IOException {
        SftpCommand ftp = new SftpCommand();
        try {
            Assert.assertTrue(ftp.connect(host, port, username, password));
            Assert.assertTrue(ftp.cd(homedir));
            FtpClientCase.run(ftp);
        } finally {
            ftp.close();
        }
    }

    @Test
    public void test2() throws JSchException, IOException {
        SecureShellCommand ssh = new SecureShellCommand();
        try {
            Assert.assertTrue(ssh.connect(host, port, username, password));
            ssh.execute("pwd");
            String sout = ssh.getStdout();
            log.info("before dir: {}", sout);
            Assert.assertTrue(StringUtils.isNotBlank(sout));

            // 运行文件测试案例
            OSFileCommand filecmd = ssh.getFileCommand();
            Assert.assertTrue(filecmd.cd(homedir));
            FtpClientCase.run(filecmd);

            ssh.execute("pwd");
            String stdout = ssh.getStdout();
            Assert.assertTrue(StringUtils.isNotBlank(stdout));
            log.info("after dir: {}", sout);
        } finally {
            ssh.close();
        }
    }
}
