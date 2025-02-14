package cn.org.expect.database.h2;

import java.util.List;

import cn.org.expect.database.DatabaseURL;
import org.junit.Assert;
import org.junit.Test;

public class H2DialectTest {

    @Test
    public void test1() {
        H2Dialect dialect = new H2Dialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:h2:file:~/.h2/DBName;AUTO_SERVER=TRUE");
        Assert.assertEquals(1, list.size());

        DatabaseURL url = list.get(0);
        Assert.assertEquals("DBName", url.getDatabaseName());
        Assert.assertEquals("h2", url.getType());
    }

    @Test
    public void test2() {
        H2Dialect dialect = new H2Dialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:h2:tcp://localhost/~/DBName");
        Assert.assertEquals(1, list.size());

        DatabaseURL url = list.get(0);
        Assert.assertEquals("DBName", url.getDatabaseName());
        Assert.assertEquals("h2", url.getType());
        Assert.assertEquals("9092", url.getPort());
        Assert.assertEquals("localhost", url.getHostname());
    }

    @Test
    public void test3() {
        H2Dialect dialect = new H2Dialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:h2:mem:DBName;");
        Assert.assertEquals(1, list.size());

        DatabaseURL url = list.get(0);
        Assert.assertEquals("DBName", url.getDatabaseName());
        Assert.assertEquals("h2", url.getType());
        Assert.assertEquals("9092", url.getPort());
        Assert.assertEquals("127.0.0.1", url.getHostname());
    }

    @Test
    public void test4() {
        H2Dialect dialect = new H2Dialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:h2:tcp://localhost:8088/mem:DBName");
        Assert.assertEquals(1, list.size());

        DatabaseURL url = list.get(0);
        Assert.assertEquals("DBName", url.getDatabaseName());
        Assert.assertEquals("h2", url.getType());
        Assert.assertEquals("8088", url.getPort());
        Assert.assertEquals("localhost", url.getHostname());
    }
}
