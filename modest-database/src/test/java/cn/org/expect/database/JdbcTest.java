package cn.org.expect.database;

import java.sql.Timestamp;
import java.util.Date;

import cn.org.expect.database.db2.DB2ExportFile;
import cn.org.expect.database.internal.StandardDatabaseProcedure;
import cn.org.expect.database.oracle.OracleDialect;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class JdbcTest {

    @Test
    public void testremoveSchema() {
        Assert.assertEquals("", Jdbc.removeSchema(""));
        Assert.assertEquals("table", Jdbc.removeSchema("table"));
        Assert.assertEquals("table", Jdbc.removeSchema(".table"));
        Assert.assertEquals("table", Jdbc.removeSchema("1.table"));
        Assert.assertEquals("table", Jdbc.removeSchema("schema.table"));
        Assert.assertEquals("table", Jdbc.removeSchema("schema.sdsf.table"));
    }

    @Test
    public void testgetSchema() {
        Assert.assertNull(Jdbc.getSchema(""));
        Assert.assertNull(Jdbc.getSchema("table"));
        Assert.assertNull(Jdbc.getSchema(".table"));
        Assert.assertEquals("1", Jdbc.getSchema("1.table"));
        Assert.assertEquals("schema", Jdbc.getSchema("schema.table"));
        Assert.assertEquals("schema", Jdbc.getSchema("schema.sdsf.table"));
    }

    @Test
    public void testQuote() {
        Assert.assertNull(StringUtils.quote(null));
        Assert.assertEquals("''", StringUtils.quote(""));
        Assert.assertEquals("' '", StringUtils.quote(" "));
    }

    @Test
    public void test3() {
        Assert.assertEquals("ABCABC", StringUtils.toCase("abcABC", false, null));
        Assert.assertEquals("ArrayList[A, B, C]", StringUtils.toString(StringUtils.toCase(ArrayUtils.asList("a", "b", "c"), false, null)));
        Assert.assertEquals("String[A, B, CD]", StringUtils.toString(StringUtils.toCase(new String[]{"a", "b", "cd"}, false, null)));
        Assert.assertEquals("Integer[0, 1, 2]", StringUtils.toString(new Integer[]{0, 1, 2}));
        Assert.assertEquals("int[1, 2, 3]", StringUtils.toString(new int[]{1, 2, 3}));
        Assert.assertEquals("char[A, B, C, 1]", StringUtils.toString(StringUtils.toCase(new char[]{'a', 'b', 'c', '1'}, false, null)));
    }

    @Test
    public void test4() {
        Date date = new Date();
        String time = StringUtils.right(Dates.format21(date), 3); // 毫秒数
        String str = StringUtils.left(Dates.format21(date), 20);
        String str1 = DB2ExportFile.toDB2ExportString(new Timestamp(date.getTime()));
        String result = str + "000" + time;
        Assert.assertEquals(str1, result.replace(':', '.'));
    }

    @Test
    public void test6() {
        String[][] array1 = OracleDialect.resolveDatabaseProcedureParam("procedure name(v1 number(12, 2), dt in varchar2char(100), r  out  int, t char, d in decimal(10,2) )");
        Assert.assertEquals("String[V1, IN, NUMBER(12, 2)]", StringUtils.toString(array1[0]));
        Assert.assertEquals("String[DT, IN, VARCHAR2CHAR(100)]", StringUtils.toString(array1[1]));
        Assert.assertEquals("String[D, IN, DECIMAL(10,2)]", StringUtils.toString(array1[4]));

        String[] array2 = StandardDatabaseProcedure.resolveDatabaseProcedureDDLName("CREATE OR REPLACE PROCEDURE \"LHBB\".\"CUSTAUM_APPEND\" (dt in varchar2)--yyyymmdd \n as");
        Assert.assertEquals("String[\"LHBB\", \"CUSTAUM_APPEND\"]", StringUtils.toString(array2));
    }
}
