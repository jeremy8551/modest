package cn.org.expect.database.oracle;

import java.util.List;

import cn.org.expect.database.DatabaseURL;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class OracleDialectTest {
    private final static Log log = LogFactory.getLog(OracleDialectTest.class);

    @Test
    public void test1() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list3 = dialect.parseJdbcUrl("jdbc:oracle:thin:@192.168.1.102:1521:sid");
        Assert.assertEquals(list3.size(), 1);
        DatabaseURL url = list3.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.102");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(3, url.toProperties().size());
    }

    @Test
    public void test2() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list4 = dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@192.168.1.102:1521:sid");
        Assert.assertEquals(list4.size(), 1);
        DatabaseURL url = list4.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.102");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getUsername(), "user");
        Assert.assertEquals(url.getPassword(), "pass");
        Assert.assertEquals(3, url.toProperties().size());
    }

    @Test
    public void test3() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list5 = dialect.parseJdbcUrl("jdbc:oracle:thin:@192.168.1.102:1521/sid");
        Assert.assertEquals(list5.size(), 1);
        DatabaseURL url = list5.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.102");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertNull(url.getUsername());
        Assert.assertNull(url.getPassword());
        Assert.assertEquals(3, url.toProperties().size());
    }

    @Test
    public void test4() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list6 = dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@192.168.1.102:1521/sid");
        Assert.assertEquals(list6.size(), 1);
        DatabaseURL url = list6.get(0);
        Assert.assertEquals(url.getHostname(), "192.168.1.102");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getUsername(), "user");
        Assert.assertEquals(url.getPassword(), "pass");
        Assert.assertEquals(3, url.toProperties().size());
    }

    @Test
    public void test5() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list7 = dialect.parseJdbcUrl("jdbc:oracle:thin:@(description=(address_list= (address=(host=rac1) (protocol=tcp1)(port=1521))(address=(host=rac2)(protocol=tcp2) (port=1522)) (load_balance=yes)(failover=yes))(connect_data=(SERVER=DEDICATED)(service_name= oratest)))");
        Assert.assertEquals(list7.size(), 2);
        DatabaseURL url = list7.get(0);
        Assert.assertEquals(7, list7.get(0).toProperties().size());
        Assert.assertEquals(7, list7.get(1).toProperties().size());
        Assert.assertEquals(url.getHostname(), "rac1");
        Assert.assertNull(url.getDatabaseName());
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertNull(url.getSID());
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getAttribute("protocol"), "tcp1");
    }
}
