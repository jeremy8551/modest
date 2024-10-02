package cn.org.expect.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.annotation.DatabaseRunner;
import cn.org.expect.ioc.EasyContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DatabaseRunner.class)
public class QueryManagerTest {

    public final static String TABLE_NAME = "test_table_name_temp".toUpperCase();

    /** 容器上下文信息 */
    @EasyBean
    public EasyContext context;

    /** 数据库连接 */
    @EasyBean
    public Connection connection;

    @Before
    public void setUp2() throws SQLException {
        try {
            DatabaseDialect dialect = this.context.getBean(DatabaseDialect.class, this.connection);
            if (dialect.containsTable(this.connection, null, Jdbc.getSchema(TABLE_NAME), Jdbc.removeSchema(TABLE_NAME))) {
                JdbcDao.execute(this.connection, "drop table " + TABLE_NAME);
            }

            JdbcDao.execute(this.connection, "create table " + TABLE_NAME + "  (id int, name char(100)  )");
            JdbcDao.execute(this.connection, "insert into " + TABLE_NAME + "  (id, name) values (1, '名字1')");
            JdbcDao.execute(this.connection, "insert into " + TABLE_NAME + "  (id, name) values (2, '名字2')");

            this.connection.commit();
        } catch (Exception e) {
            this.connection.rollback();
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testInitConnectionStringIntInt() throws SQLException {
        JdbcQueryStatement query = new JdbcQueryStatement(this.connection, "select * from " + TABLE_NAME, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet result = query.query();
        String[] colNames = Jdbc.getColumnName(result);
        while (query.next()) {
            Iterator<String> it = Arrays.asList(colNames).iterator();
            StringBuilder buf = new StringBuilder();
            while (it.hasNext()) {
                String name = it.next();
                buf.append(result.getObject(name));
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            System.out.println(buf);
        }

        this.connection.commit();
    }
}
