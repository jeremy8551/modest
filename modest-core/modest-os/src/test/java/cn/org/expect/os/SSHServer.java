package cn.org.expect.os;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import cn.org.expect.util.Settings;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

public class SSHServer {

    public static void main(String[] args) {
        //创建SshServer对象
        SshServer sshd = SshServer.setUpDefaultServer();
        //配置端口
        sshd.setPort(2229);
        //设置默认的签名文件，如过文件不存在会创建
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("test.key")));
        //设置用户名和密码进行登录验证
        sshd.setPasswordAuthenticator((username, password, serverSession) -> {
            //假定用户名：usera 密码：apass，
            //这里可以增加逻辑从数据库或其他方式校验用户名和密码
            //返回true则校验成功否则失败
            return "user".equals(username) && "password".equals(password);
        });
        //设置sftp子系统
        sshd.setSubsystemFactories(Arrays.asList(new SftpSubsystemFactory()));
        //设置sfp默认的访问目录
        Path dir = Paths.get(Settings.getUserHome().getAbsolutePath());
        //给每个用户分配不同的访问目录
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(dir.toAbsolutePath()) {
            @Override
            public Path getUserHomeDir(SessionContext session) throws IOException {
                String username = session.getUsername();
                Path homeDir = getUserHomeDir(username);
                if (homeDir == null) {
                    //这里给每个用户修改为默认目录+用户名+dir的目录格式
                    //可以根据实际的需求修改此处的代码
                    homeDir = getDefaultHomeDir().resolve(username + "dir");
                    setUserHomeDir(username, homeDir);
                }
                return homeDir;
            }
        });
        //设置ssh的shell环境
        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);

        System.out.println("start SSH Server port: " + sshd.getPort());

        //启动ssh服务
        try {
            sshd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保持java进程不关闭
        Object obj = new Object();
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
