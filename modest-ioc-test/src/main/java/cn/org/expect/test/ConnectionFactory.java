package cn.org.expect.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

@EasyBean
public class ConnectionFactory implements EasyBeanFactory<Connection> {

    public Connection build(EasyContext context, Object... args) throws Exception {
        String name = ArrayUtils.indexOf(args, String.class, 0); // EasyBean 的名字
        ClassLoader classLoader = context.getClassLoader();
        String resource = name + ".properties";
        if (StringUtils.isNotBlank(name) && ClassUtils.existsResource(classLoader, resource)) {
            Properties config = FileUtils.loadProperties(classLoader, resource, ModestRunner.PROPERTY_ACTIVE_PROFILE);
            String databaseUrl = config.getProperty("databaseUrl");
            String username = config.getProperty("username");
            String password = config.getProperty("password");
            return DriverManager.getConnection(databaseUrl, username, password);
        }

        // 测试类名以 DB2 开头
        Class<?> cls = ArrayUtils.indexOf(args, Class.class, 0); // 单元测试类
        if (cls != null && StringUtils.startsWithIgnoreCase(cls.getSimpleName(), "db2")) {
            Properties config = FileUtils.loadProperties(classLoader, "db2.properties", ModestRunner.PROPERTY_ACTIVE_PROFILE);
            String databaseUrl = config.getProperty("databaseUrl");
            String username = config.getProperty("username");
            String password = config.getProperty("password");
            return DriverManager.getConnection(databaseUrl, username, password);
        }

        Properties config = ArrayUtils.indexOf(args, Properties.class, 0);
        if (config != null) {
            String databaseUrl = config.getProperty("databaseUrl");
            String username = config.getProperty("username");
            String password = config.getProperty("password");
            return DriverManager.getConnection(databaseUrl, username, password);
        }

        return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "user", "user");
    }
}
