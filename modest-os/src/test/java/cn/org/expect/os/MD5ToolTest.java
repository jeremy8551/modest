package cn.org.expect.os;

import java.io.File;
import java.io.IOException;
import javax.script.SimpleBindings;

import cn.org.expect.crypto.MD5Encrypt;
import cn.org.expect.io.BufferedLineWriter;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class MD5ToolTest {

    @Rule
    public WithSSHRule rule = new WithSSHRule();

    /**
     * 测试字符串的md5
     */
    @Test
    public void test() {
        SimpleBindings env = rule.getEnvironment();
        String sshhost = (String) env.get("ssh.host");
        int sshport = Integer.parseInt((String) env.get("ssh.port"));
        String sshusername = (String) env.get("ssh.username");
        String sshpassword = (String) env.get("ssh.password");

        try {
            String str = "测试字符串阿斯蒂芬阿斯兰的军开发lkjsadlfsadlfj就";
            String md5 = MD5Encrypt.encrypt(str);
            System.out.println("MD5Encrypt.encrypt value is " + md5);

            OSSecureShellCommand shell = rule.getContext().getBean(OSSecureShellCommand.class);
            try {
                shell.connect(sshhost, sshport, sshusername, sshpassword);
                shell.execute("echo -n " + str + " | md5sum "); // 判断md5值与linux上是否一致

                String stdout = shell.getStdout();
                System.out.println("ssh2 stdout: " + stdout);
                String linuxMD5 = StringUtils.splitByBlank(StringUtils.trimBlank(stdout))[0];
                Assert.assertEquals(linuxMD5.toLowerCase(), md5.toLowerCase());
            } finally {
                shell.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * 对文件生成md5的功能进行测试
     */
    @Test
    public void test1() throws IOException, OSCommandException { // 对MD5功能进行测试
        File file = this.createfile();
        String md5 = MD5Encrypt.encrypt(file, null);
        System.out.println("md5: " + md5);

        SimpleBindings env = rule.getEnvironment();
        String host = (String) env.get("ssh.host");
        int port = Integer.parseInt((String) env.get("ssh.port"));
        String username = (String) env.get("ssh.username");
        String password = (String) env.get("ssh.password");
        String homedir = (String) env.get("ssh.homedir");

        OSSecureShellCommand shell = rule.getContext().getBean(OSSecureShellCommand.class);
        try {
            Assert.assertTrue(shell.connect(host, port, username, password));

            OSFileCommand filecmd = shell.getFileCommand();
            System.out.println(filecmd.pwd());
            filecmd.cd(homedir);
            filecmd.rm(homedir + "/" + file.getName());
            filecmd.upload(file, homedir);

            shell.execute("md5sum `pwd`/" + file.getName()); // 判断md5值与linux上是否一致
            String md5value = shell.getStdout();
            System.out.println("ssh2 stdout: " + md5value);
            String linuxMD5 = StringUtils.splitByBlank(StringUtils.trimBlank(md5value))[0];
            Assert.assertEquals(linuxMD5.toLowerCase(), md5.toLowerCase());
        } finally {
            shell.close();
        }
    }

    private File createfile() throws IOException {
        File parent = FileUtils.getTempDir("test", this.getClass().getSimpleName());
        File file = new File(parent, "md5testfile.txt");
        System.out.println("filepath: " + file.getAbsolutePath());

        BufferedLineWriter os = new BufferedLineWriter(file, StringUtils.CHARSET);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i <= 100; i++) {
            int column = Numbers.getRandom();
            for (int j = 1; j <= column; j++) {
                buf.append(Dates.currentTimeStamp());
                buf.append("||");
            }
            os.writeLine(buf.toString(), String.valueOf(FileUtils.lineSeparator));
        }
        os.close();
        return file;
    }
}
