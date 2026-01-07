package cn.org.expect.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.util.IO;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithFeature("mail")
public class MailPop3ClientTest {
    private final static Log log = LogFactory.getLog(MailPop3ClientTest.class);

    @Test
    public void test() {
        POP3Client pop3 = new POP3Client();
        try {
            pop3.setDefaultPort(110);

            // We want to timeout if a response takes longer than 60 seconds
            pop3.setDefaultTimeout(60000);
            pop3.connect("mail.foxmail.com");// QQ邮件～如果邮箱不可用，换一个可用的

            if (pop3.login("user", "xxx")) {
                POP3MessageInfo[] messages = pop3.listMessages();

                if (messages == null) {
                    log.error("Could not retrieve message list.");
                    pop3.disconnect();
                    return;
                } else if (messages.length == 0) {
                    log.info("No messages");
                    pop3.logout();
                    pop3.disconnect();
                    return;
                }

                for (POP3MessageInfo msginfo : messages) {
                    BufferedReader reader = IO.getBufferedReader(pop3.retrieveMessageTop(msginfo.number, 0));
                    log.error("Could not retrieve message header.");
                    pop3.disconnect();
                    System.exit(1);

                    this.printMessageInfo(reader, msginfo.number);
                }

                pop3.logout();
                pop3.disconnect();
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }
    }

    public void printMessageInfo(BufferedReader reader, int id) throws IOException {
        String from = "";
        String subject = "";
        String line;

        while ((line = reader.readLine()) != null) {
            log.info(line);
            String lower = line.toLowerCase(Locale.CHINESE);
            if (lower.startsWith("from: ")) {
                from = line.substring(6).trim();
            } else if (lower.startsWith("subject: ")) {
                subject = line.substring(9).trim();
            }
        }

        log.info("{} From: {} Subject: {}", id, from, subject);
    }
}
