package cn.org.expect.database.db2;

import java.util.List;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class DB2DialectTest {

    @Test
    public void test1() {
        DB2Dialect dialect = new DB2Dialect();
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:db2://130.1.10.103:50001/TESTDB:currentSchema=HYCS;")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:db2://130.1.10.103/TESTDB:currentSchema=HYCS;")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:db2:TESTDB")));
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
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:db2://130.1.10.103:50001/TESTDB:currentSchema=HYCS;");
        Assert.assertEquals(1, list.size());
        DatabaseURL u = list.get(0);
        Assert.assertEquals(u.getHostname(), "130.1.10.103");
        Assert.assertEquals(u.getDatabaseName(), "TESTDB");
        Assert.assertEquals(u.getType(), "db2");
        Assert.assertEquals(u.getPort(), "50001");
        Assert.assertEquals(u.getSchema(), "HYCS");

        List<DatabaseURL> l = dialect.parseJdbcUrl("jdbc:db2:TESTDB");
        Assert.assertEquals(1, l.size());
        System.out.println(StringUtils.toString(l.get(0).toProperties()));
    }
}
