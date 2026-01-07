package cn.org.expect.database.db2;

import java.sql.Connection;
import java.sql.Types;
import java.util.List;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseProcedureParameter;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 DB2 数据库存储过程 TODO
 */
@RunWith(ModestRunner.class)
@RunWithFeature("db2")
public class DB2ProcedureTest {
    private final static Log log = LogFactory.getLog(DB2ProcedureTest.class);

    @EasyBean
    private EasyContext context;

    @EasyBean("db2")
    private Connection connection;

    @Before
    public void setUp2() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeQuietly("drop PROCEDURE TEST_PROC ");

            String sql = "";
            sql += "CREATE PROCEDURE TEST_PROC                    " + Settings.getLineSeparator();
            sql += " (OUT FLAG INTEGER                            " + Settings.getLineSeparator();
            sql += " )                                            " + Settings.getLineSeparator();
            sql += "  LANGUAGE SQL                                " + Settings.getLineSeparator();
            sql += "  NOT DETERMINISTIC                           " + Settings.getLineSeparator();
            sql += "  CALLED ON NULL INPUT                        " + Settings.getLineSeparator();
            sql += "  EXTERNAL ACTION                             " + Settings.getLineSeparator();
            sql += "  OLD SAVEPOINT LEVEL                         " + Settings.getLineSeparator();
            sql += "  MODIFIES SQL DATA                           " + Settings.getLineSeparator();
            sql += "  INHERIT SPECIAL REGISTERS                   " + Settings.getLineSeparator();
            sql += "  BEGIN                                       " + Settings.getLineSeparator();
            sql += "  SET FLAG = 1;                               " + Settings.getLineSeparator();
            sql += "  RETURN 0;                                   " + Settings.getLineSeparator();
            sql += "END                                           " + Settings.getLineSeparator();
            JdbcDao.execute(dao.getConnection(), sql);
            dao.commit();
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
            DatabaseProcedure procedure = dao.getDialect().getProcedureForceOne(dao.getConnection(), dao.getCatalog(), dao.getSchema(), "TEST_PROC");
            DatabaseDDL ddl = dao.toDDL(procedure);
            if (ddl == null) {
                throw new NullPointerException();
            }

            String str = ddl.toString();
            log.info(str);
            Assert.assertTrue(StringUtils.startsWith(str, "create", 0, true, true));
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
    public void testCallProcedureByJdbcString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            DatabaseProcedure result = dao.callProcedure("call " + dao.getSchema() + ".TEST_PROC(?)");
            Assert.assertNotNull(result);
            List<DatabaseProcedureParameter> params = result.getParameters();
            Assert.assertTrue(params.size() == 1 //
                && params.get(0).getName().equalsIgnoreCase("flag") //
                && params.get(0).getSqlType() == Types.INTEGER //
                && (Integer) params.get(0).getValue() == 1 //
            );
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            dao.commit();
            dao.close();
        }
    }

    @Test
    public void testCallProcedureByJdbcStringJdbcCallProcedure() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            DatabaseProcedure proc = dao.callProcedure("call " + dao.getSchema() + ".TEST_PROC(?)");
            List<DatabaseProcedureParameter> params = proc.getParameters();
            Assert.assertTrue(params.size() == 1 //
                && params.get(0).getName().equalsIgnoreCase("flag") //
                && params.get(0).getSqlType() == Types.INTEGER //
                && (Integer) params.get(0).getValue() == 1 //
            );

            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            dao.commit();
            dao.close();
        }
    }
}
