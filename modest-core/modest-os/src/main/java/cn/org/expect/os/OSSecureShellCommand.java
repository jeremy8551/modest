package cn.org.expect.os;

public interface OSSecureShellCommand extends OSShellCommand {

    /**
     * 返回一个 sftp 接口功能
     *
     * @return 文件操作接口
     */
    OSFileCommand getFileCommand();

    /**
     * 在本地建立一个端口转发隧道
     *
     * @param localPort  本地端口号，如果为0表示由操作系统自动分配端口号
     * @param remoteHost 目标服务器的 Host 或 ip
     * @param remotePort 目标服务器的 ssh 端口号
     * @return 端口号
     */
    int localPortForward(int localPort, String remoteHost, int remotePort);

}
