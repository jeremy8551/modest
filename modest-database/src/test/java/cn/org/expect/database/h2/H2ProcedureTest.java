package cn.org.expect.database.h2;

import java.sql.Connection;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class H2ProcedureTest {
    private final static Log log = LogFactory.getLog(H2ProcedureTest.class);

    @EasyBean
    private EasyContext context;

    @EasyBean
    private Connection connection;

    @Before
    public void setUp2() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeQuietly("drop PROCEDURE TEST_PROC ");

            String sql = "";
            sql += "CREATE ALIAS TEST_PROC AS $$" + Settings.LINE_SEPARATOR;
            sql += "ResultSet testProc(Connection conn, String sql) throws SQLException {" + Settings.LINE_SEPARATOR;
            sql += "    return conn.createStatement().executeQuery(sql);" + Settings.LINE_SEPARATOR;
            sql += "} $$" + Settings.LINE_SEPARATOR;

            JdbcDao.execute(dao.getConnection(), sql);

            dao.commit();
            this.connection = dao.getConnection();
        } catch (Exception e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void testToDDL() {
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
            log.info(str);
            Assert.assertTrue(StringUtils.isNotBlank(str));
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            dao.close();
        }
    }
}
