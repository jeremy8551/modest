package cn.org.expect.expression;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/2
 */
public class DataUnitExpressionTest {

    @Test
    public void test1() {
        Assert.assertEquals("444B", DataUnitExpression.toString(new BigDecimal("444"), false));
        Assert.assertEquals("1.06KB", DataUnitExpression.toString(new BigDecimal("1056"), false));
        Assert.assertEquals("663.04MB", DataUnitExpression.toString(new BigDecimal("663040000"), false));
        Assert.assertEquals("678.95GB", DataUnitExpression.toString(new BigDecimal("678952960000"), false));
        Assert.assertEquals("695.25TB", DataUnitExpression.toString(new BigDecimal("695247831040000"), false));
    }

    @Test
    public void test2() {
        Assert.assertEquals("444B", DataUnitExpression.toString(new BigDecimal("444"), true));
        Assert.assertEquals("1.03KiB", DataUnitExpression.toString(new BigDecimal("1056"), true));
        Assert.assertEquals("632.32MiB", DataUnitExpression.toString(new BigDecimal("663040000"), true));
        Assert.assertEquals("632.32GiB", DataUnitExpression.toString(new BigDecimal("678952960000"), true));
        Assert.assertEquals("632.32TiB", DataUnitExpression.toString(new BigDecimal("695247831040000"), true));
    }

    @Test
    public void testformatHumanExpress() {
        Assert.assertEquals(0, DataUnitExpression.parse("1k").compareTo(new BigDecimal("1024")));
        Assert.assertEquals(0, DataUnitExpression.parse("1kb").compareTo(new BigDecimal("1024")));
        Assert.assertEquals(0, DataUnitExpression.parse("1m").compareTo(new BigDecimal("1048576")));
        Assert.assertEquals(0, DataUnitExpression.parse("1mb").compareTo(new BigDecimal("1048576")));
        Assert.assertEquals(0, DataUnitExpression.parse("1g").compareTo(new BigDecimal("1073741824")));
        Assert.assertEquals(0, DataUnitExpression.parse("1gb").compareTo(new BigDecimal("1073741824")));
        Assert.assertEquals(0, DataUnitExpression.parse("1tb").compareTo(new BigDecimal("1099511627776")));
        Assert.assertEquals(0, DataUnitExpression.parse("1pb").compareTo(new BigDecimal("1125899906842624")));
        Assert.assertEquals(0, DataUnitExpression.parse("1eb").compareTo(new BigDecimal("1152921504606846976")));
    }
}
