package cn.org.expect.database.db2;

import java.sql.Connection;
import java.sql.SQLException;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithProperties;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithProperties(filename = "db2", require = {"db2.url", "db2.username", "db2.password"})
public class DB2TerminateConnectionTest {
    private final static Log log = LogFactory.getLog(DB2TerminateConnectionTest.class);

    @EasyBean
    private EasyContext context;

    @EasyBean
    private Connection connection;

    @Test
    public void test() throws SQLException {
        try {
            DatabaseDialect dialect = this.context.getBean(DatabaseDialect.class, connection);

            TestThread thread = new TestThread(dialect, connection);
            thread.start();

            TimeWatch watch = new TimeWatch();
            while (watch.useSeconds() < 5 || thread.isAlive()) {
            }

            Assert.assertFalse(thread.isError());

            try {
                connection.commit();
                Assert.fail();
            } catch (Throwable e) {
                Assert.assertTrue(true);
            }
        } catch (Exception e) {
            try {
                connection.rollback();
                Assert.fail();
            } catch (Throwable e1) {
                Assert.assertTrue(true);
            }
        } finally {
            try {
                connection.close();
                Assert.fail();
            } catch (Throwable e2) {
                Assert.assertTrue(true);
                log.info("DB2数据库连接中断测试成功!");
            }
        }
    }
}
