package cn.org.expect.database.db2;

import java.sql.Connection;
import java.sql.Types;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseProcedureParameter;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunIf;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 DB2 数据库存储过程 TODO
 */
@RunWith(ModestRunner.class)
@RunIf(values = {"db2.url", "db2.username", "db2.password"})
public class DB2ProcedureTest {

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
            sql += "CREATE PROCEDURE TEST_PROC            " + FileUtils.lineSeparator;
            sql += " (OUT FLAG INTEGER                          " + FileUtils.lineSeparator;
            sql += " )                                            " + FileUtils.lineSeparator;
            sql += "  LANGUAGE SQL                                " + FileUtils.lineSeparator;
            sql += "  NOT DETERMINISTIC                           " + FileUtils.lineSeparator;
            sql += "  CALLED ON NULL INPUT                        " + FileUtils.lineSeparator;
            sql += "  EXTERNAL ACTION                             " + FileUtils.lineSeparator;
            sql += "  OLD SAVEPOINT LEVEL                         " + FileUtils.lineSeparator;
            sql += "  MODIFIES SQL DATA                           " + FileUtils.lineSeparator;
            sql += "  INHERIT SPECIAL REGISTERS                   " + FileUtils.lineSeparator;
            sql += "  BEGIN                                       " + FileUtils.lineSeparator;
            sql += "  SET FLAG = 1;                               " + FileUtils.lineSeparator;
            sql += "  RETURN 0;                                   " + FileUtils.lineSeparator;
            sql += "END                                           " + FileUtils.lineSeparator;
            JdbcDao.execute(dao.getConnection(), sql);
            dao.commit();
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
            DatabaseProcedure procedure = dao.getDialect().getProcedureForceOne(dao.getConnection(), dao.getCatalog(), dao.getSchema(), "TEST_PROC");
            DatabaseDDL ddl = dao.toDDL(procedure);
            if (ddl == null) {
                throw new NullPointerException();
            }

            String str = ddl.toString();
            System.out.println(str);
            Assert.assertTrue(StringUtils.startsWith(str, "create", 0, true, true));
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.commit();
            dao.close();
        }
    }

}
