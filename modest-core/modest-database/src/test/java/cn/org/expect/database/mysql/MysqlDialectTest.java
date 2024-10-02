package cn.org.expect.database.mysql;

import java.util.List;

import cn.org.expect.database.DatabaseURL;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class MysqlDialectTest {

    @Test
    public void test1() {
        MysqlDialect dialect = new MysqlDialect();
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:mysql://localhost:3306/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:mysql://localhost/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:mysql://localhost,127.0.0.1/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false")));
    }

    @Test
    public void test2() {
        MysqlDialect dialect = new MysqlDialect();
        List<DatabaseURL> list0 = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(1, list0.size());
        DatabaseURL url = list0.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");

        List<DatabaseURL> list1 = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.1/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(1, list1.size());
        url = list1.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        System.out.println(StringUtils.toString(url.toProperties()));

        List<DatabaseURL> list2 = dialect.parseJdbcUrl("jdbc:mysql://127.0.0.2,127.0.0.1/test?user=root&password=&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false");
        Assert.assertEquals(list2.size(), 2);
        url = list2.get(0);
        Assert.assertEquals(url.getHostname(), "127.0.0.2");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        System.out.println(StringUtils.toString(url.toProperties()));

        url = list2.get(1);
        Assert.assertEquals(url.getHostname(), "127.0.0.1");
        Assert.assertEquals(url.getDatabaseName(), "test");
        Assert.assertEquals(url.getType(), "mysql");
        Assert.assertEquals(url.getPort(), "3306");
        System.out.println(StringUtils.toString(list2.get(1).toProperties()));
    }
}
