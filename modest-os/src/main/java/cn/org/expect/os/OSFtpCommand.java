package cn.org.expect.os;

import java.io.IOException;

/**
 * FTP 协议接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2018-08-10
 */
public interface OSFtpCommand extends OSConnectCommand, OSFileCommand {

    /**
     * 进入被动模式
     *
     * @param remotePassive true表示远程被动模式 false开启标准正常模式
     * @throws IOException 访问文件错误
     */
    void enterPassiveMode(boolean remotePassive) throws IOException;

    /**
     * 终止正在进行的传输
     */
    void terminate() throws Exception;
}
