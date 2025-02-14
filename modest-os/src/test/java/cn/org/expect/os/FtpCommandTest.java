package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.ftp.FtpCommand;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyRunIf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@EasyRunIf(values = {"ftp.host", "ftp.port", "ftp.username", "ftp.password", "ftp.homedir"})
public class FtpCommandTest {

    @EasyBean("${ftp.host}")
    private String host;

    @EasyBean("${ftp.port}")
    private int port;

    @EasyBean("${ftp.username}")
    private String username;

    @EasyBean("${ftp.password}")
    private String password;

    @EasyBean("${ftp.homedir}")
    private String homedir;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        FtpCommand ftp = new FtpCommand();
        try {
            ftp.setContext(this.context);
            Assert.assertTrue(ftp.connect(host, port, username, password));
            Assert.assertTrue(ftp.cd(homedir));
            FtpClientCase.run(ftp);
        } finally {
            ftp.close();
        }
    }
}
