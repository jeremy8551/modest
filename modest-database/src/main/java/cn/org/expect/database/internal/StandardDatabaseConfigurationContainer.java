package cn.org.expect.database.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.database.DatabaseConfiguration;
import cn.org.expect.database.DatabaseConfigurationContainer;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.Jdbc;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.os.OSAccount;
import cn.org.expect.os.OSConnectCommand;
import cn.org.expect.os.OSShellCommand;
import cn.org.expect.util.Ensure;

/**
 * 数据库连接信息集合
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean(singleton = true)
public class StandardDatabaseConfigurationContainer implements DatabaseConfigurationContainer {

    private CaseSensitivMap<DatabaseConfiguration> map;

    private EasyContext context;

    public StandardDatabaseConfigurationContainer(EasyContext context) {
        this.map = new CaseSensitivMap<DatabaseConfiguration>();
        this.context = Ensure.notNull(context);
    }

    public DatabaseConfiguration add(Properties p) {
        String host = p.getProperty(OSConnectCommand.HOST);
        String driverClassName = p.getProperty(Jdbc.DRIVER_CLASS_NAME);
        String url = p.getProperty(Jdbc.URL);
        String username = p.getProperty(OSConnectCommand.USERNAME); // 数据库用户
        String password = p.getProperty(OSConnectCommand.PASSWORD);
        String adminUsername = p.getProperty(Jdbc.ADMIN_USERNAME); // 数据库管理员账号
        String adminPassword = p.getProperty(Jdbc.ADMIN_PASSWORD); // 数据库管理员密码
        String sshUser = p.getProperty(OSShellCommand.SSH_USERNAME); // 数据库所在服务器的ssh用户
        String sshUserPw = p.getProperty(OSShellCommand.SSH_PASSWORD);
        String sshPort = p.getProperty(OSShellCommand.SSH_PORT); // 数据库所在服务器的ssh端口

        StandardDatabaseConfiguration config = new StandardDatabaseConfiguration(this.context, host, driverClassName, url, username, password, adminUsername, adminPassword, sshUser, sshUserPw, sshPort);
        this.add(config);
        return config;
    }

    public void add(DatabaseConfiguration config) {
        DatabaseDialect dialect = this.context.getBean(DatabaseDialect.class, config.getUrl());
        List<DatabaseURL> list = dialect.parseJdbcUrl(config.getUrl());
        for (DatabaseURL url : list) {
            String key = this.toKey(url.getHostname(), url.getPort(), url.getDatabaseName());
            if (this.map.containsKey(key)) {
                DatabaseConfiguration obj = this.map.get(key);
                Collection<String> names = config.getAccountNames();
                for (String name : names) {
                    OSAccount account = config.getAccount(name);
                    obj.addAccount(account.getUsername(), account.getPassword(), account.isAdmin());
                }
            } else {
                this.map.put(key, config);
            }
        }
    }

    public DatabaseConfiguration get(Connection conn) throws SQLException {
        String url = conn.getMetaData().getURL();
        DatabaseDialect dialect = this.context.getBean(DatabaseDialect.class, url);
        List<DatabaseURL> list = dialect.parseJdbcUrl(url);
        for (DatabaseURL obj : list) {
            DatabaseConfiguration config = this.get(obj.getHostname(), obj.getPort(), obj.getDatabaseName());
            if (config != null) {
                return config;
            }
        }
        return null;
    }

    public DatabaseConfiguration get(String hostname, String port, String database) {
        String key = this.toKey(hostname, port, database);
        return this.map.get(key);
    }

    /**
     * 转为数据库配置信息编号
     *
     * @param hostname 数据库服务器host
     * @param port     端口
     * @param database 数据库名
     * @return 编号
     */
    protected String toKey(String hostname, String port, String database) {
        StringBuilder buf = new StringBuilder();
        buf.append(hostname);
        buf.append('-');
        buf.append(port);
        buf.append('-');
        buf.append(database);
        return buf.toString();
    }
}
