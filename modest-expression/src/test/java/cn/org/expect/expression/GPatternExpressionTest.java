package cn.org.expect.expression;

import org.junit.Assert;
import org.junit.Test;

public class GPatternExpressionTest {

    @Test
    public void test() {
        Assert.assertTrue(new GPatternExpression("*").match("123"));
        Assert.assertTrue(new GPatternExpression("t*").match("t123"));
        Assert.assertTrue(!new GPatternExpression("t?").match("t123"));
        Assert.assertTrue(new GPatternExpression("t?").match("t1"));
        Assert.assertTrue(!new GPatternExpression("t?").match("t"));
        Assert.assertTrue(new GPatternExpression("t[a-b][0-9]e").match("ta2e"));
        Assert.assertTrue(new GPatternExpression("t[^a-b][^01234]e").match("tc5e"));
        Assert.assertTrue(new GPatternExpression("t[!a-b][!01234]e").match("tc5e"));
        Assert.assertTrue(new GPatternExpression("t{a,b,{t1,{t2*,t3*}}} ").match("ta "));
        Assert.assertTrue(new GPatternExpression("t{a,b,{t1,t{2..5}}} ").match("ta "));
        Assert.assertTrue(new GPatternExpression("t{a,b,{t1,t{d..f}}} ").match("ta "));
        Assert.assertTrue(!new GPatternExpression("t{a,b,{t1,t{td..f}}} ").match("tttd "));
        Assert.assertTrue(!new GPatternExpression("t{a,b,{t1,t{td..f}}} ").match("ttf "));
        Assert.assertTrue(new GPatternExpression("").match(""));
    }
}
