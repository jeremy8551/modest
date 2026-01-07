package cn.org.expect.expression;

import cn.org.expect.util.Dates;
import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {

    @Test
    public void test1() {
        Assert.assertTrue(new Expression("true").booleanValue());
        Assert.assertTrue(new Expression("(true)").booleanValue());
        Assert.assertFalse(new Expression("false").booleanValue());
        Assert.assertFalse(new Expression("!true").booleanValue());
        Assert.assertTrue(new Expression("!false").booleanValue());
        Assert.assertTrue(new Expression("true || false").booleanValue());
        Assert.assertTrue(new Expression("false || true").booleanValue());
        Assert.assertTrue(new Expression("(false || true)").booleanValue());
        Assert.assertTrue(new Expression("1 == 1 and 2==2").booleanValue());
        Assert.assertTrue(new Expression("1 == 2 or 2==2").booleanValue());
        Assert.assertFalse(new Expression("1 == 2 or 2==3").booleanValue());
        Assert.assertTrue(new Expression("1 != 2 ").booleanValue());
        Assert.assertTrue(new Expression("1!=2").booleanValue());
        Assert.assertFalse(new Expression("1==2 or 2==3").booleanValue());
        Assert.assertEquals(new Expression("'20200101' + 1day-1day + 1 year - 1 month + 1hour +1minute+1second +1millis").dateValue(), Dates.parse("2020-12-01 01:01:01:001"));
        Assert.assertEquals(new Expression("'20200101' + 1day-1day + 1 year - 1 month + 1hour +1minute+1second +1millis").longValue().longValue(), Dates.parse("2020-12-01 01:01:01:001").getTime());
        Assert.assertTrue(new Expression("1 in (1, 2, 3,5)").booleanValue());
        Assert.assertFalse(new Expression("10 in (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("10 not   in  (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("'10' in ('1' , '2'  ,'3',10,'10')").booleanValue());
        Assert.assertTrue(new Expression("'10' in ( 1, 2  ,3,10,'10' )").booleanValue());
        Assert.assertFalse(new Expression("'10' in ('1' , '2'  ,'3',10)").booleanValue());
        Assert.assertTrue(new Expression("'10'  not   in ('1' , '2'  ,'3',10)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1 in (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.0 in (1.0, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.0 in (1.00, 2, 3,5)").booleanValue());
        Assert.assertFalse(new Expression("1==1 && 1.01 in (1.00, 2, 3,5, '1.01')").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.01    not in (1.00, 2, 3,5, '1.01')").booleanValue());
        Assert.assertTrue(new Expression("'aaabbb' like '.+bbb'").booleanValue());
        Assert.assertTrue(new Expression("'aaa\"bbb' like '.+bbb'").booleanValue());
        Assert.assertTrue(new Expression("'aaabbb' not like '.+bbbb'").booleanValue());
        Assert.assertEquals("\"a\'", new Expression("'\\\"a\\\''").stringValue());
    }

    @Test
    public void test() throws Exception {
        Assert.assertEquals(45, (double) ((Double) new Expression("12.00 + 11 + ( 11*2) ").doubleValue()), 0.0);
        Assert.assertEquals(45, (double) ((Double) new Expression("12.00+11+(11*2)").doubleValue()), 0.0);

        Assert.assertFalse(new Expression("1==2 && 1==1 && 2==2").booleanValue());
        Assert.assertFalse(new Expression("1==1 && 1==2 && 2==2").booleanValue());
        Assert.assertFalse(new Expression("1==1 && 2==2 && 3==4").booleanValue());
        Assert.assertFalse(new Expression("1==1 && 2==2 && 3==3 && 4==3").booleanValue());

        Assert.assertTrue(new Expression("1==2 && 1==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==2 || 1==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==2 || 3==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==1 || 3==1 || 2==3").booleanValue());

        Assert.assertEquals(1, (long) ((Long) new Expression("1==1 ? 1 : 2").longValue()));
        Assert.assertEquals(2, (long) ((Long) new Expression("1==2 ? 1 : 2").longValue()));
        Assert.assertEquals("1", ((String) new Expression("(1==1) ? (1==1 ? '1' : '2') : '3' ").stringValue())); // 1
        Assert.assertEquals("2", ((String) new Expression("(1==1) ? (1==3 ? '1' : '2') : '3' ").stringValue())); // 2
        Assert.assertEquals("3", ((String) new Expression("(1==2) ? (1==3 ? '1' : '2') : '3' ").stringValue())); // 3
        Assert.assertEquals("3", ((String) new Expression("1==2 ? (1==3 ? '1' : '2') : '3' ").stringValue())); // 3
        Assert.assertEquals(":1:):", ((String) new Expression("1==1 ? (1==1 ? ':1:):' : '2:)') : '3:)' ").stringValue())); // 3
        Assert.assertEquals(":1:):", ((String) new Expression("1==1?(1==1?':1:):':'2:)'):'3:)'").stringValue())); // 3

        Assert.assertEquals(1, (long) ((Long) new Expression("1").longValue()));
        Assert.assertEquals(1, (double) ((Double) new Expression("1.0").doubleValue()), 0.0);
        Assert.assertEquals(1.1, ((Double) new Expression("1.1").doubleValue()), 0.0);
        Assert.assertEquals(1.1, ((Double) new Expression("+1.1").doubleValue()), 0.0);
        Assert.assertEquals(-1.1, ((Double) new Expression("-1.1").doubleValue()), 0.0);
        Assert.assertEquals("", ((String) new Expression("''").stringValue()));
        Assert.assertEquals("字符串", ((String) new Expression("'字符串'").stringValue()));
        Assert.assertEquals("字符串是正确的!", ((String) new Expression("'字符串'+'是正确的!'").stringValue()));
        Assert.assertEquals("字符串是正确的!12", ((String) new Expression("'字符串'+'是正确的!' + 12").stringValue()));
        Assert.assertEquals("字符串是正确的!12abc", ((String) new Expression("'字符串'+'是正确的!' + 12+'abc'").stringValue()));
        Assert.assertEquals("字符串是正确的!12abc", ((String) new Expression("'字符串'+'是正确的!'+12+'abc'").stringValue()));
        Assert.assertEquals("aa' \\", ((String) new Expression("'aa'+'\\' \\\\' ").stringValue()));
        Assert.assertFalse((Boolean) new Expression("'1'+'2'=='3'").booleanValue());

        Assert.assertEquals(86400000, (long) ((Long) new Expression("24*3600* 1000").longValue()));
        Assert.assertEquals(86400000, (long) ((Long) new Expression("24*3600*1000").longValue()));
        Assert.assertTrue(((Boolean) new Expression("'1985-02-01'>='1985-02-01'").booleanValue()));
        Assert.assertTrue(((Boolean) new Expression("'1983-02-01'<='1985-02-01'").booleanValue()));
        Assert.assertTrue(((Boolean) new Expression("(11+11)==(10+12)").booleanValue()));
        Assert.assertFalse((Boolean) new Expression("11+11!=10.4+11.6").booleanValue());
        Assert.assertEquals(-1.1234, ((Double) new Expression("10+ -11.1234").doubleValue()), 0.0);
        Assert.assertEquals(-1.1234, ((Double) new Expression("10+-11.1234").doubleValue()), 0.0);

        Assert.assertEquals(4.9e-324, ((Double) new Expression("4.900000e-324").doubleValue()), 0.0);
        Assert.assertEquals(1.797693E308, ((Double) new Expression("1.797693e+308").doubleValue()), 0.0);

        Assert.assertEquals("", new Expression("   " + Expression.STRING_BLOCK + Expression.STRING_BLOCK + "  ").stringValue());
        Assert.assertEquals("", new Expression(Expression.STRING_BLOCK + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals(" ", new Expression(Expression.STRING_BLOCK + " " + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("1", new Expression(Expression.STRING_BLOCK + "1" + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("12", new Expression(Expression.STRING_BLOCK + "12" + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("\"12", new Expression(Expression.STRING_BLOCK + "\"12" + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("'12'", new Expression(Expression.STRING_BLOCK + "'12'" + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("\"\"'12'", new Expression(Expression.STRING_BLOCK + "\"\"'12'" + Expression.STRING_BLOCK).stringValue());
        Assert.assertEquals("\"12\n\r\n\r", new Expression(Expression.STRING_BLOCK + "\"12\n\r\n\r" + Expression.STRING_BLOCK).stringValue());
    }
}
