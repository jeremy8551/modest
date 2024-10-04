package cn.org.expect.mail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunIf;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunIf(values = {"mail.host", "mail.username", "mail.password", "mail.charset"})
public class ApacheEmailCommandTest {

    @EasyBean
    private EasyContext context;

    @EasyBean
    private Properties properties;

    @Test
    public void test1() throws IOException {
        ApacheEmailCommand cmd = new ApacheEmailCommand();
        cmd.setHost(properties.getProperty("mail.host"));
        cmd.setUser(properties.getProperty("mail.username"), properties.getProperty("mail.password"));
        cmd.setCharsetName(properties.getProperty("mail.charset"));

        StringBuilder msg = new StringBuilder();
        msg.append("测试单统计信息如下：").append("\r\n\t");
        msg.append("1、测试单数量: 4").append("\r\n\t");
        msg.append("2、测试单数量：5").append("\r\n\t");
        msg.append("3、测试单成功数量：6").append("\r\n\t");
        msg.append("统计时间：1 ").append(Dates.currentTimeStamp());

        File txt = FileUtils.getTempDir("test", ApacheEmailCommandTest.class.getSimpleName(), "test测试.txt");
        FileUtils.write(txt, StringUtils.CHARSET, false, msg);

        File dir = FileUtils.getTempDir("test", ApacheEmailCommandTest.class.getSimpleName(), "目录12test");
        Assert.assertTrue(FileUtils.createDirectory(dir));
        File f1 = new File(dir, "test测试.txt");
        FileUtils.write(f1, StringUtils.CHARSET, false, msg);

        File txt1 = FileUtils.getTempDir("test", ApacheEmailCommandTest.class.getSimpleName(), "testsetest.txt");
        FileUtils.write(txt1, StringUtils.CHARSET, false, msg);

        cmd.drafts("imap", 0, "etl@foxmail.com", ArrayUtils.asList("410336929@qq.com"), "测试单统计信息-" + Dates.currentTimeStamp(), msg, new MailFile(context, txt1, "tt1.txt", "file"));
    }

    @Test
    public void test3() throws IOException {
        ApacheEmailCommand cmd = new ApacheEmailCommand();
        cmd.setHost(properties.getProperty("mail.host"));
        cmd.setUser(properties.getProperty("mail.username"), properties.getProperty("mail.password"));
        cmd.setCharsetName(properties.getProperty("mail.charset"));

        StringBuilder msg = new StringBuilder();
        msg.append("测试单统计信息如下：").append("\r\n\t");
        msg.append("1、测试单数量: 4").append("\r\n\t");
        msg.append("2、测试单数量：5").append("\r\n\t");
        msg.append("3、测试单成功数量：6").append("\r\n\t");
        msg.append("统计时间：1 ").append(Dates.currentTimeStamp());

        File txt = FileUtils.getTempDir("test", ApacheEmailCommandTest.class.getSimpleName(), "test测试.txt");
        FileUtils.write(txt, StringUtils.CHARSET, false, msg);

        File dir = FileUtils.getTempDir("test", ApacheEmailCommandTest.class.getSimpleName(), "目录12test");
        Assert.assertTrue(FileUtils.createDirectory(dir));
        File f1 = new File(dir, "test测试.txt");
        FileUtils.write(f1, StringUtils.CHARSET, false, msg);

        cmd.send(null, 0, true, "测试 etl@foxmail.com", ArrayUtils.asList("410336929@qq.com"), "测试单统计信息-" + Dates.currentTimeStamp(), msg, new MailFile(context, txt, "tt1.txt", "file"), new MailFile(context, txt, "tttt.txt", "file"), new MailFile(context, txt), new MailFile(context, dir));
    }

    @Test
    public void test2() {
        ApacheEmailCommand cmd = new ApacheEmailCommand();
        cmd.setHost(properties.getProperty("mail.host"));
        cmd.setUser(properties.getProperty("mail.username"), properties.getProperty("mail.password"));
        cmd.setCharsetName(properties.getProperty("mail.charset"));

        List<Mail> it = cmd.search(null, 0, true, null, null);
        for (Mail mail : it) {
            System.out.println(mail);
            List<MailAttachment> attachments = mail.getAttachments();
            for (MailAttachment ma : attachments) {
                System.out.print("下载附件 " + ma.getName());
                File file = cmd.download(ma, null);
                if (file == null) {
                    System.out.println(" 失败！");
                } else {
                    System.out.println(" " + file.getAbsolutePath());
                }
            }
            System.out.println();
            System.out.println();
        }
    }
}
