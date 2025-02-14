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
        Assert.assertTrue(new Expression("true || false").booleanValue());
        Assert.assertTrue(new Expression("false || true").booleanValue());
        Assert.assertTrue(new Expression("(false || true)").booleanValue());
        Assert.assertTrue(new Expression("1 == 1 and 2==2").booleanValue());
        Assert.assertTrue(new Expression("1 == 2 or 2==2").booleanValue());
        Assert.assertTrue(!new Expression("1 == 2 or 2==3").booleanValue());
        Assert.assertTrue(new Expression("1 != 2 ").booleanValue());
        Assert.assertTrue(new Expression("1!=2").booleanValue());
        Assert.assertTrue(!new Expression("1==2 or 2==3").booleanValue());
        Assert.assertTrue(new Expression("'20200101' + 1day-1day + 1 year - 1 month + 1hour +1minute+1second +1millis").dateValue().equals(Dates.parse("2020-12-01 01:01:01:001")));
        Assert.assertTrue(new Expression("'20200101' + 1day-1day + 1 year - 1 month + 1hour +1minute+1second +1millis").longValue().longValue() == Dates.parse("2020-12-01 01:01:01:001").getTime());
        Assert.assertTrue(new Expression("1 in (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(!new Expression("10 in (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("10 not   in  (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("'10' in ('1' , '2'  ,'3',10,'10')").booleanValue());
        Assert.assertTrue(new Expression("'10' in ( 1, 2  ,3,10,'10' )").booleanValue());
        Assert.assertTrue(!new Expression("'10' in ('1' , '2'  ,'3',10)").booleanValue());
        Assert.assertTrue(new Expression("'10'  not   in ('1' , '2'  ,'3',10)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1 in (1, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.0 in (1.0, 2, 3,5)").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.0 in (1.00, 2, 3,5)").booleanValue());
        Assert.assertTrue(!new Expression("1==1 && 1.01 in (1.00, 2, 3,5, '1.01')").booleanValue());
        Assert.assertTrue(new Expression("1==1 && 1.01    not in (1.00, 2, 3,5, '1.01')").booleanValue());
    }

    @Test
    public void test() throws Exception {
        Assert.assertTrue(((Double) new Expression("12.00 + 11 + ( 11*2) ").doubleValue()) == 45);
        Assert.assertTrue(((Double) new Expression("12.00+11+(11*2)").doubleValue()) == 45);

        Assert.assertTrue(!new Expression("1==2 && 1==1 && 2==2").booleanValue());
        Assert.assertTrue(!new Expression("1==1 && 1==2 && 2==2").booleanValue());
        Assert.assertTrue(!new Expression("1==1 && 2==2 && 3==4").booleanValue());
        Assert.assertTrue(!new Expression("1==1 && 2==2 && 3==3 && 4==3").booleanValue());

        Assert.assertTrue(new Expression("1==2 && 1==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==2 || 1==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==2 || 3==1 || 2==2").booleanValue());
        Assert.assertTrue(new Expression("1==1 || 3==1 || 2==3").booleanValue());

        Assert.assertTrue(((Long) new Expression("1==1 ? 1 : 2").longValue()) == 1);
        Assert.assertTrue(((Long) new Expression("1==2 ? 1 : 2").longValue()) == 2);
        Assert.assertTrue(((String) new Expression("(1==1) ? (1==1 ? '1' : '2') : '3' ").stringValue()).equals("1")); // 1
        Assert.assertTrue(((String) new Expression("(1==1) ? (1==3 ? '1' : '2') : '3' ").stringValue()).equals("2")); // 2
        Assert.assertTrue(((String) new Expression("(1==2) ? (1==3 ? '1' : '2') : '3' ").stringValue()).equals("3")); // 3
        Assert.assertTrue(((String) new Expression("1==2 ? (1==3 ? '1' : '2') : '3' ").stringValue()).equals("3")); // 3
        Assert.assertTrue(((String) new Expression("1==1 ? (1==1 ? ':1:):' : '2:)') : '3:)' ").stringValue()).equals(":1:):")); // 3
        Assert.assertTrue(((String) new Expression("1==1?(1==1?':1:):':'2:)'):'3:)'").stringValue()).equals(":1:):")); // 3

        Assert.assertTrue(((Long) new Expression("1").longValue()) == 1);
        Assert.assertTrue(((Double) new Expression("1.0").doubleValue()) == 1);
        Assert.assertTrue(((Double) new Expression("1.1").doubleValue()) == 1.1);
        Assert.assertTrue(((Double) new Expression("+1.1").doubleValue()) == 1.1);
        Assert.assertTrue(((Double) new Expression("-1.1").doubleValue()) == -1.1);
        Assert.assertTrue(((String) new Expression("''").stringValue()).equals(""));
        Assert.assertTrue(((String) new Expression("'字符串'").stringValue()).equals("字符串"));
        Assert.assertTrue(((String) new Expression("'字符串'+'是正确的!'").stringValue()).equals("字符串是正确的!"));
        Assert.assertTrue(((String) new Expression("'字符串'+'是正确的!' + 12").stringValue()).equals("字符串是正确的!12"));
        Assert.assertTrue(((String) new Expression("'字符串'+'是正确的!' + 12+'abc'").stringValue()).equals("字符串是正确的!12abc"));
        Assert.assertTrue(((String) new Expression("'字符串'+'是正确的!'+12+'abc'").stringValue()).equals("字符串是正确的!12abc"));
        Assert.assertTrue(((String) new Expression("'aa'+'\\' \\\\' ").stringValue()).equals("aa' \\"));
        Assert.assertTrue(!((Boolean) new Expression("'1'+'2'=='3'").booleanValue()));

        Assert.assertTrue(((Long) new Expression("24*3600* 1000").longValue()) == 86400000);
        Assert.assertTrue(((Long) new Expression("24*3600*1000").longValue()) == 86400000);
        Assert.assertTrue(((Boolean) new Expression("'1985-02-01'>='1985-02-01'").booleanValue()));
        Assert.assertTrue(((Boolean) new Expression("'1983-02-01'<='1985-02-01'").booleanValue()));
        Assert.assertTrue(((Boolean) new Expression("(11+11)==(10+12)").booleanValue()));
        Assert.assertTrue(!((Boolean) new Expression("11+11!=10.4+11.6").booleanValue()));
        Assert.assertTrue(((Double) new Expression("10+ -11.1234").doubleValue()) == -1.1234);
        Assert.assertTrue(((Double) new Expression("10+-11.1234").doubleValue()) == -1.1234);

        Assert.assertTrue(((Double) new Expression("4.900000e-324").doubleValue()) == 4.9e-324);
        Assert.assertTrue(((Double) new Expression("1.797693e+308").doubleValue()) == 1.797693E308);
    }
}
