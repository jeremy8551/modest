package cn.org.expect.database;

import java.sql.Connection;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;

@EasyBean
public class ConnectionBuilder implements EasyBeanBuilder<Connection> {

    public static String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static String JDBC_USER = "user";
    public static String JDBC_PASSWORD = "user";

    public Connection getBean(EasyContext context, Object... args) throws Exception {
        String name = ArrayUtils.indexOf(args, String.class, 0);
        if ("db2".equalsIgnoreCase(name)) {
//            String url = this.properties.getProperty("db2.url");
//            String username = this.properties.getProperty("db2.username");
//            String password = this.properties.getProperty("db2.password");
//            return Jdbc.getConnection(url, username, password);
        }

        return Jdbc.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
