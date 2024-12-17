package cn.org.expect.os;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunIf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TELNET 协议操作接口
 *
 * @author jeremy8551@qq.com
 * @createtime 2022-01-13
 */
@RunWith(ModestRunner.class)
@RunIf(values = {"telnet.host", "telnet.port", "telnet.username", "telnet.password"})
public class TelnetCommandTest {

    @EasyBean("${telnet.host}")
    private String host;

    @EasyBean("${telnet.port}")
    private int port;

    @EasyBean("${telnet.username}")
    private String username;

    @EasyBean("${telnet.password}")
    private String password;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws Exception {
        OSShellCommand telnet = this.context.getBean(OSShellCommand.class, "telnet");
        try {
            Assert.assertTrue(telnet.connect(host, port, username, password));

            telnet.execute("pwd", 2000);
            System.out.println(telnet.getStdout());

            telnet.execute("ls -la", 2000);
            System.out.println(telnet.getStdout());
        } finally {
            telnet.close();
        }
    }
}
