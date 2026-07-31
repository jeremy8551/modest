package cn.org.expect.database.gaussdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.internal.StandardDatabaseTableColumn;
import org.junit.Assert;
import org.junit.Test;

public class GaussDBDialectTest {

    @Test
    public void testParseJdbcUrl() {
        GaussDBDialect dialect = new GaussDBDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:gaussdb://127.0.0.1:5432/postgres?user=gaussdb&password=secret&currentSchema=public");

        Assert.assertEquals(1, list.size());
        DatabaseURL url = list.get(0);
        Assert.assertEquals("127.0.0.1", url.getHostname());
        Assert.assertEquals("5432", url.getPort());
        Assert.assertEquals("postgres", url.getDatabaseName());
        Assert.assertEquals("gaussdb", url.getType());
        Assert.assertEquals(5, url.toProperties().size());
    }

    @Test
    public void testParseJdbcUrlUseDefaultPort() {
        GaussDBDialect dialect = new GaussDBDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:opengauss://127.0.0.1/postgres");

        Assert.assertEquals(1, list.size());
        DatabaseURL url = list.get(0);
        Assert.assertEquals("127.0.0.1", url.getHostname());
        Assert.assertEquals("5432", url.getPort());
        Assert.assertEquals("postgres", url.getDatabaseName());
        Assert.assertEquals("opengauss", url.getType());
    }

    @Test
    public void testParseJdbcUrlUseMultipleHosts() {
        GaussDBDialect dialect = new GaussDBDialect();
        List<DatabaseURL> list = dialect.parseJdbcUrl("jdbc:gaussdb://127.0.0.1:5432,127.0.0.2:15432/postgres?ssl=true");

        Assert.assertEquals(2, list.size());
        Assert.assertEquals("127.0.0.1", list.get(0).getHostname());
        Assert.assertEquals("5432", list.get(0).getPort());
        Assert.assertEquals("127.0.0.2", list.get(1).getHostname());
        Assert.assertEquals("15432", list.get(1).getPort());
        Assert.assertEquals(3, list.get(0).toProperties().size());
    }

    @Test
    public void testGenerateMergeStatement() {
        GaussDBDialect dialect = new GaussDBDialect();
        List<DatabaseTableColumn> columns = new ArrayList<DatabaseTableColumn>();
        columns.add(this.newColumn("id"));
        columns.add(this.newColumn("name"));
        columns.add(this.newColumn("age"));

        List<String> mergeColumn = new ArrayList<String>();
        mergeColumn.add("id");

        String sql = dialect.generateMergeStatement("public.t_user", columns, mergeColumn);
        Assert.assertEquals("INSERT INTO public.t_user (id, name, age) VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET name=excluded.name, age=excluded.age", sql);
    }

    @Test
    public void testSqlStateException() {
        GaussDBDialect dialect = new GaussDBDialect();

        Assert.assertTrue(dialect.isOverLengthException(new SQLException("value too long", "22001")));
        Assert.assertTrue(dialect.isPrimaryRepeatException(new SQLException("duplicate key", "23505")));
        Assert.assertTrue(dialect.isIndexExistsException(new SQLException("relation already exists", "42P07")));
        Assert.assertTrue(dialect.isRebuildTableException(new SQLException("syntax error", "42601")));
    }

    private DatabaseTableColumn newColumn(String name) {
        StandardDatabaseTableColumn column = new StandardDatabaseTableColumn();
        column.setName(name);
        return column;
    }
}
