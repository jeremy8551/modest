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
import cn.org.expect.test.annotation.EasyRunIf;
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
@EasyRunIf(values = {"db2.url", "db2.username", "db2.password"})
public class DB2ProcedureTest {
    private final static Log log = LogFactory.getLog(DB2ProcedureTest.class);

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
            sql += "CREATE PROCEDURE TEST_PROC            " + Settings.LINE_SEPARATOR;
            sql += " (OUT FLAG INTEGER                          " + Settings.LINE_SEPARATOR;
            sql += " )                                            " + Settings.LINE_SEPARATOR;
            sql += "  LANGUAGE SQL                                " + Settings.LINE_SEPARATOR;
            sql += "  NOT DETERMINISTIC                           " + Settings.LINE_SEPARATOR;
            sql += "  CALLED ON NULL INPUT                        " + Settings.LINE_SEPARATOR;
            sql += "  EXTERNAL ACTION                             " + Settings.LINE_SEPARATOR;
            sql += "  OLD SAVEPOINT LEVEL                         " + Settings.LINE_SEPARATOR;
            sql += "  MODIFIES SQL DATA                           " + Settings.LINE_SEPARATOR;
            sql += "  INHERIT SPECIAL REGISTERS                   " + Settings.LINE_SEPARATOR;
            sql += "  BEGIN                                       " + Settings.LINE_SEPARATOR;
            sql += "  SET FLAG = 1;                               " + Settings.LINE_SEPARATOR;
            sql += "  RETURN 0;                                   " + Settings.LINE_SEPARATOR;
            sql += "END                                           " + Settings.LINE_SEPARATOR;
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
