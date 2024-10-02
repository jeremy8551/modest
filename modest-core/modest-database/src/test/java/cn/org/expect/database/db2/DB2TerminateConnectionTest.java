package cn.org.expect.database.db2;

import java.sql.Connection;
import java.sql.SQLException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DB2Runner.class)
public class DB2TerminateConnectionTest {

    /** 容器上下文信息 */
    @EasyBean
    public EasyContext context;

    /** 数据库连接 */
    @EasyBean
    public Connection connection;

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
                System.out.println("DB2数据库连接中断测试成功!");
            }
        }
    }

}

