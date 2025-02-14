package cn.org.expect.script.method;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@EasyVariableExtension
public class XmlFunction {

    /**
     * 将 xml 解析为 Document 对象
     *
     * @param xml 字符串
     * @return Document 对象
     */
    public static Document newDocument(String xml) {
        return XMLUtils.newDocument(xml, CharsetName.UTF_8); // 将字符串转为 Node
    }

    /**
     * 读取 xml 中指定标签
     *
     * @param xml     字符串
     * @param tagName 标签名
     * @param from    起始位置，从 0 开始
     * @return 标签内容
     */
    public static String readTag(String xml, String tagName, int from) {
        return XMLUtils.readTag(xml, tagName, from, false);
    }

    /**
     * @Ignore 因为脚本引擎中 int 使用 long 存储，所以定义重载方法
     */
    public static String readTag(String variable, String tagName, long from) {
        return readTag(variable, tagName, (int) from);
    }

    /**
     * 过滤节点集合
     *
     * @param nodeList    节点集合
     * @param expressions 过滤规则数组，规则：属性=属性值
     * @return 节点集合
     */
    public static List<Node> filter(List<Node> nodeList, String... expressions) {
        List<String[]> filters = new ArrayList<String[]>();
        for (int i = 0; i < expressions.length; i++) {
            String expression = expressions[i];
            String[] property = StringUtils.splitProperty(expression);
            if (property == null) {
                throw new IllegalArgumentException(expression);
            } else {
                filters.add(property);
            }
        }

        List<Node> list = new ArrayList<Node>();
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);

            boolean match = true;
            for (String[] filter : filters) {
                String name = filter[0];
                String value = filter[1];

                String attribute = XMLUtils.getAttribute(node, name, "");
                if (!value.equals(attribute)) {
                    match = false;
                }
            }

            if (match) {
                list.add(node);
            }
        }

        return list;
    }

    /**
     * 返回标签中的属性值
     *
     * @param node 标签
     * @param name 属性名
     * @return 属性值
     */
    public static String get(Node node, String name) {
        return XMLUtils.getAttribute(node, name, "");
    }

    /**
     * 返回标签的子节点
     *
     * @param node  标签信息
     * @param array 子标签名，忽略大小写
     * @return 标签集合
     */
    public static List<Node> getChildNodes(Node node, String... array) {
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        List<Node> list = new ArrayList<Node>();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            if (array.length == 0 || StringUtils.inArrayIgnoreCase(item.getNodeName(), array)) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 返回标签名
     *
     * @param node 标签信息
     * @return 标签名
     */
    public static String getName(Node node) {
        return node.getNodeName();
    }

    /**
     * 返回标签内容
     *
     * @param node 标签信息
     * @return 标签中的字符串
     */
    public static String getText(Node node) {
        return node.getTextContent();
    }
}
