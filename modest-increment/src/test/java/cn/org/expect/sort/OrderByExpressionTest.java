package cn.org.expect.sort;

import cn.org.expect.expression.Analysis;
import cn.org.expect.expression.DefaultAnalysis;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.StrAsIntComparator;
import cn.org.expect.util.StrAsNumberComparator;
import cn.org.expect.util.StringComparator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class OrderByExpressionTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() {
        Analysis a = new DefaultAnalysis();
        OrderByExpression o = new OrderByExpression(context, a, "1 asc");
        Assert.assertEquals(o.getPosition(), 1);
        Assert.assertTrue(o.isAsc());
        Assert.assertEquals(o.getComparator().getClass(), StringComparator.class);
    }

    @Test
    public void test1() {
        Analysis a = new DefaultAnalysis();
        OrderByExpression expression = new OrderByExpression(context, a, "1");
        Assert.assertEquals(expression.getPosition(), 1);
        Assert.assertTrue(expression.isAsc());
        Assert.assertEquals(expression.getComparator().getClass(), StringComparator.class);
    }

    @Test
    public void test2() {
        Analysis a = new DefaultAnalysis();
        OrderByExpression o = new OrderByExpression(context, a, "int(1) asc");
        Assert.assertEquals(o.getPosition(), 1);
        Assert.assertTrue(o.isAsc());
        Assert.assertEquals(o.getComparator().getClass(), StrAsIntComparator.class);
    }

    @Test
    public void test3() {
        Analysis a = new DefaultAnalysis();
        OrderByExpression o = new OrderByExpression(context, a, "int(1) desc");
        Assert.assertEquals(o.getPosition(), 1);
        Assert.assertFalse(o.isAsc());
        Assert.assertEquals(o.getComparator().getClass(), StrAsIntComparator.class);
    }

    @Test
    public void test4() {
        Analysis a = new DefaultAnalysis();
        OrderByExpression o = new OrderByExpression(context, a, "number(11) desc");
        Assert.assertEquals(o.getPosition(), 11);
        Assert.assertFalse(o.isAsc());
        Assert.assertEquals(o.getComparator().getClass(), StrAsNumberComparator.class);
    }
}
