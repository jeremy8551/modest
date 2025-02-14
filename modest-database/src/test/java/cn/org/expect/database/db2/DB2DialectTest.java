package cn.org.expect.database.db2;

import java.util.List;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class DB2DialectTest {
    private final static Log log = LogFactory.getLog(DB2DialectTest.class);

    @Test
    public void test1() {
        DatabaseDialect dialect = new DB2Dialect();
        List<DatabaseURL> urlList = dialect.parseJdbcUrl("jdbc:db2://192.168.1.100/TESTDB:currentSchema=TEST;");
        Assert.assertEquals(1, urlList.size());
        DatabaseURL url = urlList.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.100");
        Assert.assertEquals(url.getDatabaseName(), "TESTDB");
        Assert.assertEquals(url.getType(), "db2");
        Assert.assertEquals(url.getSchema(), "TEST");
    }

    @Test
    public void test2() {
        Assert.assertNull(DB2Dialect.to(null));
        Assert.assertEquals("", DB2Dialect.to(""));
        Assert.assertEquals("A", DB2Dialect.to("a"));
        Assert.assertEquals("AB", DB2Dialect.to("ab"));
        Assert.assertEquals("aBc", DB2Dialect.to("\"aBc\""));
        Assert.assertEquals("aBc", DB2Dialect.to("'aBc'"));
        Assert.assertEquals("", DB2Dialect.to(""));
    }

    @Test
    public void test3() {
        DatabaseDialect dialect = new DB2Dialect();
        List<DatabaseURL> urlList = dialect.parseJdbcUrl("jdbc:db2://192.168.1.100:50001/TESTDB:currentSchema=TEST;");
        Assert.assertEquals(1, urlList.size());
        DatabaseURL url = urlList.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.100");
        Assert.assertEquals(url.getDatabaseName(), "TESTDB");
        Assert.assertEquals(url.getType(), "db2");
        Assert.assertEquals(url.getPort(), "50001");
        Assert.assertEquals(url.getSchema(), "TEST");

        List<DatabaseURL> l = dialect.parseJdbcUrl("jdbc:db2:TESTDB");
        Assert.assertEquals(1, l.size());
        Assert.assertEquals(0, l.get(0).toProperties().size());
    }
}
