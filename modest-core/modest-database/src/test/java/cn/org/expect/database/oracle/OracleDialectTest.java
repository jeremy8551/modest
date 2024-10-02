package cn.org.expect.database.oracle;

import java.util.List;

import cn.org.expect.database.DatabaseURL;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class OracleDialectTest {

    @Test
    public void test1() {
        OracleDialect dialect = new OracleDialect();
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:oracle:thin:@130.1.10.104:1521:sid")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@130.1.10.104:1521:sid")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:oracle:thin:@130.1.10.104:1521/sid")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@130.1.10.104:1521/sid")));
        System.out.println(StringUtils.toString(dialect.parseJdbcUrl("jdbc:oracle:thin:@(description=(address_list= (address=(host=rac1) (protocol=tcp1)(port=1521))(address=(host=rac2)(protocol=tcp2) (port=1522)) (load_balance=yes)(failover=yes))(connect_data=(SERVER=DEDICATED)(service_name= oratest)))")));
    }

    @Test
    public void test2() {
        OracleDialect dialect = new OracleDialect();
        List<DatabaseURL> list3 = dialect.parseJdbcUrl("jdbc:oracle:thin:@130.1.10.104:1521:sid");
        Assert.assertEquals(list3.size(), 1);
        DatabaseURL url = list3.get(0);
        Assert.assertEquals(url.getHostname(), "130.1.10.104");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        System.out.println(StringUtils.toString(url.toProperties()));

        List<DatabaseURL> list4 = dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@130.1.10.104:1521:sid");
        Assert.assertEquals(list4.size(), 1);
        url = list4.get(0);
        Assert.assertEquals(url.getHostname(), "130.1.10.104");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getUsername(), "user");
        Assert.assertEquals(url.getPassword(), "pass");
        System.out.println(StringUtils.toString(url.toProperties()));

        List<DatabaseURL> list5 = dialect.parseJdbcUrl("jdbc:oracle:thin:@130.1.10.104:1521/sid");
        Assert.assertEquals(list5.size(), 1);
        url = list5.get(0);
        Assert.assertEquals(url.getHostname(), "130.1.10.104");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertNull(url.getUsername());
        Assert.assertNull(url.getPassword());
        System.out.println(StringUtils.toString(url.toProperties()));

        List<DatabaseURL> list6 = dialect.parseJdbcUrl("jdbc:oracle:thin:user/pass@130.1.10.104:1521/sid");
        Assert.assertEquals(list6.size(), 1);
        url = list6.get(0);
        Assert.assertEquals(url.getHostname(), "130.1.10.104");
        Assert.assertEquals(url.getDatabaseName(), "sid");
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertEquals(url.getSID(), "sid");
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getUsername(), "user");
        Assert.assertEquals(url.getPassword(), "pass");
        System.out.println(StringUtils.toString(url.toProperties()));

        List<DatabaseURL> list7 = dialect.parseJdbcUrl("jdbc:oracle:thin:@(description=(address_list= (address=(host=rac1) (protocol=tcp1)(port=1521))(address=(host=rac2)(protocol=tcp2) (port=1522)) (load_balance=yes)(failover=yes))(connect_data=(SERVER=DEDICATED)(service_name= oratest)))");
        Assert.assertEquals(list7.size(), 2);
        url = list7.get(0);
        System.out.println(StringUtils.toString(list7.get(0).toProperties()));
        System.out.println(StringUtils.toString(list7.get(1).toProperties()));
        Assert.assertEquals(url.getHostname(), "rac1");
        Assert.assertNull(url.getDatabaseName());
        Assert.assertEquals(url.getType(), "oracle");
        Assert.assertEquals(url.getPort(), "1521");
        Assert.assertNull(url.getSID());
        Assert.assertEquals(url.getDriverType(), "thin");
        Assert.assertEquals(url.getAttribute("protocol"), "tcp1");
    }
}
