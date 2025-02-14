package cn.org.expect.database.export.inernal;

import java.io.IOException;

import cn.org.expect.database.export.ExtractMessage;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.util.Ensure;

@EasyBean(value = "ftp", description = "卸载数据到远程ftp服务器\nftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径")
public class FtpFileWriter extends SftpFileWriter implements ExtractWriter, EasyContextAware {

    protected EasyContext context;

    public void setContext(EasyContext context) {
        this.context = context;
    }

    public FtpFileWriter(ExtracterContext context, ExtractMessage message, String host, String port, String username, String password, String remotepath) throws IOException {
        super(context, message, host, port, username, password, remotepath);
    }

    protected void open(String host, String port, String username, String password, String remotepath) {
        this.ftp = this.context.getBean(OSFtpCommand.class, "ftp");
        Ensure.isTrue(this.ftp.connect(host, Integer.parseInt(port), username, password), host, port, username, password);
        this.target = "ftp://" + username + "@" + host + ":" + port + "?password=" + password;
    }
}
