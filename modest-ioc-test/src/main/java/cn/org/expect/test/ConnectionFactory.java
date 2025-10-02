package cn.org.expect.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyBean
public class ConnectionFactory implements EasyBeanFactory<Connection> {

    public static String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    public static String JDBC_USER = "user";

    public static String JDBC_PASSWORD = "user";

    public Connection build(EasyContext context, Object... args) throws Exception {
        String name = ArrayUtils.indexOf(args, String.class, 0); // EasyBean的名字
        Class<?> cls = ArrayUtils.indexOf(args, Class.class, 0); // 单元测试类

        String url = JDBC_URL;
        String username = JDBC_USER;
        String password = JDBC_PASSWORD;

        // 如果组件名是 db2 或 测试类名以 DB2 开头
        if ("db2".equalsIgnoreCase(name) || (cls != null && StringUtils.startsWithIgnoreCase(cls.getSimpleName(), "db2"))) {
            Properties config = ArrayUtils.indexOf(args, Properties.class, 0);
            Ensure.notNull(config);
            url = config.getProperty("db2.url");
            username = config.getProperty("db2.username");
            password = config.getProperty("db2.password");
        }

        return DriverManager.getConnection(url, username, password);
    }
}
