package cn.org.expect.expression;

import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ParameterExpressionTest {

    @Test
    public void test() {
        String expr = " -d 20170102 -f /home/user/shell/qyzx/m_file/test.del -c true -p /home/user/shell/qyzx/m_file/txt.del -dd -u -s 'this is params ' parameter1 ";
        ArgumentExpression param = new ArgumentExpression(expr);
        Assert.assertEquals(expr.trim(), param.toString());

        Assert.assertTrue(!param.containOption("-a"));
        Assert.assertTrue(param.getOption("-d").equals("20170102"));
        Assert.assertTrue(param.getParameter(1).equals("parameter1"));
        Assert.assertTrue(param.getParameterSize() == 1);
        Assert.assertTrue(!param.existsOption());
        Assert.assertTrue(param.isOptionBlank("-dd"));
        Assert.assertTrue(StringUtils.replaceVariable(" 20170301 > ${d}", param.removeMinus()).equals(" 20170301 > 20170102"));
        param.addOption("-key", "yusdf");
        Assert.assertTrue(param.getOption("-key").equals("yusdf"));
        Assert.assertTrue(param.containOption("-f"));

        String[] names = param.getOptionNames();
        Assert.assertTrue("-d".equals(names[0]));
        Assert.assertTrue("-f".equals(names[1]));
        Assert.assertTrue("-c".equals(names[2]));

        Assert.assertTrue("this is params ".equals(param.getOption("-s")));
        Assert.assertTrue("-d 20170102 -f /home/user/shell/qyzx/m_file/test.del -c true -p /home/user/shell/qyzx/m_file/txt.del -dd -u -s 'this is params ' parameter1 -key yusdf".equals(param.toString()));

        Assert.assertTrue("yusdf".equals(param.removeOption("-key")));
        Assert.assertTrue("-d 20170102 -f /home/user/shell/qyzx/m_file/test.del -c true -p /home/user/shell/qyzx/m_file/txt.del -dd -u -s 'this is params ' parameter1".equals(param.toString()));

        ArgumentExpression clone = new ArgumentExpression();
        clone.addOption("-x", "11");
        clone.addOption(param);
        Assert.assertTrue("-x 11 -d 20170102 -f /home/user/shell/qyzx/m_file/test.del -c true -p /home/user/shell/qyzx/m_file/txt.del -dd -u -s 'this is params ' parameter1".equals(clone.toString()));
    }
}
