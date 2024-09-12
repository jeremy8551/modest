package cn.org.expect.sort;

import cn.org.expect.expression.Analysis;
import cn.org.expect.expression.AnalysisImpl;
import cn.org.expect.ioc.DefaultEasyetlContext;
import cn.org.expect.util.StrAsIntComparator;
import cn.org.expect.util.StrAsNumberComparator;
import cn.org.expect.util.StringComparator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrderByExpressionTest {

    @Test
    public void test() {
        DefaultEasyetlContext context = new DefaultEasyetlContext();
        Analysis a = new AnalysisImpl();
        OrderByExpression o = new OrderByExpression(context, a, "1 asc");
        assertEquals(o.getPosition(), 1);
        assertTrue(o.isAsc());
        assertEquals(o.getComparator().getClass(), StringComparator.class);
    }

    @Test
    public void test1() {
        DefaultEasyetlContext context = new DefaultEasyetlContext();
        Analysis a = new AnalysisImpl();
        OrderByExpression expression = new OrderByExpression(context, a, "1");
        assertEquals(expression.getPosition(), 1);
        assertTrue(expression.isAsc());
        assertEquals(expression.getComparator().getClass(), StringComparator.class);
    }

    @Test
    public void test2() {
        DefaultEasyetlContext context = new DefaultEasyetlContext();
        Analysis a = new AnalysisImpl();
        OrderByExpression o = new OrderByExpression(context, a, "int(1) asc");
        assertEquals(o.getPosition(), 1);
        assertTrue(o.isAsc());
        assertEquals(o.getComparator().getClass(), StrAsIntComparator.class);
    }

    @Test
    public void test3() {
        DefaultEasyetlContext context = new DefaultEasyetlContext();
        Analysis a = new AnalysisImpl();
        OrderByExpression o = new OrderByExpression(context, a, "int(1) desc");
        assertEquals(o.getPosition(), 1);
        assertFalse(o.isAsc());
        assertEquals(o.getComparator().getClass(), StrAsIntComparator.class);
    }

    @Test
    public void test4() {
        DefaultEasyetlContext context = new DefaultEasyetlContext();
        Analysis a = new AnalysisImpl();
        OrderByExpression o = new OrderByExpression(context, a, "number(11) desc");
        assertEquals(o.getPosition(), 11);
        assertFalse(o.isAsc());
        assertEquals(o.getComparator().getClass(), StrAsNumberComparator.class);
    }

}
