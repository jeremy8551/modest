package cn.org.expect.os;

import java.util.Iterator;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithProperties;
import cn.org.expect.util.Settings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithFeature("ssh")
@RunWithProperties(filename = "ssh", require = {"ssh.host", "ssh.port", "ssh.username", "ssh.password"})
public class OSTest {

    @EasyBean("${ssh.host}")
    private String host;

    @EasyBean("${ssh.port}")
    private int port;

    @EasyBean("${ssh.username}")
    private String username;

    @EasyBean("${ssh.password}")
    private String password;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() {
        // log.info("{}@{}:{}?password={}", this.username, this.host, this.port, this.password);

        OS os = this.context.getBean(OS.class, this.host, this.port, this.username, this.password);
        this.testOSCommand(os);

        int count = 0;
        boolean exists = false;
        for (Iterator<OSNetworkCard> it = os.getOSNetwork().getOSNetworkCards().iterator(); it.hasNext(); count++) {
            OSNetworkCard card = it.next();
            // log.info(card.getIPAddress());
            if (this.host.equals(card.getIPAddress())) {
                exists = true;
                break;
            }
        }

        if (count > 0) {
            Assert.assertTrue(exists);
        }

        this.testOSCommand(this.context.getBean(OS.class, this.host, this.port, this.username, this.password));
        this.testOSCommand(this.context.getBean(OS.class, Settings.getUserName()));
        this.testOSCommand(this.context.getBean(OS.class));
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
