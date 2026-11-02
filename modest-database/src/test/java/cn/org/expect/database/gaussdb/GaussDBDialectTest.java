package cn.org.expect.database.gaussdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.export.converter.StringConverter;
import cn.org.expect.database.export.converter.IntegerConverter;
import cn.org.expect.database.export.converter.LongConverter;
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

    /**
     * 高斯驱动返回的 bpchar 类型应复用 CHAR 的字符串导出逻辑。
     */
    @Test
    public void testExportBpchar() {
        GaussDBDialect dialect = new GaussDBDialect();
        JdbcConverterMapper converters = dialect.getObjectConverters();
        Assert.assertTrue(converters.contains("bpchar"));
        Assert.assertTrue(converters.contains("BPCHAR"));
        Assert.assertTrue(converters.get("bpchar") instanceof StringConverter);
    }

    /**
     * 自增类型别名应按底层整数宽度进行导出和导入。
     */
    @Test
    public void testSerialConverters() {
        GaussDBDialect dialect = new GaussDBDialect();
        for (String type : new String[]{"smallserial", "serial2", "serial", "serial4"}) {
            Assert.assertTrue(dialect.getObjectConverters().get(type) instanceof IntegerConverter);
            Assert.assertTrue(dialect.getStringConverters().get(type) instanceof cn.org.expect.database.load.converter.IntegerConverter);
        }
        for (String type : new String[]{"bigserial", "serial8"}) {
            Assert.assertTrue(dialect.getObjectConverters().get(type) instanceof LongConverter);
            Assert.assertTrue(dialect.getStringConverters().get(type) instanceof cn.org.expect.database.load.converter.LongConverter);
        }
    }

    /**
     * 扩展类型与动态类型均应创建独立的文本十六进制转换器。
     */
    @Test
    public void testExtendedTypes() {
        GaussDBDialect dialect = new GaussDBDialect();
        for (String type : new String[]{"timestamptz", "timetz", "interval", "smalldatetime", "nvarchar2", "json", "jsonb", "xml", "uuid", "money", "bit", "varbit", "inet", "cidr", "macaddr", "point", "line", "lseg", "box", "path", "polygon", "circle", "_int4", "_text", "custom_enum", "custom_domain", "custom_composite"}) {
            cn.org.expect.database.export.converter.TextConverter export = dialect.getObjectConverters().get(type);
            cn.org.expect.database.load.converter.TextConverter load = dialect.getStringConverters().get(type);
            Assert.assertNotNull(export);
            Assert.assertNotNull(load);
            Assert.assertNotSame(export, dialect.getObjectConverters().get(type));
        }
        Assert.assertNull(dialect.getObjectConverters().get(""));
    }

    private DatabaseTableColumn newColumn(String name) {
        StandardDatabaseTableColumn column = new StandardDatabaseTableColumn();
        column.setName(name);
        return column;
    }
}
