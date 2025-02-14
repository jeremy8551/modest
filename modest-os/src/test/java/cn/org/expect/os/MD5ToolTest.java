package cn.org.expect.os;

import java.io.File;
import java.io.IOException;

import cn.org.expect.crypto.MD5Encrypt;
import cn.org.expect.io.BufferedWriter;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyRunIf;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@EasyRunIf(values = {"ssh.host", "ssh.port", "ssh.username", "ssh.password", "ssh.homedir"})
public class MD5ToolTest {
    private final static Log log = LogFactory.getLog(MD5ToolTest.class);

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

    /**
     * 测试字符串的md5
     */
    @Test
    public void test() {
        String str = "测试字符串阿斯蒂芬阿斯兰的军开发lkjsadlfsadlfj就";
        String md5 = MD5Encrypt.encrypt(str);
        log.info("MD5Encrypt.encrypt value is {}", md5);

        OSSecureShellCommand shell = this.context.getBean(OSSecureShellCommand.class);
        try {
            shell.connect(host, port, username, password);
            shell.execute("echo -n " + str + " | md5sum "); // 判断md5值与linux上是否一致

            String stdout = shell.getStdout();
            log.info("ssh2 stdout: {}", stdout);
            String linuxMD5 = StringUtils.splitByBlank(StringUtils.trimBlank(stdout))[0];
            Assert.assertEquals(linuxMD5.toLowerCase(), md5.toLowerCase());
        } finally {
            shell.close();
        }
    }

    /**
     * 对文件生成md5的功能进行测试
     */
    @Test
    public void test1() throws IOException, OSCommandException { // 对MD5功能进行测试
        File file = this.createfile();
        String md5 = MD5Encrypt.encrypt(file, null);
        log.info("md5: {}", md5);

        OSSecureShellCommand shell = this.context.getBean(OSSecureShellCommand.class);
        try {
            Assert.assertTrue(shell.connect(this.host, this.port, this.username, this.password));

            OSFileCommand filecmd = shell.getFileCommand();
            log.info(filecmd.pwd());
            filecmd.cd(this.homedir);
            filecmd.rm(this.homedir + "/" + file.getName());
            filecmd.upload(file, this.homedir);

            shell.execute("md5sum `pwd`/" + file.getName()); // 判断md5值与linux上是否一致
            String md5value = shell.getStdout();
            log.info("ssh2 stdout: {}", md5value);
            String linuxMD5 = StringUtils.splitByBlank(StringUtils.trimBlank(md5value))[0];
            Assert.assertEquals(linuxMD5.toLowerCase(), md5.toLowerCase());
        } finally {
            shell.close();
        }
    }

    private File createfile() throws IOException {
        File parent = FileUtils.getTempDir("test", this.getClass().getSimpleName());
        File file = new File(parent, "md5testfile.txt");
        log.info("filepath: {}", file);

        BufferedWriter os = new BufferedWriter(file, CharsetUtils.get());
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i <= 100; i++) {
            int column = Numbers.getRandom();
            for (int j = 1; j <= column; j++) {
                buf.append(Dates.currentTimeStamp());
                buf.append("||");
            }
            os.writeLine(buf.toString(), String.valueOf(Settings.LINE_SEPARATOR));
        }
        os.close();
        return file;
    }
}
