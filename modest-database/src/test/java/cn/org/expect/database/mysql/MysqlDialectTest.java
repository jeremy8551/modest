package cn.org.expect.database.mysql;

import java.util.List;

import cn.org.expect.database.DatabaseURL;
import org.junit.Assert;
import org.junit.Test;

public class MysqlDialectTest {

    @Test
    public void test1() {
        MysqlDialect dialect = new MysqlDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(1, list.size());
        DatabaseURL url = list.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
    }

    @Test
    public void test2() {
        MysqlDialect dialect = new MysqlDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.1/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(1, list.size());
        DatabaseURL url = list.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        Assert.assertEquals(8, url.toProperties().size());
    }

    @Test
    public void test3() {
        MysqlDialect dialect = new MysqlDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.2,127.0.0.1/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(list.size(), 2);
        DatabaseURL url = list.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.2");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        Assert.assertEquals(8, url.toProperties().size());

        url = list.get(1);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        Assert.assertEquals(8, list.get(1).toProperties().size());
    }
}
