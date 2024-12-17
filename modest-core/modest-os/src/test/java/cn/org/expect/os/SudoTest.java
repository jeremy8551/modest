package cn.org.expect.os;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunIf;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunIf(values = {"ssh.host", "ssh.port", "ssh.username", "ssh.password", "ssh.sudo.username", "ssh.sudo.password"})
public class SudoTest {

    @EasyBean("${ssh.host}")
    private String host;

    @EasyBean("${ssh.port}")
    private int port;

    @EasyBean("${ssh.username}")
    private String username;

    @EasyBean("${ssh.password}")
    private String password;

    @EasyBean("${ssh.sudo.username}")
    private String sudoUsername;

    @EasyBean("${ssh.sudo.password}")
    private String sudoPassword;

    @Test
    public void test() throws JSchException, IOException {
        String suname = "";
        String supass = "";

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setServerAliveInterval(60);
        session.setTimeout(0);
        session.connect(0);

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("whoami && su - " + suname);
        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        ((ChannelExec) channel).setErrStream(System.err);
        channel.connect();

        out.write((supass + "\n").getBytes());
        out.flush();

        out.write(("pwd && whoami && exit 0" + "\n").getBytes());
        out.flush();

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                System.out.print(new String(tmp, 0, i));
            }

            if (channel.isClosed()) {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }

        channel.disconnect();
        session.disconnect();
    }

}
