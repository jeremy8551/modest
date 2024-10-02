package cn.org.expect.os;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

public class SftpServer {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost("127.0.0.1");
        sshd.setPort(2228);

//        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("home.key").toPath()));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setPasswordAuthenticator((username, password, session) -> username.equals("user") && password.equals("password"));
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
//        sshd.setShellFactory((c) -> {
//            ServerSession session = c.getSession();
//            String username = session.getUsername();
//            //除了usera以外的的用户都不允许远程登录
//            if ("user".equals(username)) {
//                return InteractiveProcessShellFactory.INSTANCE.createShell(c);
//            } else {
//                return null;
//            }
//        });

        sshd.setCommandFactory(new ProcessShellCommandFactory());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));

        sshd.start();
        System.out.println("SSH server started use port: " + sshd.getPort());

        Thread.sleep(1000 * 600);
        // 停止服务器
        sshd.stop(true);
    }

}
