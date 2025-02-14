package cn.org.expect.os;

import cn.org.expect.util.CharsetName;

/**
 * Shell命令接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-12 04:47:54
 */
public interface OSShellCommand extends OSConnectCommand, OSCommand, CharsetName {

    /** Shell配置 */
    String PROFILES = "profiles";
    String SSH_HOST = "ssh.host";
    String SSH_PORT = "ssh.port";
    String SSH_USERNAME = "ssh.username";
    String SSH_PASSWORD = "ssh.password";
}
