package cn.org.expect.database.h2;

import java.sql.Connection;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.annotation.DatabaseRunner;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DatabaseRunner.class)
public class H2ProcedureTest {

    /** 容器上下文信息 */
    @EasyBean
    public EasyContext context;

    /** 数据库连接 */
    @EasyBean
    public Connection connection;

    @Before
    public void setUp2() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeQuietly("drop PROCEDURE TEST_PROC ");

            String sql = "";
            sql += "CREATE ALIAS TEST_PROC AS $$" + FileUtils.lineSeparator;
            sql += "ResultSet testProc(Connection conn, String sql) throws SQLException {" + FileUtils.lineSeparator;
            sql += "    return conn.createStatement().executeQuery(sql);" + FileUtils.lineSeparator;
            sql += "} $$" + FileUtils.lineSeparator;

//            sql = "";
//            sql += "CREATE PROCEDURE TEST_PROC()" + FileUtils.lineSeparator;
//            sql += " AS " + FileUtils.lineSeparator;
//            sql += " BEGIN " + FileUtils.lineSeparator;
//            sql += " END " + FileUtils.lineSeparator;
//
//            sql = "CREATE ALIAS TEST_PROC FOR \"" + H2Procedure.class.getName() + ".testProc" + "\"";
            JdbcDao.execute(dao.getConnection(), sql);

            dao.commit();
            this.connection = dao.getConnection();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testtoDDL() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            DatabaseProcedure procedure = dao.getDialect().getProcedureForceOne(dao.getConnection(), null, null, "TEST_PROC");
            if (procedure == null) {
                Assert.fail();
            }

            DatabaseDDL ddl = dao.toDDL(procedure);
            if (ddl == null) {
                Assert.fail();
            }

            String str = ddl.toString();
            System.out.println(str);
            Assert.assertTrue(StringUtils.isNotBlank(str));
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }
}
