package cn.org.expect.database;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class SQLTest {

    @Test
    public void testIndexSqlBrackedEnd() {
        Assert.assertEquals(-1, SQL.indexOfParenthes("0123456789", 0));
        Assert.assertEquals(-1, SQL.indexOfParenthes("0123456789", 9));
        Assert.assertEquals(9, SQL.indexOfParenthes("012345678)", 0));
        Assert.assertEquals(9, SQL.indexOfParenthes("01(3)5()8)", 0));
        Assert.assertEquals(9, SQL.indexOfParenthes("0((3))()8)", 0));
    }

    @Test
    public void testIndexSqlBraceEnd() {
        Assert.assertEquals(-1, SQL.indexOfBrace("0123456789", 0));
        Assert.assertEquals(1, SQL.indexOfBrace("0}23456789", 0));
        Assert.assertEquals(9, SQL.indexOfBrace("012345678}", 0));
        Assert.assertEquals(9, SQL.indexOfBrace("0{2}4{67}}", 0));
        Assert.assertEquals(9, SQL.indexOfBrace("0{2}4{{}}}", 0));
    }

    @Test
    public void testIndexSqlEndOfAnnotation() {
        Assert.assertEquals(1, SQL.indexOfAnnotation("--", 0));
        Assert.assertEquals(2, SQL.indexOfAnnotation("--2", 0));
        Assert.assertEquals(2, SQL.indexOfAnnotation("--\r", 0));
        Assert.assertEquals(2, SQL.indexOfAnnotation("--\n", 0));
        Assert.assertEquals(3, SQL.indexOfAnnotation("--\r\n", 0));
        Assert.assertEquals(3, SQL.indexOfAnnotation("--\r\n4", 0));
        Assert.assertEquals(5, SQL.indexOfAnnotation("--234\n4", 0));
        Assert.assertEquals(5, SQL.indexOfAnnotation("0--34\n4", 1));
        Assert.assertEquals(6, SQL.indexOfAnnotation("01--45\n7", 2));
        Assert.assertEquals(3, SQL.indexOfAnnotation("/**/", 0));
        Assert.assertEquals(4, SQL.indexOfAnnotation("/*2*/", 0));
        Assert.assertEquals(4, SQL.indexOfAnnotation("/*2*/5", 0));
        Assert.assertEquals(6, SQL.indexOfAnnotation("/*234*/7", 0));
        Assert.assertEquals(12, SQL.indexOfAnnotation("/*2\n4\r\n78\n0*/7", 0));
        Assert.assertEquals(13, SQL.indexOfAnnotation("0/*2\n4\r\n78\n0*/7", 1));
        Assert.assertEquals(14, SQL.indexOfAnnotation("01/*2\n4\r\n78\n0*/7", 2));
    }

    @Test
    public void testIndexSqlIgnoreTextOrAnnotation() {
        Assert.assertEquals(-1, SQL.indexOf("", "proc", 0, true));
        Assert.assertEquals(0, SQL.indexOf("a", "a", 0, false));
        Assert.assertEquals(-1, SQL.indexOf("a", "A", 0, false));
        Assert.assertEquals(0, SQL.indexOf("a", "A", 0, true));
        Assert.assertEquals(0, SQL.indexOf("aB", "Ab", 0, true));
        Assert.assertEquals(0, SQL.indexOf("AB", "Ab", 0, true));
        Assert.assertEquals(0, SQL.indexOf("proc", "proc", 0, false));
        Assert.assertEquals(SQL.indexOf("0123456789012345proc", "proc", 0, true), 16);
        Assert.assertEquals(16, SQL.indexOf("012345678901234.proc", "proc", 0, true));
        Assert.assertEquals(16, SQL.indexOf("--\n345678901234.proc", "proc", 0, true));
        Assert.assertEquals(16, SQL.indexOf("--\n34--78\r\n1234.proc", "proc", 0, true));
        Assert.assertEquals(35, SQL.indexOf("--\n34--78\r\n1234'proc','asdf.proc'd.proc", "proc", 0, true));
        Assert.assertEquals(45, SQL.indexOf("--\n34--78\r\n1234'proc','asdf.proc'd./*123*/12.proc", "proc", 0, true));
        Assert.assertEquals(16, SQL.indexOf("--\n3/*6\n8\n0*/34.proc 123456789", "proc", 0, true));
    }

    @Test
    public void testToDatabaseIdentifier() {
        Assert.assertNull(SQL.toIdentifier(null));
        Assert.assertEquals("", SQL.toIdentifier("\"\""));
        Assert.assertEquals("A", SQL.toIdentifier(" \"a\" " + StringUtils.FULLWIDTH_BLANK));
    }

    @Test
    public void testSplitSqlByBlank() {
        String str = "dt in varchar2char(100), v2 in  number(12,  2), r  out  int, a out number( 10,2), t char, d in decimal(10,2),    ' ,  ', m in ('   '||a.loanid),   1'   ', ";
        String[] array = SQL.split(str, ',');
        Assert.assertEquals("dt in varchar2char(100)  v2 in  number(12,  2)  r  out  int  a out number( 10,2)  t char  d in decimal(10,2)     ' ,  '  m in ('   '||a.loanid)    1'   '  ", StringUtils.join(array, " "));

        String[] b = SQL.splitByBlank(str);
        Assert.assertEquals("dt in varchar2char(100), v2 in number(12,  2), r out int, a out number( 10,2), t char, d in decimal(10,2), ' ,  ', m in ('   '||a.loanid), 1'   ', ", StringUtils.join(b, " "));

        String[] b1 = SQL.splitByBlank("'1 ; 2 ' 2;3 4", ';');
        Assert.assertTrue(b1.length == 4 && b1[0].equals("'1 ; 2 '") && b1[1].equals("2") && b1[2].equals("3") && b1[3].equals("4"));
    }

    @Test
    public void testSplitSqlStringChar() {
        String sql = "";
        sql += "select a.name, b.id ,c id_type from table as a, table as b, table as c ";
        sql += " where a.name = b.name ";
        sql += " and a.id = c.id  ";
        sql += " ";

        String[] array = SQL.split(sql, ',');
        Assert.assertEquals("select a.name", array[0]);
        Assert.assertEquals(" b.id ", array[1]);
        Assert.assertEquals("c id_type from table as a", array[2]);
        Assert.assertEquals(" table as b", array[3]);
        Assert.assertEquals(" table as c  where a.name = b.name  and a.id = c.id   ", array[4]);
    }

    @Test
    public void testSplitSqlStringCharListOfString() {
        String sql = "";
        sql += "select a.name, b.id ,c id_type from table as a, table as b, table as c ";
        sql += " where a.name = b.name ";
        sql += " and a.id = c.id  ";
        sql += " ";

        List<String> list = new ArrayList<String>();
        SQL.split(sql, ',', list);
        Assert.assertEquals("select a.name", list.get(0));
        Assert.assertEquals(" b.id ", list.get(1));
        Assert.assertEquals("c id_type from table as a", list.get(2));
        Assert.assertEquals(" table as b", list.get(3));
        Assert.assertEquals(" table as c  where a.name = b.name  and a.id = c.id   ", list.get(4));
    }

    @Test
    public void testSplitSqlStringCollectionOfStringBoolean() {
        String sql = "O_ALS_CUSTOMER_RELATIVE_QYZX D      ON C.CUSTID = D.CustomerID    AND D.RELATIONSHIP in ('0100', '0102', '0103')";
        String[] array = SQL.split(sql, ArrayUtils.asList("on"), true);
        Assert.assertEquals("String[O_ALS_CUSTOMER_RELATIVE_QYZX D      ,  C.CUSTID = D.CustomerID    AND D.RELATIONSHIP in ('0100', '0102', '0103')]", StringUtils.toString(array));
    }

    @Test
    public void testSplitSqlStringCollectionOfStringCollectionOfStringBoolean() {
        List<String> list = new ArrayList<String>();
        SQL.split("O_ALS_CUSTOMER_RELATIVE_QYZX D      ON C.CUSTID = D.CustomerID    AND D.RELATIONSHIP in ('0100', '0102', '0103')", ArrayUtils.asList("on"), true, list);
        Assert.assertEquals("ArrayList[O_ALS_CUSTOMER_RELATIVE_QYZX D      ,  C.CUSTID = D.CustomerID    AND D.RELATIONSHIP in ('0100', '0102', '0103')]", StringUtils.toString(list));
    }

    @Test
    public void testSplitSqlByUnion() {
        String sql = "";
        sql += "    select CONTRACT_NO from ECC_ENSURECONTRACTS_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;
        sql += " UNION ALL" + Settings.LINE_SEPARATOR;
        sql += "    select CONTRACT_NO from ECC_IMPAWNCONTRACT_I  a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;
        sql += " UNION" + Settings.LINE_SEPARATOR;
        sql += "    select CONTRACT_NO from ECC_PLEDGECONTRACTS_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;
        sql += " UNION ALL   " + Settings.LINE_SEPARATOR;
        sql += "    select CONTRACT_NO from ECC_ENSURECONTRACTS_BP_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;
        sql += " UNION ALL" + Settings.LINE_SEPARATOR;
        sql += "    select CONTRACT_NO from ECC_IMPAWNCONTRACT_BP_I  a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;
        sql += " UNION" + Settings.LINE_SEPARATOR;
        sql += "    select all CONTRACT_NO from ECC_PLEDGECONTRACTS_BP_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )" + Settings.LINE_SEPARATOR;

        List<String> list = new ArrayList<String>();
        List<String> deimiters = new ArrayList<String>();
        SQL.splitByUnion(sql, deimiters, list);

        Assert.assertEquals("select CONTRACT_NO from ECC_ENSURECONTRACTS_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )", list.get(0).trim());
        Assert.assertEquals("select CONTRACT_NO from ECC_IMPAWNCONTRACT_I  a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )", list.get(1).trim());
        Assert.assertEquals("select all CONTRACT_NO from ECC_PLEDGECONTRACTS_BP_I a where TRUSTLOAN_TYPE='1' and not exists (select 1 from ECC_LOANCONTRACTS_I b where a.CONTRACT_NO = b.LOANCONT_NO )", list.get(5).trim());
    }

    @Test
    public void test11() {
        Assert.assertFalse(SQL.isFieldName(null));
        Assert.assertFalse(SQL.isFieldName(""));
        Assert.assertFalse(SQL.isFieldName("1"));
        Assert.assertTrue(SQL.isFieldName("a"));
        Assert.assertFalse(SQL.isFieldName("_"));
        Assert.assertTrue(SQL.isFieldName("_1"));
    }
}
