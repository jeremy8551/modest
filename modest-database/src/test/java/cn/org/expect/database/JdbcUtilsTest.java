package cn.org.expect.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
@RunWith(ModestRunner.class)
public class JdbcUtilsTest {
    private final static Log log = LogFactory.getLog(JdbcUtilsTest.class);

    /** 容器上下文信息 */
    @EasyBean
    public EasyContext context;

    /** 数据库连接 */
    @EasyBean
    public Connection connection;

    @Test
    public void test1() {
        Connection conn = this.connection;
        try {
            CaseSensitivSet set = Jdbc.getSQLKeywords(conn);
            log.info(StringUtils.toString(set));
            Jdbc.rollback(conn);
        } catch (Exception e) {
            Jdbc.rollback(conn);
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            IO.closeQuietly(conn);
        }
    }

    @Test
    public void test2() throws SQLException {
        String tablename = "";
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            tablename = "test".toUpperCase() + Dates.format17();
            dao.execute("create table " + tablename + "(f1 char(100) not null, f2 char(10), primary key(f1) ) ");
            dao.commit();

            dao.execute("create index " + tablename + "idx on " + tablename + "(f2)");
            dao.commit();

            String schema = dao.getSchema();
            DatabaseTable table = dao.getTable(null, schema, tablename);
            List<DatabaseIndex> primaryIndexs = table.getPrimaryIndexs();

            DatabaseTable clone = table.clone();

            Assert.assertTrue(clone.getIndexs().contains(primaryIndexs.get(0), false, false) || clone.getPrimaryIndexs().contains(primaryIndexs.get(0), false, false));
            Assert.assertTrue(primaryIndexs.get(0).equals(clone.getPrimaryIndexs().get(0), true, true));

            dao.rollback();
        } finally {
            try {
                dao.execute("drop table " + tablename);
                dao.commit();
            } finally {
                dao.close();
            }
        }
    }

    @Test
    public void test3() {
        String tablename = "";
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Assert.assertTrue(dao.testConnection());

            tablename = "test".toUpperCase() + Dates.format17();
            dao.execute("create table " + tablename + "(f1 char(100) not null, f2 char(10), primary key(f1) ) ");
            dao.commit();
            dao.execute("create index " + tablename + "idx on " + tablename + "(f2)");
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void test4() throws Exception {
        String tablename = "";
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Assert.assertTrue(dao.testConnection());

            tablename = "test" + Dates.format17();
            dao.execute("create table " + tablename + "(f1 char(100), f2 char(10) ) ");
            dao.commit();

            JdbcQueryStatement qryLastCreditLine = new JdbcQueryStatement(dao.getConnection(), "select * from " + tablename + " a where f1 = ? and f2 < ? ");
            qryLastCreditLine.setParameter("2301052016000008");
            qryLastCreditLine.setParameter("2017-04-25");
            ResultSet result = qryLastCreditLine.query();
            result.next();

            qryLastCreditLine.close();
            dao.commit();
        } finally {
            try {
                dao.execute("drop table " + tablename);
                dao.commit();
            } finally {
                dao.close();
            }
        }
    }
}
