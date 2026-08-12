package cn.org.expect.mail;

import java.io.File;
import java.util.List;

import cn.org.expect.util.CharsetName;

/**
 * 邮件收发接口
 */
public interface Mail extends CharsetName {

    /**
     * 设置邮件服务器地址
     *
     * @param host 邮件服务器地址
     */
    void setHost(String host);

    /**
     * 设置邮件服务器认证信息
     *
     * @param username 用户名
     * @param password 密码
     */
    void setUser(String username, String password);

    /**
     * 发送邮件
     *
     * @param protocol    邮件协议
     * @param port        邮件服务器端口
     * @param ssl         是否启用 SSL
     * @param sender      发件人
     * @param receivers   收件人
     * @param title       邮件标题
     * @param content     邮件正文
     * @param attachments 附件
     * @return 邮件编号
     */
    String send(String protocol, int port, boolean ssl, String sender, List<String> receivers, String title, CharSequence content, File... attachments);

    /**
     * 接收邮件
     *
     * @param protocol   邮件协议
     * @param port       邮件服务器端口
     * @param ssl        是否启用 SSL
     * @param folderName 邮件目录
     * @return 邮件集合
     */
    List<?> receive(String protocol, int port, boolean ssl, String folderName);
}
