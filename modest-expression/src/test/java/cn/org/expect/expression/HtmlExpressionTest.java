package cn.org.expect.expression;

import org.junit.Assert;
import org.junit.Test;

public class HtmlExpressionTest {

    @Test
    public void test() {
        Assert.assertEquals(new HtmlExpression("<html />").getTagName(), "html");
        Assert.assertEquals(new HtmlExpression("<html/>").getTagName(), "html");
    }

    @Test
    public void test1() {
        HtmlExpression e = new HtmlExpression("<html name='value' />");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
    }

    @Test
    public void test2() {
        HtmlExpression e = new HtmlExpression("<html name=\"value\" />");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
    }

    @Test
    public void test3() {
        HtmlExpression e = new HtmlExpression("<html name=\"value\" key='value' />");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
        Assert.assertEquals(e.getAttribute("key"), "value");
    }

    @Test
    public void test4() {
        HtmlExpression e = new HtmlExpression("<html name=\"value\" key='value'></html>");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
        Assert.assertEquals(e.getAttribute("key"), "value");
        Assert.assertEquals(e.getText(), "");
    }

    @Test
    public void test5() {
        HtmlExpression e = new HtmlExpression("<html name=\"value\" key='value'>1</html>");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
        Assert.assertEquals(e.getAttribute("key"), "value");
        Assert.assertEquals(e.getText(), "1");
    }

    @Test
    public void test6() {
        HtmlExpression e = new HtmlExpression("<html name=\"value\" key='value'><table></table></html>");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
        Assert.assertEquals(e.getAttribute("key"), "value");
        Assert.assertEquals(e.getText(), "<table></table>");
    }

    @Test
    public void test7() {
        HtmlExpression e = new HtmlExpression("<th:html name=\"value\" key='value'><table></table></html>");
        Assert.assertEquals(e.getTagName(), "html");
        Assert.assertEquals(e.getNamespace(), "th");
        Assert.assertTrue(e.contains("name"));
        Assert.assertEquals(e.getAttribute("name"), "value");
        Assert.assertEquals(e.getAttribute("key"), "value");
        Assert.assertEquals(e.getText(), "<table></table>");
    }
}
