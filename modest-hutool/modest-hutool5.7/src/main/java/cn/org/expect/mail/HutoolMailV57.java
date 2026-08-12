package cn.org.expect.mail;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

/**
 * Hutool 5.7 邮件工具实现
 */
public class HutoolMailV57 {

    /** 邮件服务器地址 */
    private String host;

    /** 邮件服务器用户名 */
    private String username;

    /** 邮件服务器密码 */
    private String password;

    /** 邮件字符集 */
    private String charsetName = "UTF-8";

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getCharsetName() {
        return this.charsetName;
    }

    public void setCharsetName(String charsetName) {
        if (charsetName != null && charsetName.length() > 0) {
            this.charsetName = charsetName;
        }
    }

    public String send(String protocol, int port, boolean ssl, String sender, List<String> receivers, String title, CharSequence content, File... attachments) {
        MailAccount account = this.createAccount(port, ssl, sender);
        return MailUtil.send(account, receivers, title, content.toString(), false, attachments);
    }

    public List<?> receive(String protocol, int port, boolean ssl, String folderName) {
        throw new UnsupportedOperationException("Hutool 5.7 does not provide a mail receiving API");
    }

    /**
     * 创建 Hutool 邮件账户
     *
     * @param port   邮件服务器端口
     * @param ssl    是否启用 SSL
     * @param sender 发件人
     * @return Hutool 邮件账户
     */
    private MailAccount createAccount(int port, boolean ssl, String sender) {
        MailAccount account = new MailAccount();
        account.setHost(this.host);
        account.setUser(this.username);
        account.setPass(this.password);
        account.setFrom(sender);
        account.setAuth(Boolean.TRUE);
        account.setSslEnable(Boolean.valueOf(ssl));
        account.setCharset(Charset.forName(this.charsetName));
        if (port > 0) {
            account.setPort(Integer.valueOf(port));
        }
        return account;
    }
}
