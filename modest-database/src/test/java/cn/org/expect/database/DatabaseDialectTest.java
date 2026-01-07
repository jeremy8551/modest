package cn.org.expect.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import cn.org.expect.database.internal.StandardDatabaseDialect;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class DatabaseDialectTest {
    private final static Log log = LogFactory.getLog(DatabaseDialectTest.class);

    @EasyBean
    private EasyContext context;

    @EasyBean
    private Connection connection;

    @Test
    public void test0() {
        String catalog = null;
        String schema = null;
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            String packageName = Settings.getPackageName();
            String tableName = packageName.replace('.', '_') + "_TEST_TEMP".toUpperCase();
            tableName = tableName.toUpperCase();

            String fullName = dao.getDialect().generateTableName(catalog, schema, tableName);
            DatabaseTable table = dao.getTable(catalog, schema, tableName);
            if (table != null) {
                dao.dropTable(table);
                dao.commit();
            }

            String sql = "";
            sql += "create table " + tableName + " (                               \n";
            sql += "    SERIALNO  VARCHAR(32) not null, --  xxx                        \n";
            sql += "    OBJECTNO  VARCHAR(40), --  xxx                                \n";
            sql += "    DOCUMENTTYPE  VARCHAR(30), --  xxx                            \n";
            sql += "    PAYDATE  VARCHAR(10), --  xx                                 \n";
            sql += "    ACTUALPAYDATE  VARCHAR(10), --  xx                         \n";
            sql += "    CURRENCY  VARCHAR(3), --  xx                                   \n";
            sql += "    PAYAMT  NUMERIC(20, 2), --  xx                               \n";
            sql += "    ACTUALPAYAMT  NUMERIC(20, 2), --  xx                         \n";
            sql += "    DEDUCTACCNO1  VARCHAR(40), --  xx                            \n";
            sql += "    DEDUCTACCNO2  VARCHAR(40), --  xx                            \n";
            sql += "    DEDUCTACCNO  VARCHAR(40), --  x                             \n";
            sql += "    BILLSTATUS  VARCHAR(10), --  x                                \n";
            sql += "    RETURNCHANNEL  VARCHAR(20), --  xx                           \n";
            sql += "    OBJECTTYPE  VARCHAR(10), --  xx                              \n";
            sql += "    PAYCORPUSAMT  NUMERIC(20, 2), --  xx                         \n";
            sql += "    ACTUALPAYCORPUSAMT  NUMERIC(20, 2), --  xx                   \n";
            sql += "    PAYINTEAMT  NUMERIC(20, 2), --  xx                           \n";
            sql += "    ACTUALPAYINTEAMT  NUMERIC(20, 2), --  xx                     \n";
            sql += "    PAYFINEAMT  NUMERIC(20, 2), --  xx                           \n";
            sql += "    ACTUALFINEAMT  NUMERIC(20, 2), --  xx                        \n";
            sql += "    PAYCOMPDINTEAMT  NUMERIC(20, 2), --  xx                      \n";
            sql += "    ACTUALCOMPDINTEAMT  NUMERIC(20, 2), --  xxx                   \n";
            sql += "    PAYFEEAMT  NUMERIC(20, 2), --  xxx                            \n";
            sql += "    ACTUALFEEAMT  NUMERIC(20, 2), --  xxx                         \n";
            sql += "    ORGID  VARCHAR(20), --  xxx                                     \n";
            sql += "    TBREPAYSERIALNO  VARCHAR(32), --  xxx                \n";
            sql += "    TBLOANSERIALNO  VARCHAR(32), --  xxx                 \n";
            sql += "    FEETYPE  VARCHAR(20), --  中文测试表                                 \n";
            sql += "    CHECKFLAG  VARCHAR(1), --  中文测试表                                \n";
            sql += "    CHECKACCOUNTNO  VARCHAR(32), --  中文测试表                        \n";
            sql += "    CHECKACCOUNTTYPE  VARCHAR(2), --  中文测试表                       \n";
            sql += "    DEDUCTACCNONAME  VARCHAR(80), --  中文测试表                        \n";
            sql += "    PREPAYCORPUS  NUMERIC(20, 2), --  中文测试表                     \n";
            sql += "    ACTUALPREPAYCORPUS  NUMERIC(20, 2), --  中文测试表               \n";
            sql += "    PREPAYINTEREST  NUMERIC(20, 2), --  中文测试表                   \n";
            sql += "    ACTUALPREPAYINTEREST  NUMERIC(20, 2), --  中文测试表             \n";
            sql += "    PAYMENTORDER  NUMERIC(20, 2),                                  \n";
            sql += "    EXPIATIONSUM  NUMERIC(20, 2),                                  \n";
            sql += "    IFSEND  VARCHAR(2),                                            \n";
            sql += "    CUSTOMERID  VARCHAR(32), --  中文测试表                              \n";
            sql += "    CUSTOMERNAME  VARCHAR(80), --  中文测试表                            \n";
            sql += "    BUSINESSTYPE  VARCHAR(18), --  中文测试表                            \n";
            sql += "    PUTOUTDATE  VARCHAR(10), --  中文测试表                               \n";
            sql += "    MATURITYDATE  VARCHAR(10), --  中文测试表                             \n";
            sql += "    CERTID  VARCHAR(40), --  中文测试表                                  \n";
            sql += "    INTEBASEADD  NUMERIC(24, 6), --  中文测试表                        \n";
            sql += "    OLDINTEBASE  NUMERIC(24, 6), --  中文测试表                         \n";
            sql += "    PAYBANKAMT  NUMERIC(20, 2),                                    \n";
            sql += "    PAYCOMPANYAMT  NUMERIC(20, 2),                                 \n";
            sql += "    ACTUALPAYAMTTYPE  VARCHAR(30), --  中文测试表                    \n";
            sql += "    BILLKIND  VARCHAR(30),                                         \n";
            sql += "    ADVANCEPAYMETHOD  VARCHAR(32), --  中文测试表                      \n";
            sql += "    SYSTOLIC  VARCHAR(10), --  中文测试表                                \n";
            sql += "    PROFITAMT  NUMERIC(26, 6), --  中文测试表                          \n";
            sql += "    TRANSCODE  CHAR(4), --  中文测试表                                    \n";
            sql += "    RECORDUSERID  VARCHAR(20), --  中文测试表                             \n";
            sql += "    REPAYMENTTYPE  VARCHAR(8), --  中文测试表                            \n";
            sql += "    REPAYMENTAMT  NUMERIC(22, 2),                                   \n";
            sql += " primary key(SERIALNO) \n";
            sql += ")                             \n";

            dao.execute(sql);
            dao.commit();

            dao.execute("create index " + tableName + "IDX on " + fullName + "(SERIALNO)");
            dao.commit();

            dao.execute("create index " + tableName + "IDX1 on " + fullName + "(OBJECTNO,TRANSCODE)");
            dao.commit();

            dao.execute("create index " + tableName + "IDX2 on " + fullName + "(OBJECTNO,PAYFEEAMT)");
            dao.commit();

            table = dao.getTable(catalog, schema, tableName);
            List<DatabaseIndex> indexs = table.getIndexs();
            List<DatabaseIndex> pks = table.getPrimaryIndexs();

            Assert.assertEquals(indexs.size(), 2);
            Assert.assertEquals(pks.size(), 1);

            DatabaseTableColumn col = table.getColumns().getColumn("PAYAMT");
            if (col == null) {
                throw new NullPointerException();
            }

            Assert.assertEquals(col.getName(), "PAYAMT");
            Assert.assertEquals(col.getPosition(), 7);

            col = table.getColumns().getColumn(7);
            if (col == null) {
                throw new NullPointerException();
            }

            DatabaseTableDDL ddl = dao.toDDL(table);
            log.info(ddl.getTable());

            for (String str : ddl.getPrimaryKey()) {
                log.info(str);
            }

            for (String str : ddl.getIndex()) {
                log.info(str);
            }

            for (String str : ddl.getComment()) {
                log.info(str);
            }

            dao.commit();
        } catch (Throwable e) {
            dao.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void test1() throws SQLException {
        try {
            DatabaseMetaData metaData = this.connection.getMetaData();
            log.info(ClassUtils.toString(metaData, true, true, "get", "to"));
            log.info(StringUtils.toString(Jdbc.getSchemas(this.connection)));

            log.info("getCatalogs:");
            log.info(Jdbc.toString(this.connection.getMetaData().getCatalogs()));

            log.info("getTableTypes:");
            log.info(Jdbc.toString(this.connection.getMetaData().getTableTypes()));
        } catch (Exception e) {
            this.connection.rollback();
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void test2() throws SQLException {
        StandardDatabaseDialect dialect = new StandardDatabaseDialect();
        Assert.assertTrue(StringUtils.isNotBlank(dialect.getSchema(this.connection)));
    }

    /**
     * 打印数据库中字段类型
     */
    @Test
    public void test3() {
        Connection conn = this.connection;
        try {
            StandardDatabaseDialect dialect = new StandardDatabaseDialect();
            DatabaseTypeSet typeInfo = dialect.getFieldInformation(conn);
            log.info(typeInfo.toString());
            Jdbc.rollback(conn);
        } catch (Exception e) {
            Jdbc.rollback(conn);
            log.error(e.getLocalizedMessage(), e);
            Assert.fail();
        } finally {
            IO.closeQuietly(conn);
        }
    }
}
