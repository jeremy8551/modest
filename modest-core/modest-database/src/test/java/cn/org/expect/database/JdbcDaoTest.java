package cn.org.expect.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.internal.StandardDatabaseIndex;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 测试 JdbcDao
 */
@RunWith(ModestRunner.class)
public class JdbcDaoTest {

    public final static String TABLE_NAME = "test_table_name_temp".toUpperCase();

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
            DatabaseDialect dialect = dao.getDialect();
            if (dialect.containsTable(dao.getConnection(), null, Jdbc.getSchema(TABLE_NAME), Jdbc.removeSchema(TABLE_NAME))) {
                dao.execute("drop table " + TABLE_NAME);
            }

            dao.execute("create table " + TABLE_NAME + "  (id int, name char(100)  )");
            dao.closeStatement();

            dao.execute("insert into " + TABLE_NAME + "  (id, name) values (1, '名字1')");
            JdbcDao.execute(dao.getConnection(), "insert into " + TABLE_NAME + "  (id, name) values (2, '名字2')");

            dao.commit();
            this.connection = dao.getConnection();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testgetSchema() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            DatabaseTable table = dao.getTable(dao.getCatalog(), dao.getSchema(), TABLE_NAME);
            String schema = table.getSchema();
            assertTrue(schema.equalsIgnoreCase(dao.getSchema()));
            assertTrue(dao.containsTable(null, schema, table.getName()));
            assertNotNull(dao.getTable(null, schema, table.getName()));
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
    public void testExistsConnection() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            assertTrue(dao.existsConnection());
            assertTrue(true);
            dao.rollback();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testConnection() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            assertTrue(dao.testConnection());
            dao.rollback();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.commit();
            dao.close();
        }
    }

    @Test
    public void testQueryFirstRowFirstColByJdbcString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Integer num = (Integer) dao.queryFirstRowFirstCol("select id, name from " + TABLE_NAME + " order by id desc");
            dao.commit();
            assertEquals(2, num.intValue());
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQuery() throws SQLException {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            JdbcQueryStatement query = dao.query("select id, name from " + TABLE_NAME + " where id = ? and name = ? ", -1, -1, 1, "名字1");
            query.query();
            assertTrue(query.next());
            dao.commit();
            assertTrue(true);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Integer count = dao.queryCount("select count(*) from " + TABLE_NAME + " with ur "); // 查询结果集笔数
            System.out.println("total is " + count);
            Assert.assertNotNull(count);
            Assert.assertTrue(count > 0);
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryFirstColumnByJdbc() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            List<String> list = dao.queryFirstColumn("select id, name from " + TABLE_NAME + " order by id desc");
            dao.commit();
            assertTrue(list.size() == 2 && StringUtils.objToStr(list.get(0)).equals("2") && StringUtils.objToStr(list.get(1)).equals("1"));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryCountByJdbcString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            int num = dao.queryCount("select count(*) from " + TABLE_NAME + " ");
            dao.commit();
            assertEquals(2, num);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryMapByJdbcString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Map<String, String> map = dao.queryMap("select id, name from " + TABLE_NAME + " ");
            dao.commit();
            assertEquals(2, map.size());
            assertEquals("名字1", map.get("1"));
            assertEquals("名字2", map.get("2"));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryMapByJdbcStringIntInt() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Map<String, String> map = dao.queryMap("select id, name from " + TABLE_NAME + " ", 1, 2);
            dao.commit();
            assertEquals(2, map.size());
            assertEquals("名字1", map.get("1"));
            assertEquals("名字2", map.get("2"));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryMapByJdbcStringStringString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Map<String, String> map = dao.queryMap("select id, name from " + TABLE_NAME + " ", "id", "name");
            dao.commit();
            assertEquals(2, map.size());
            assertEquals("名字1", map.get("1"));
            assertEquals("名字2", map.get("2"));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExecuteUpdateByJdbcStringArray() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            String[] array2 = new String[]{"update " + TABLE_NAME + " set name='名字11' where id = 1", "update " + TABLE_NAME + " set name='名字22' where id = 2"};
            int[] array = dao.executeUpdate(array2);
            dao.commit();
            assertEquals(2, array.length);
            assertEquals(1, array[0]);
            assertEquals(1, array[1]);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExecuteUpdateByJdbcListOfString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            List<String> list = ArrayUtils.asList("update " + TABLE_NAME + " set name='名字11' where id = 1", "update " + TABLE_NAME + " set name='名字22' where id = 2");
            int[] array = dao.executeUpdate(list);
            dao.commit();
            assertTrue(array.length == 2 && array[0] == 1 && array[1] == 1);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExecuteUpdateByJdbcString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            int val = dao.executeUpdate("update " + TABLE_NAME + " set name='名字11' where id = 1");
            dao.commit();
            assertEquals(1, val);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExecuteByJdbcString() {
    }

    @Test
    public void testQueryListMapByJdbc() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            List<Map<String, String>> list = dao.queryListMap("select * from " + TABLE_NAME + " order by id asc");

            assertTrue(list.size() == 2 //
                    && list.get(0).get("id").equals("1") //
                    && list.get(0).get("name").equals("名字1") //
                    && list.get(1).get("id").equals("2") //
                    && list.get(1).get("name").equals("名字2") //
            );
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
    public void testResultToList() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            PreparedStatement ps = dao.getConnection().prepareStatement("select * from " + TABLE_NAME + " order by id asc");
            ResultSet result = ps.executeQuery();
            List<Map<String, String>> list = Jdbc.resultToList(result);

            assertTrue(list.size() == 2 //
                    && StringUtils.trimBlank(list.get(0).get("id")).equals("1") //
                    && StringUtils.trimBlank(list.get(0).get("name")).equals("名字1") //
                    && StringUtils.trimBlank(list.get(1).get("id")).equals("2") //
                    && StringUtils.trimBlank(list.get(1).get("name")).equals("名字2") //
            );
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
    public void testCurrentRowToMap() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            PreparedStatement ps = dao.getConnection().prepareStatement("select * from " + TABLE_NAME + " order by id asc");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                Map<String, String> map = Jdbc.resultToMap(result, true);
                assertEquals("1", map.get("id"));
                assertEquals("名字1", StringUtils.trimBlank(map.get("name")));
            }
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
    public void testResultToMap() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            JdbcQueryStatement query = new JdbcQueryStatement(dao.getConnection(), "select id, name from " + TABLE_NAME + " order by id asc");
            ResultSet result = query.query();
            Map<String, String> map = Jdbc.resultToMap(result, 1, 2);
            dao.commit();
            assertEquals(2, map.size());
            assertEquals("名字1", StringUtils.trimBlank(map.get("1")));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExistsTable() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            assertTrue(dao.containsTable(null, null, TABLE_NAME));
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
    public void testExecuteByJdbcQuietlyString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeQuietly("drop table tabletestseljlskjdflk ");
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
    public void testExecuteByJdbcQuiet() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeQuiet("drop table tabletestseljlskjdflk ");
            dao.rollback();
            dao.close();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testExecuteUpdateByJdbcQuietly() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeUpdateQuietly("delete from tanbl lsdkfjlkjlksdjf ");
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
    public void testExecuteUpdateByJdbcQuiet() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            dao.executeUpdateQuiet("update " + TABLE_NAME + "_TSET set name='' where 2=1");
            dao.rollback();
            dao.close();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testExecuteUpdateByJdbcConnectionString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            int result = JdbcDao.executeUpdate(dao.getConnection(), "delete from " + TABLE_NAME + " where id = 1");
            assertEquals(1, result);
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
    public void testExecuteByJdbcConnectionString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            boolean result = JdbcDao.execute(dao.getConnection(), "delete from " + TABLE_NAME + " where id = 2");
            dao.commit();
            assertFalse(result);
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testExecuteCreateTableConnectionDatabaseDialectDatabaseTableInfoBoolean() {
        EasyContext context = this.context;
        JdbcDao dao = new JdbcDao(context, this.connection);
        try {
            Connection conn = dao.getConnection();
            DatabaseDialect dialect = context.getBean(DatabaseDialect.class, conn);
            List<DatabaseTable> list = dialect.getTable(conn, dao.getCatalog(), dao.getSchema(), Jdbc.removeSchema(TABLE_NAME));
            if (list.size() > 1) {
                throw new RuntimeException();
            }

            DatabaseTable table = list.get(0);
            DatabaseTableDDL ddl = dao.toDDL(table);
            dao.execute("drop table " + table.getFullName());
            dao.createTable(ddl);
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
    public void testExecuteCreateTableConnectionDatabaseDialectDatabaseTableInfoBooleanBooleanBoolean() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Connection conn = dao.getConnection();
            DatabaseDialect dialect = this.context.getBean(DatabaseDialect.class, conn);
            List<DatabaseTable> list = dialect.getTable(conn, null, dao.getSchema(), Jdbc.removeSchema(TABLE_NAME));
            if (list.size() > 1) {
                throw new RuntimeException();
            }

            DatabaseTable table = list.get(0);
            DatabaseTableDDL ddl = dao.toDDL(table);
            dao.execute("drop table " + table.getFullName());
            dao.createTable(ddl);
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

//	@Test
//	public void testExecuteDropTableIndexConnectionDatabaseDialectStringStringBoolean() {
//		JdbcDao dao = new JdbcDao(Env.getConnection());
//		try {
//			dao.dropIndex(Jdbcs.getSchemaFromTableName(tableName), Jdbcs.removeSchemaFromTableName(tableName));
//			dao.commit();
//		} catch (Exception e) {
//			dao.rollback();
//			e.printStackTrace();
//			Assert.fail();
//		} finally {
//			dao.close();
//		}
//	}

    @Test
    public void testExecuteDropTableIndexConnectionDatabaseTableInfoBoolean() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Connection conn = dao.getConnection();
            DatabaseDialect dialect = dao.getDialect();
            List<DatabaseTable> list = dialect.getTable(conn, dao.getCatalog(), dao.getSchema(), Jdbc.removeSchema(TABLE_NAME));
            if (list.size() > 1) {
                throw new RuntimeException();
            }

            DatabaseTable table = list.get(0);
            dao.dropIndex(table);
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
    public void testExecuteCreateTableIndexConnectionDatabaseDialectDatabaseTableInfoBoolean() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            String schema = dao.getSchema();

            StandardDatabaseIndex idx = new StandardDatabaseIndex();
            idx.setTableCatalog(null);
            idx.setTableSchema(schema);
            idx.setTableName(TABLE_NAME);
            idx.setTableFullName(dao.getDialect().toTableName(idx.getTableCatalog(), idx.getTableSchema(), idx.getTableName()));
            idx.setSchema(schema);
            idx.setName("idxnametest");
            idx.setFullName(dao.getDialect().toTableName(null, idx.getSchema(), idx.getName()));
            idx.setColumnNames(ArrayUtils.asList("id"));
            idx.setSort(ArrayUtils.asList(DatabaseIndex.INDEX_ASC));

            DatabaseDDL ddl = dao.getDialect().toDDL(dao.getConnection(), idx, false);
            dao.execute(ddl);
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
    public void testExecuteCreateTableIndexConnectionDatabaseDialectDatabaseIndexBoolean() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            String schema = dao.getSchema();

            StandardDatabaseIndex index = new StandardDatabaseIndex();
            index.setTableCatalog(null);
            index.setTableSchema(schema);
            index.setTableName(TABLE_NAME);
            index.setTableFullName(dao.getDialect().toTableName(index.getTableCatalog(), index.getTableSchema(), index.getTableName()));
            index.setName("idxnametest");
            index.setSchema(schema);
            index.setFullName(dao.getDialect().toIndexName(null, index.getSchema(), index.getName()));
            index.setColumnNames(ArrayUtils.asList("id"));
            index.setSort(ArrayUtils.asList(DatabaseIndex.INDEX_ASC));

            DatabaseDDL ddl = dao.getDialect().toDDL(dao.getConnection(), index, false);
            dao.execute(ddl);
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
    public void testQueryListMapsByJdbc() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            List<Map<String, String>> list = JdbcDao.queryListMaps(dao.getConnection(), "select * from " + TABLE_NAME + " order by id asc");
            dao.commit();
            Assert.assertTrue(list.size() == 2 && StringUtils.trimBlank(list.get(0).get("id")).equals("1"));
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    @Test
    public void testQueryCountByJdbcConnectionString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Connection conn = dao.getConnection();
            assertEquals(2, JdbcDao.queryCount(conn, "select count(*) from " + TABLE_NAME).intValue());
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
    public void testQueryCountByJdbcDataSourceString() throws SQLException {
        DataSourceWrapper dataSource = new DataSourceWrapper(this.connection);
        try {
            assertEquals(2, JdbcDao.queryCount(dataSource, "select count(*) from " + TABLE_NAME).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testQueryFirstRowFirstColByJdbcConnectionString() {
        JdbcDao dao = new JdbcDao(this.context, this.connection);
        try {
            Connection conn = dao.getConnection();
            Object id = JdbcDao.queryFirstRowFirstCol(conn, "select id, name from " + TABLE_NAME + " order by id asc");
            assertTrue(id != null && StringUtils.trimBlank(StringUtils.objToStr(id)).equals("1"));
            dao.commit();
        } catch (Exception e) {
            dao.rollback();
            e.printStackTrace();
            Assert.fail();
        } finally {
            dao.close();
        }
    }

    private static class DataSourceWrapper implements DataSource {

        private Connection conn;

        public DataSourceWrapper(Connection conn) {
            super();
            this.conn = conn;
        }

        @Override
        public Connection getConnection() {
            return this.conn;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }
}
