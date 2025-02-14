package cn.org.expect.os;

import java.util.Iterator;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyRunIf;
import cn.org.expect.util.Settings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@EasyRunIf(values = {"ssh.host", "ssh.port", "ssh.username", "ssh.password", "ssh.homedir"})
public class OSTest {
    private final static Log log = LogFactory.getLog(OSTest.class);

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

    @EasyBean
    private EasyContext context;

    @Test
    public void test() {
        log.info("{}@{}:{}?password={}", username, host, port, password);

        OS os = context.getBean(OS.class, host, port, username, password);
        this.testOSCommand(os);

        boolean exists = false;
        for (Iterator<OSNetworkCard> it = os.getOSNetwork().getOSNetworkCards().iterator(); it.hasNext(); ) {
            OSNetworkCard card = it.next();
            log.info(card.getIPAddress());
            if (host.equals(card.getIPAddress())) {
                exists = true;
                break;
            }
        }
        Assert.assertTrue(exists);

        this.testOSCommand(context.getBean(OS.class, host, username, password));
        this.testOSCommand(context.getBean(OS.class, Settings.getUserName()));
        this.testOSCommand(context.getBean(OS.class));
    }

    private void testOSCommand(OS os) {
        try {
            Assert.assertTrue(os.supportOSCommand());
            Assert.assertTrue(os.supportOSFileCommand());
            Assert.assertTrue(os.enableOSCommand());
            Assert.assertTrue(os.enableOSFileCommand());
        } finally {
            os.close();
        }
    }
}
