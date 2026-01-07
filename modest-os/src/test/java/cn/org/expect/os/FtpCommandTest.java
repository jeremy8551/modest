package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.ftp.FtpCommand;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithProperties;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(ModestRunner.class)
@RunWithFeature("ftp")
@RunWithProperties(filename = "ftp", require = {"ftp.host", "ftp.port", "ftp.username", "ftp.password"})
public class FtpCommandTest {

    @EasyBean("${ftp.host}")
    private String host;

    @EasyBean("${ftp.port}")
    private int port;

    @EasyBean("${ftp.username}")
    private String username;

    @EasyBean("${ftp.password}")
    private String password;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        FtpCommand ftp = new FtpCommand();
        try {
            ftp.setContext(this.context);
            Assert.assertTrue(ftp.connect(this.host, this.port, this.username, this.password));
            ftp.enterPassiveMode(false);
            FtpClientCase.run(ftp);
        } finally {
            ftp.close();
        }
    }
}
