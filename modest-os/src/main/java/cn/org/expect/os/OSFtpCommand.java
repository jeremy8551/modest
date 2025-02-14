package cn.org.expect.os;

/**
 * FTP 协议接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2018-08-10
 */
public interface OSFtpCommand extends OSConnectCommand, OSFileCommand {

    /**
     * 终止正在进行的传输
     */
    void terminate() throws Exception;
}
