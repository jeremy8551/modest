package cn.org.expect.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.database.annotation.DatabaseRunner;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
@RunWith(DatabaseRunner.class)
public class JdbcUtilsTest {

    /** 容器上下文信息 */
    @EasyBean
    public EasyContext context;

    /** 数据库连接 */
    @EasyBean
    public Connection connection;

    @Test
    public void testGetTypeInfo() {
        Connection conn = this.connection;
        try {
            DatabaseTypeSet typeInfo = Jdbc.getTypeInfo(conn);
            System.out.println(typeInfo.toString());

            Jdbc.rollback(conn);
        } catch (Exception e) {
            Jdbc.rollback(conn);
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(conn);
        }
    }

    @Test
    public void test51() {
        Connection conn = this.connection;
        try {
            CaseSensitivSet set = Jdbc.getSQLKeywords(conn);
            System.out.println(StringUtils.join(set, "\n"));
            Jdbc.rollback(conn);
        } catch (Exception e) {
            Jdbc.rollback(conn);
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IO.closeQuietly(conn);
        }
    }

//    @Test
//    public void test5() {
//        Connection conn = this.connection;
//        try {
//            DatabaseDialect dialect = DatabaseDialectFactory.getDialect(conn);
//            String schema = dialect.getSchema(conn);
//            DatabaseTableInfo table = dialect.getDatabaseTableInfoForceOne(conn, null, schema, "ECC_ENSURECONTRACTS_R"); // 创建测试表
//            assertTrue(table.matchTableName("", "ECC_ENSURECONTRACTS_R"));
//            assertTrue(table.matchTableName("TESTADM", "ECC_ENSURECONTRACTS_R"));
//            assertTrue(table.matchTableName("1", "ECC_ENSURECONTRACTS_R") == false);
//            Jdbcs.rollback(conn);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Jdbcs.rollback(conn);
//        } finally {
//            IOUtils.closeQuietly(conn);
//        }
//    }

//	@Test
//	public void test7() {
//		Connection conn = TESTDB.getConnection();
//		try {
//			DatabaseDialect dialect = DatabaseDialectFactory.getDialect(conn);
//			String schema = dialect.getSchema(conn);
//			DatabaseTableInfo table1 = dialect.getDatabaseTableInfoForceOne(conn, null, schema, "ECC_ENSURECONTRACTS_R");
//			DatabaseTableInfo table2 = dialect.getDatabaseTableInfoForceOne(conn, null, schema, "ECC_ENSURECONTRACTS");
////			List<DatabaseTableColumn> columns = table1.getColumns();
//			
//			List<DatabaseIndex> primaryIndexs = table1.getPrimaryIndexs();
//			DatabaseIndex pkg = primaryIndexs.get(0);
//			DatabaseTableColumn[] searchDatabaseTableColumn = table1.indexOfColumns(Collections.toArray(pkg.getColumnName()));
//			String[] databaseTableFieldNames = DatabaseTableColumn.toDatabaseTableColumnNames(searchDatabaseTableColumn);
//			List<String> list = Arrays.toList(databaseTableFieldNames);
//			
//			System.out.println(Jdbcs.getDoubleDatabaseTableDeleteFromSql(conn, null, table2.getSchema(), table2.getName(), list, table1.getSchema(), table1.getName(), list));
//			assertTrue(true);
//			
//			Jdbcs.rollback(conn);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Jdbcs.rollback(conn);
//		} finally {
//			IOUtils.closeQuietly(conn);
//		}
//	}

    @Test
    public void test8() throws SQLException {
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
//			assertTrue(dialect.getDatabaseTableDDL(table));
//			assertTrue(table.getIndexs().size());
            List<DatabaseIndex> primaryIndexs = table.getPrimaryIndexs();
//			assertTrue(primaryIndexs.size());

            DatabaseTable clone = table.clone();
//			assertTrue(dialect.getDatabaseTableDDL(clone));
//			assertTrue(clone.getIndexs().size());
//			assertTrue(clone.getPrimaryIndexs().size());

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
    public void test9() {
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
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            dao.close();
        }
    }

    @Test
    public void test10() throws Exception {
        String tablename = "";
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            System.out.println("testing");
            Assert.assertTrue(dao.testConnection());
            System.out.println("finsih test ");

            tablename = "test" + Dates.format17();
            dao.execute("create table " + tablename + "(f1 char(100), f2 char(10) ) ");
            dao.commit();
            System.out.println("finsih create table");

            JdbcQueryStatement qryLastCreditLine = new JdbcQueryStatement(dao.getConnection(), "select * from " + tablename + " a where f1 = ? and f2 < ? ");
            qryLastCreditLine.setParameter("2301052016000008");
            qryLastCreditLine.setParameter("2017-04-25");
            ResultSet result = qryLastCreditLine.query();
            if (result.next()) {
//				int val = result.getInt("credit_line");
//				assertTrue(val + ", " + result.wasNull());
            }

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
