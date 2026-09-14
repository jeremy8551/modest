package cn.org.expect.database.internal;

import cn.org.expect.os.OSAccount;

/**
 * 封装数据库所在操作系统的账户信息
 */
public class StandardOSAccount implements OSAccount {

    private String username;

    private String password;

    private boolean admin;

    /**
     * 初始化 StandardOSAccount
     */
    public StandardOSAccount() {
        super();
    }

    /**
     * 初始化 StandardOSAccount
     */
    public StandardOSAccount(String username, String password, boolean admin) {
        this();
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** {@inheritDoc} */
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /** {@inheritDoc} */
    public OSAccount clone() {
        StandardOSAccount obj = new StandardOSAccount();
        obj.username = this.username;
        obj.password = this.password;
        obj.admin = this.admin;
        return obj;
    }

    /** {@inheritDoc} */
    public String toString() {
        return this.username;
    }
}
