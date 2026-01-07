package cn.org.expect.expression;

import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * 测试 shell 命令表达式是否正确
 */
public class CommandExpressionTest {
    private final static Log log = LogFactory.getLog(CommandExpressionTest.class);

    @Test
    public void test() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();

        CommandExpression expression = new CommandExpression(analysis, "echo -i: -e -b -c {0-1|4}", "echo -i a -e b c -b d -c e");
        Assert.assertEquals("echo", expression.getName());
        Assert.assertTrue(expression.containsOption("-i"));
        Assert.assertTrue(expression.containsOption("-e"));
        Assert.assertEquals("a", expression.getOptionValue("-i"));
        Assert.assertNull(expression.getOptionValue("-e"));
        List<String> list = expression.getParameters();
        Assert.assertTrue(list.size() == 4 && "b".equals(list.get(0)) && "c".equals(list.get(1)));

        try {
            new CommandExpression(analysis, "echo -i: -e -b -c", "echo -i a -e b c -b d -c e -d");
            fail();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            Assert.assertTrue(true);
        }

        expression = new CommandExpression(analysis, "-tvf -nme: --prefix: --o  --you:", "tar -vf -t parameter -n filename -m t1 --prefix ab --o  --you hello p2 ");
        Assert.assertEquals("tar", expression.getName());
        Assert.assertTrue(expression.containsOption("-v"));
        Assert.assertTrue(expression.containsOption("-f"));
        Assert.assertTrue(expression.containsOption("-t"));
        Assert.assertTrue(expression.containsOption("-t", "-v"));
        Assert.assertTrue(expression.containsOption("-t", "-v", "-f"));
        Assert.assertTrue(expression.containsOption("-o"));
        Assert.assertEquals("filename", expression.getOptionValue("-n"));
        Assert.assertEquals("t1", expression.getOptionValue("-m"));
        Assert.assertEquals("ab", expression.getOptionValue("-prefix"));
        Assert.assertEquals("hello", expression.getOptionValue("-you"));

        Assert.assertEquals(2, expression.getParameters().size());
        Assert.assertEquals("parameter", expression.getParameters().get(0));
        Assert.assertEquals("p2", expression.getParameters().get(1));
    }

    @Test
    public void test1() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        LoginExpression p = new LoginExpression(analysis, "ssh user@127.0.0.1:22?password=passwd&alive=true&d=");
        Assert.assertEquals("ssh", p.getName());
        Assert.assertEquals("user", p.getLoginUsername());
        Assert.assertEquals("passwd", p.getLoginPassword());
        Assert.assertEquals("127.0.0.1", p.getLoginHost());
        Assert.assertEquals("22", p.getLoginPort());
        Assert.assertEquals("true", p.getAttribute("alive"));
        Assert.assertEquals("", p.getAttribute("d"));
        Assert.assertEquals("", p.getAttribute("d"));
    }

    @Test
    public void test2() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!isfile -i: -e -b -c {4}", "!isfile -i a -e b c -b d -c e");
        Assert.assertEquals("isfile", p.getName());
        Assert.assertTrue(p.isReverse());
        Assert.assertTrue(p.containsOption("-i"));
        Assert.assertTrue(p.containsOption("-e"));
        Assert.assertEquals("a", p.getOptionValue("-i"));
        Assert.assertNull(p.getOptionValue("-e"));
        List<String> list = p.getParameters();
        Assert.assertTrue(list.size() == 4 && "b".equals(list.get(0)) && "c".equals(list.get(1)));
    }

    @Test
    public void test3() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!isfile -i: -e -b -c {4}", "isfile -i a -e b c -b d -c e");
        Assert.assertEquals("isfile", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-i"));
        Assert.assertTrue(p.containsOption("-e"));
        Assert.assertEquals("a", p.getOptionValue("-i"));
        Assert.assertNull(p.getOptionValue("-e"));
        List<String> list = p.getParameters();
        Assert.assertTrue(list.size() == 4 && "b".equals(list.get(0)) && "c".equals(list.get(1)));
    }

    @Test
    public void test4() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!test [-t:|-s|-d:date] {0}", "test -t tp");
        Assert.assertEquals("test", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertEquals("tp", p.getOptionValue("-t"));

        p = new CommandExpression(analysis, "!test [-t:|-s|-d:date] {0}", "test -d 2020-01-01");
        Assert.assertEquals("test", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertEquals("2020-01-01", p.getOptionValue("-d"));

        p = new CommandExpression(analysis, "!test [-t:|-s|-d:date] {0}", "test -s");
        Assert.assertEquals("test", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-s"));

        p = new CommandExpression(analysis, "!test (-t:|-s|-d:date) {0}", "test -s");
        Assert.assertEquals("test", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-s"));

        try {
            new CommandExpression(analysis, "!test (-t:|-s|-d:date) {0}", "test");
            fail();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            Assert.assertTrue(true);
        }

        p = new CommandExpression(analysis, "!test [-t:] [-s] [-d:date] {0}", "test -s -t tv -d 20200101");
        Assert.assertEquals("test", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-s"));
        Assert.assertTrue(p.containsOption("-t"));
        Assert.assertTrue(p.containsOption("-d"));
        Assert.assertEquals("20200101", p.getOptionValue("-d"));
        Assert.assertEquals("tv", p.getOptionValue("-t"));
        Assert.assertNull(p.getOptionValue("-s"));

        try {
            new CommandExpression(analysis, "!test [-t:|-s|-d:date] {0}", "test -t tp -s");
            fail();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test5() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!isfile --prefix: -if ", "isfile --prefix=test value -i");
        Assert.assertEquals("isfile", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-i"));
        Assert.assertEquals("test", p.getOptionValue("-prefix"));
        Assert.assertEquals(1, p.getParameters().size()); // 参数个数只能是1
        Assert.assertEquals("value", p.getParameter());
    }

    @Test
    public void test6() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        String pattern = "!isfile --prefix: -if ";
        String command = "isfile --prefix=  -i";
        CommandExpression p = new CommandExpression(analysis, pattern, command);
        Assert.assertEquals("isfile", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-i"));
        Assert.assertNull(p.getOptionValue("-prefix"));
        Assert.assertEquals(0, p.getParameters().size()); // 参数个数只能是1
        Assert.assertEquals(2, p.getOptionNames().length);
        Assert.assertEquals(0, p.getParameterSize());
        Assert.assertEquals(pattern, p.getPattern());
        Assert.assertEquals(command, p.toString());
    }

    @Test
    public void test7() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!isfile --prefix: -if {0-1} ", "isfile --prefix=test -i   this is a test world!  ");
        Assert.assertEquals("isfile", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-i"));
        Assert.assertFalse(p.containsOption("-d"));
        Assert.assertEquals("test", p.getOptionValue("-prefix"));
        Assert.assertEquals(1, p.getParameters().size()); // 参数个数只能是1
        Assert.assertEquals("this is a test world!", p.getParameter());
    }

    @Test
    public void test8() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "!isfile --prefix: -if ", "isfile --prefix");
        Assert.assertEquals("isfile", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertFalse(p.containsOption("-i"));
        Assert.assertNull(p.getOptionValue("-prefix"));
        Assert.assertEquals(0, p.getParameters().size()); // 参数个数只能是1
    }

    // 测试参数中包含选项
    @Test
    public void test9() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        try {
            new CommandExpression(analysis, "!isfile --prefix: -if {0-1} ", "isfile --prefix=test -i this is a -f test world!  ");
            Assert.fail();
        } catch (ExpressionException e) {
            log.info(e.getLocalizedMessage());
            Assert.assertTrue(e.getLocalizedMessage().contains("-f"));
        }
    }

    // 测试参数中包含选项
    @Test
    public void test10() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "set [-E|-e] {0-1} ", "set name=`wc -l xxx | grep test`");
        Assert.assertEquals("set", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertFalse(p.containsOption("-e"));
        Assert.assertEquals(1, p.getParameters().size()); // 参数个数只能是1
    }

    @Test
    public void test11() {
        TestAnalysisImpl analysis = new TestAnalysisImpl();
        CommandExpression p = new CommandExpression(analysis, "date -d:", "date -d '2020-12-13 03:14:56' 'H'");
        Assert.assertEquals("date", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertTrue(p.containsOption("-d"));
        Assert.assertEquals("'2020-12-13 03:14:56'", p.getOptionValue("-d"));
        Assert.assertEquals(1, p.getParameters().size()); // 参数个数只能是1
        Assert.assertEquals("'H'", p.getParameter());
    }

    @Test
    public void test12() {
        CommandExpression p = new CommandExpression("tar [-c|-x] -zv -f:", "tar -xvf $TMPDIR/test.txt");
        Assert.assertEquals("tar", p.getName());
        Assert.assertFalse(p.isReverse());
        Assert.assertEquals("$TMPDIR/test.txt", p.getOptionValue("-f"));
        Assert.assertTrue(p.containsOption("-x"));
        Assert.assertTrue(p.containsOption("-v"));
        Assert.assertTrue(p.containsOption("-f"));
        Assert.assertFalse(p.containsOption("-c"));
        Assert.assertEquals(0, p.getParameters().size()); // 参数个数只能是1
        Assert.assertEquals("$TMPDIR/test.txt", p.getOptionValue("-f"));
    }

    @Test
    public void test13() {
        try {
            new CommandExpression("tar [-c|-x] -z -v: -f:", "tar -xvf $TMPDIR/test.txt");
        } catch (ExpressionException e) {
            log.info(e.getLocalizedMessage());
        }
    }
}
