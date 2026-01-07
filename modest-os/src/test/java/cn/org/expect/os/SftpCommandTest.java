package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.ssh.SecureShellCommand;
import cn.org.expect.os.ssh.SftpCommand;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithProperties;
import cn.org.expect.util.StringUtils;
import com.jcraft.jsch.JSchException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithFeature("sftp")
@RunWithProperties(filename = "sftp", require = {"sftp.host", "sftp.port", "sftp.username", "sftp.password"})
public class SftpCommandTest {

    @EasyBean("${sftp.host}")
    private String host;

    @EasyBean("${sftp.port}")
    private int port;

    @EasyBean("${sftp.username}")
    private String username;

    @EasyBean("${sftp.password}")
    private String password;

    @Test
    public void test1() throws JSchException, IOException {
        // log.info("sftp {}@{}:{}?password={}", this.username, this.host, this.port, this.password);
        SftpCommand ftp = new SftpCommand();
        try {
            Assert.assertTrue(ftp.connect(this.host, this.port, this.username, this.password));
            FtpClientCase.run(ftp);
        } finally {
            ftp.close();
        }
    }

    @Test
    public void test2() throws JSchException, IOException {
        // log.info("ssh {}@{}:{}?password={}", this.username, this.host, this.port, this.password);
        SecureShellCommand ssh = new SecureShellCommand();
        try {
            Assert.assertTrue(ssh.connect(this.host, this.port, this.username, this.password));
            ssh.execute("pwd");
            String sout = ssh.getStdout();
            // log.info("before dir: {}", sout);
            Assert.assertTrue(StringUtils.isNotBlank(sout));

            // 运行文件测试案例
            OSFileCommand filecmd = ssh.getFileCommand();
            FtpClientCase.run(filecmd);

            ssh.execute("pwd");
            String stdout = ssh.getStdout();
            Assert.assertTrue(StringUtils.isNotBlank(stdout));
            // log.info("after dir: {}", stdout);
        } finally {
            ssh.close();
        }
    }
}
