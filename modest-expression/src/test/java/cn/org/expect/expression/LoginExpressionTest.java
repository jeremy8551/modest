package cn.org.expect.expression;

import org.junit.Assert;
import org.junit.Test;

public class LoginExpressionTest {

    @Test
    public void test() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        LoginExpression p = new LoginExpression(analysis, "ssh us@er@127.0.0.1:22?password=pass@wd&alive=true@&d=&c=@");
        Assert.assertEquals("ssh", p.getName());
        Assert.assertEquals("us@er", p.getLoginUsername());
        Assert.assertEquals("pass@wd", p.getLoginPassword());
        Assert.assertEquals("127.0.0.1", p.getLoginHost());
        Assert.assertEquals("22", p.getLoginPort());
        Assert.assertEquals("true@", p.getAttribute("alive"));
        Assert.assertEquals("", p.getAttribute("d"));
        Assert.assertEquals("@", p.getAttribute("c"));
    }
}
