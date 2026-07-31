package cn.org.expect.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.org.expect.Modest;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLUtilsTest {

    @Test
    public void test() throws IOException {
        Assert.assertEquals(CharsetName.UTF_8, XMLUtils.getEncoding("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        String xml = new String(IO.read(Modest.class.getResourceAsStream("china.xml")));
        String encoding = XMLUtils.getEncoding(xml);
        Assert.assertEquals(CharsetName.UTF_8.toUpperCase(), StringUtils.toCase(encoding, false, null));
    }

    @Test
    public void testGetEncoding() throws IOException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

        Assert.assertEquals("UTF-8", XMLUtils.getEncoding(xml.getBytes("UTF-8")));
        Assert.assertEquals("UTF-8", XMLUtils.getEncoding("<?xml version=\"1.0\" encoding=\"UTF-8\"?><table></table>".getBytes("UTF-8")));
        Assert.assertEquals("UTF-8", XMLUtils.getEncoding("<?xml version=\"1.0\" encoding=\"UTF-8\"?><table></table>".getBytes("utf-8")));
        Assert.assertEquals("UTF-8", XMLUtils.getEncoding("<?xml version=\"1.0\" encoding=\"UTF-8\"?><table></table>".getBytes("ISO-8859-1")));
        Assert.assertNull(XMLUtils.getEncoding("<table></table>".getBytes("ISO-8859-1")));
        Assert.assertNull(XMLUtils.getEncoding("<?xml version=\"1.0\" encoding=\"\"?><table></table>".getBytes("ISO-8859-1")));
        Assert.assertNull(XMLUtils.getEncoding("<?xml version=\"1.0\" ?><table></table>".getBytes("ISO-8859-1")));
    }

    @Test
    public void testescape() {
        Assert.assertNull(XMLUtils.escape(null));
        Assert.assertEquals("", XMLUtils.escape(""));
        Assert.assertEquals("&lt;&amp;&apos;&quot;&gt;", XMLUtils.escape("<&'\">"));
        Assert.assertEquals("&lt;&amp;&apos;&quot;&gt;&lt;&amp;&apos;&quot;&gt;", XMLUtils.escape("<&'\"><&'\">"));
        Assert.assertEquals("<&'\">", XMLUtils.unescape("&lt;&amp;&apos;&quot;&gt;"));
        Assert.assertEquals("<&'\"><&'\"><&'\">", XMLUtils.unescape("&lt;&amp;&apos;&quot;&gt;&lt;&amp;&apos;&quot;&gt;&lt;&amp;&apos;&quot;&gt;"));

        Assert.assertEquals("a<b&c'd\"e>b", XMLUtils.unescape("a&lt;b&amp;c&apos;d&quot;e&gt;b"));
    }

    @Test
    public void test7() {
        Assert.assertEquals("<table><tr><td></td></tr></table>", XMLUtils.readTag("<html><head></head><body><table><tr><td></td></tr></table></body></html>", "table", 0, true));
        Assert.assertEquals("<Table id='test'><tr><td></td></tr></Table>", XMLUtils.readTag("<html><head></head><body><Table id='test'><tr><td></td></tr></Table></body></html>", "table", 0, true));
        Assert.assertEquals("<table><Table id='test'><tr><td></td></tr></Table></Table>", XMLUtils.readTag("<html><head></head><body><table><Table id='test'><tr><td></td></tr></Table></Table></body></html>", "table", 0, true));
        Assert.assertEquals("<table><Table id='test'><tr><td></td></tr></Table>1</Table>", XMLUtils.readTag("<html><head></head><body><table><Table id='test'><tr><td></td></tr></Table>1</Table></body></html>", "table", 0, true));
    }

    @Test
    public void test8() {
        Document root = XMLUtils.newDocument("<html><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></html>", CharsetName.UTF_8);
        Node node = XMLUtils.getChildNodes(root, "html").get(0);
        Assert.assertEquals(5, XMLUtils.getNodeNumber(node, "tr"));
    }

    @Test
    public void test9() {
        Document root = XMLUtils.newDocument("<html><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></html>", CharsetName.UTF_8);
        Node node = XMLUtils.getChildNodes(root, "html").get(0);

        AtomicInteger count = new AtomicInteger(0);
        XMLUtils.runChildNodes(node, new RunChildNodeFunc(count));
        Assert.assertEquals(5, count.get());
    }

    @Test
    public void test10() {
        Document root = XMLUtils.newDocument("<html><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></html>", CharsetName.UTF_8);
        Node node = XMLUtils.getChildNodes(root, "html").get(0);

        List<Node> childNodes = XMLUtils.getChildNodes(node, new XMLUtils.Filter() {
            public boolean test(int index, Node node) {
                return node.getNodeName().equalsIgnoreCase("tr");
            }
        });

        Assert.assertEquals(5, childNodes.size());
    }

    @Test
    public void test11() {
        Document root = XMLUtils.newDocument("<html><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></html>", CharsetName.UTF_8);
        XMLUtils.getChildNode(root, "html");
    }

    @Test
    public void testSplitXmlPropertys() {
        List<Property> list = XMLUtils.splitProperty(" value='' v1=\"1\" v2=  v3= 3 v4 = 4 v5 =5 ");
        Assert.assertTrue(list.get(0).getValue().equals("") && list.get(0).getKey().equals("value"));
        Assert.assertTrue(list.get(1).getValue().equals("1") && list.get(1).getKey().equals("v1"));
        Assert.assertTrue(list.get(2).getValue() == null && list.get(2).getKey().equals("v2"));
        Assert.assertTrue(list.get(3).getValue().equals(" 3") && list.get(3).getKey().equals("v3"));
        Assert.assertTrue(list.get(4).getValue().equals(" 4") && list.get(4).getKey().equals("v4"));
        Assert.assertTrue(list.get(5).getValue().equals("5") && list.get(5).getKey().equals("v5"));
        Assert.assertEquals("", XMLUtils.splitProperty(" value='' ").get(0).getValue());
        Assert.assertEquals("v1", XMLUtils.splitProperty(" value='' v1 = \"test\" ").get(1).getKey());
        Assert.assertEquals("test", XMLUtils.splitProperty(" value='' v1 = \"test\" ").get(1).getValue());

        String s1 = " value='' v1 = \"test\" v2 v3 ";
        Assert.assertEquals("test", XMLUtils.splitProperty(s1).get(1).getValue());
        Assert.assertEquals("v2", XMLUtils.splitProperty(s1).get(2).getKey());
        Assert.assertEquals("v3", XMLUtils.splitProperty(s1).get(3).getKey());
    }

    static class RunChildNodeFunc implements XMLUtils.Function {

        AtomicInteger count;

        public RunChildNodeFunc(AtomicInteger count) {
            this.count = count;
        }

        public void execute(int index, Node node) {
            count.incrementAndGet();
        }
    }
}
