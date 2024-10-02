package cn.org.expect.database;

import java.sql.Connection;
import java.util.Properties;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyBean
public class ConnectionBuilder implements EasyBeanBuilder<Connection> {

    public static String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static String JDBC_USER = "user";
    public static String JDBC_PASSWORD = "user";

    public Connection getBean(EasyContext context, Object... args) throws Exception {
        String name = ArrayUtils.indexOf(args, String.class, 0); // EasyBean的名字
        Class<?> cls = ArrayUtils.indexOf(args, Class.class, 0); // 单元测试类

        if ("db2".equalsIgnoreCase(name) || (cls != null && StringUtils.startsWithIgnoreCase(cls.getSimpleName(), "db2"))) {
            Properties config = ArrayUtils.indexOf(args, Properties.class, 0);
            Ensure.notNull(config);
            String url = config.getProperty("db2.url");
            String username = config.getProperty("db2.username");
            String password = config.getProperty("db2.password");
            return Jdbc.getConnection(url, username, password);
        }

        return Jdbc.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
