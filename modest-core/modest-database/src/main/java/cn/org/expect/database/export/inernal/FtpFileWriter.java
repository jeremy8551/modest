package cn.org.expect.database.export.inernal;

import java.io.IOException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.export.ExtractMessage;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.ioc.EasyetlContext;
import cn.org.expect.ioc.EasyetlContextAware;
import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.util.Ensure;

@EasyBean(name = "ftp", description = "卸载数据到远程ftp服务器")
public class FtpFileWriter extends SftpFileWriter implements ExtractWriter, EasyetlContextAware {

    protected EasyetlContext context;

    public void setContext(EasyetlContext context) {
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
