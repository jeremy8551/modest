package cn.org.expect.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.org.expect.ModestRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML工具
 *
 * @author jeremy8551@gmail.com
 * @createtime 2017-02-04
 */
public class XMLUtils {

    /**
     * 返回 xml 文档对象
     *
     * @param xml XML文本
     * @return XML文档对象
     */
    public static Document newDocument(String xml, String charsetName) {
        try {
            return newDocument(new ByteArrayInputStream(StringUtils.toBytes(xml, charsetName)));
        } catch (Exception e) {
            throw new ModestRuntimeException(charsetName + Settings.LINE_SEPARATOR + xml, e);
        }
    }

    /**
     * 返回 xml 配置文件对象
     *
     * @param in xml信息
     * @return XML文档对象
     */
    public static Document newDocument(InputStream in) {
        try {
            DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
            xml.setIgnoringComments(true);
            xml.setIgnoringElementContentWhitespace(true);
            DocumentBuilder documentBuilder = xml.newDocumentBuilder();
            return documentBuilder.parse(in);
        } catch (Throwable e) {
            throw new ModestRuntimeException("xml.stdout.message001", Document.class.getName(), e);
        }
    }

    /**
     * 计算在[节点node]下有多少个子节点名字=[nodeName]
     *
     * @param node     节点
     * @param nodeName 子节点名
     * @return 节点个数
     */
    public static int getNodeNumber(Node node, String nodeName) {
        int count = 0;
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            if (nodeName.equalsIgnoreCase(item.getNodeName())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 返回 XML 的根节点，XML 中只能有一个根节点
     *
     * @param node     XML输入流
     * @param nodeName 根节点名
     * @return 根节点
     */
    public static Node getChildNode(Node node, String nodeName) {
        List<Node> nodeList = XMLUtils.getChildNodes(node, nodeName);
        int size = nodeList.size();
        if (size == 0) {
            throw new ModestRuntimeException("xml.stdout.message008", node.getNodeName(), nodeName);
        } else if (size == 1) {
            return nodeList.get(0);
        } else {
            throw new ModestRuntimeException("xml.stdout.message007", node.getNodeName(), nodeName, size);
        }
    }

    /**
     * 返回在[节点node]下子节点的集合
     *
     * @param node     节点
     * @param nodeName 子节点的名字，忽略大小写
     * @return 集合
     */
    public static List<Node> getChildNodes(Node node, String nodeName) {
        List<Node> result = new ArrayList<Node>();
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            if (item.getNodeName().equalsIgnoreCase(nodeName)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 返回在[节点node]下子节点的集合
     *
     * @param node   节点
     * @param filter 子节点的过滤条件
     * @return 集合
     */
    public static List<Node> getChildNodes(Node node, Filter filter) {
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        List<Node> result = new ArrayList<Node>(length);
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            if (filter.test(i, item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 处理在[节点node]的子节点
     *
     * @param node 节点
     * @param func 处理规则
     */
    public static void runChildNodes(Node node, Function func) {
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node item = nodeList.item(i);
            func.execute(i, item);
        }
    }

    /**
     * 查找节点中的属性值，如果找不到用默认值替代
     *
     * @param node       节点
     * @param name       属性名
     * @param defaultVal 默认值
     * @return 属性值
     */
    public static String getAttribute(Node node, String name, String defaultVal) {
        try {
            String value = getAttribute(node, name);
            return StringUtils.isBlank(value) ? defaultVal : value;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * 查找节点中的属性值
     *
     * @param node 节点
     * @param name 属性名
     * @return 属性值
     */
    public static String getAttribute(Node node, String name) {
        try {
            return StringUtils.trimBlank(node.getAttributes().getNamedItem(name).getNodeValue());
        } catch (Exception e) {
            throw new ModestRuntimeException("xml.stdout.message002", node.getNodeName(), name, e);
        }
    }

    /**
     * 查询节点的所有属性
     *
     * @param node 节点
     * @return 属性值
     */
    public static Map<String, String> getAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        int length = attributes.getLength();
        HashMap<String, String> map = new HashMap<String, String>(length);
        for (int i = 0; i < length; i++) {
            Node item = attributes.item(i);
            String name = StringUtils.trim(item.getNodeName());
            String value = item.getNodeValue();
            map.put(name, value);
        }
        return map;
    }

    /**
     * 获取属性值并转换成整数
     *
     * @param node 节点
     * @param name 属性名
     * @return 整数
     */
    public static int getAttributeAsInt(Node node, String name) {
        String value = getAttribute(node, name);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new ModestRuntimeException("xml.stdout.message003", node.getNodeName(), name, value);
        }
    }

    /**
     * 获取属性值并转换成整数
     *
     * @param node       节点
     * @param name       属性名
     * @param defaultVal 默认值
     * @return 整数
     */
    public static int getAttributeAsInt(Node node, String name, int defaultVal) {
        String value = getAttribute(node, name, null);
        if (StringUtils.isBlank(value)) {
            return defaultVal;
        }

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new ModestRuntimeException("xml.stdout.message003", node.getNodeName(), name, value);
        }
    }

    /**
     * 获取属性值并转换成 boolean
     *
     * @param node       节点
     * @param name       属性名
     * @param defaultVal 默认值
     * @return 布尔值
     */
    public static boolean getAttributeAsBoolean(Node node, String name, String defaultVal) {
        String value = getAttribute(node, name, null);
        if (StringUtils.isNotBlank(value)) {
            return Boolean.parseBoolean(StringUtils.removeBlank(value));
        } else {
            return Boolean.parseBoolean(StringUtils.removeBlank(defaultVal));
        }
    }

    /**
     * 判断属性值是否为空
     *
     * @param node 节点
     * @param name 属性名
     * @return true-为空
     */
    public static boolean isAttributeBlank(Node node, String name) {
        Node item = node.getAttributes().getNamedItem(name);
        if (item == null) {
            return true;
        } else {
            try {
                return StringUtils.isBlank(item.getNodeValue());
            } catch (Exception e) {
                throw new ModestRuntimeException("xml.stdout.message003", node.getNodeName(), name, e);
            }
        }
    }

    /**
     * 转义xml中特殊字符
     * {@literal 1010 < 1020 == 1010 \&lt; 1020 }
     * {@literal 1010 > 1020 == 1010 \&gt; 1020 }
     * {@literal "string" == &quot;string&quot; }
     *
     * @param str 字符串
     * @return 转义后的字符串
     */
    public static String escape(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(str.length() + 10);
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '&':
                        buf.append("&amp;");
                        break;

                    case '<':
                        buf.append("&lt;");
                        break;

                    case '>':
                        buf.append("&gt;");
                        break;

                    case '\"':
                        buf.append("&quot;");
                        break;

                    case '\'':
                        buf.append("&apos;");
                        break;

                    default:
                        buf.append(c);
                        break;
                }
            }
            return str.length() == buf.length() ? str : buf.toString();
        }
    }

    /**
     * 反转义xml中特殊字符
     * {@literal &nb == &amp;nb }
     * {@literal 1010 &lt; 1020 == 1010 < 1020 }
     * {@literal 1010 &gt; 1020 == 1010 > 1020 }
     * {@literal &quot;string&quot; == "string" }
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String unescape(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder buf = new StringBuilder(str.length());
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '&') {
                    int next = i + 1;
                    if (next < str.length()) {

                        // 转义 &amp;
                        if (str.startsWith("amp;", next)) {
                            buf.append('&');
                            i += "amp;".length();
                            continue;
                        }

                        // 转义 &lt;
                        if (str.startsWith("lt;", next)) {
                            buf.append('<');
                            i += "lt;".length();
                            continue;
                        }

                        // 转义 &gt;
                        if (str.startsWith("gt;", next)) {
                            buf.append('>');
                            i += "gt;".length();
                            continue;
                        }

                        // 转义 &apos;
                        if (str.startsWith("apos;", next)) {
                            buf.append('\'');
                            i += "apos;".length();
                            continue;
                        }

                        // 转义 &quot;
                        if (str.startsWith("quot;", next)) {
                            buf.append('"');
                            i += "quot;".length();
                            continue;
                        }
                    }

                    buf.append(c);
                } else {
                    buf.append(c);
                }
            }
            return str.length() == buf.length() ? str : buf.toString();
        }
    }

    /**
     * 从xml信息中读取头信息, 如: {@literal <?xml version=\"1.0\" encoding=\"UTF-8\"?> }
     *
     * @param xml xml信息
     * @return null表示不存在xml报文头
     */
    public static String getEncoding(String xml) {
        if (xml == null) {
            return null;
        }

        String dest = "<?xml";
        int begin = StringUtils.indexOf(xml, dest, 0, true);
        if (begin == -1) {
            return null;
        }

        int index = StringUtils.indexOf(xml, "?>", begin + 4, true);
        if (index == -1) {
            throw new ModestRuntimeException("xml.stdout.message004", xml, "?>");
        }

        String content = StringUtils.trimBlank(xml.substring(dest.length() + 5, index)); // 截取xml头信息
        List<Property> list = new ArrayList<Property>();
        XMLUtils.splitProperty(content, list);
        for (Property node : list) {
            if ("encoding".equalsIgnoreCase(node.getKey())) {
                return StringUtils.trimBlank((Object) node.getValue());
            }
        }
        return null;
    }

    /**
     * 读取 {@literal <?xml version="1.0" encoding="UTF-8"?> } 中的 encoding 属性值
     *
     * @param array xml的字节数组
     * @return XML字符集编码
     */
    public static String getEncoding(byte[] array) {
        try {
            String charsetName = XMLUtils.getEncoding(new String(array, CharsetName.UTF_8));
            if (StringUtils.isNotBlank(charsetName)) {
                return charsetName;
            }
        } catch (Exception e) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(e.getLocalizedMessage(), e);
            }
        }

        return null;
    }

    /**
     * 分隔XML中的属性信息, 保存属性名与属性值到集合list
     *
     * @param xml XML属性信息, 如: version =\"1.0\" encoding = \"UTF-8\" name=\"名字\" v1 = 1 v2= v3=3 v4= 4"
     */
    public static List<Property> splitProperty(String xml) {
        List<Property> list = new ArrayList<Property>();
        XMLUtils.splitProperty(xml, list);
        return list;
    }

    /**
     * 分隔XML中的属性信息, 保存属性名与属性值到集合list
     *
     * @param xml  XML属性信息, 如: version =\"1.0\" encoding = \"UTF-8\" name=\"名字\" v1 = 1 v2= v3=3 v4= 4"
     * @param list XML属性集合，用于存储解析后的所有字段
     */
    public static void splitProperty(String xml, Collection<Property> list) {
        if (xml == null) {
            return;
        }

        String name = null;  // 属性名
        String str = StringUtils.trimBlank(xml);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // 属性值开始的位置
            if (c == '=') {
                int from = StringUtils.indexOfNotBlank(str, i + 1, -1);
                if (from == -1) {
                    break;
                }

                // 引号位置
                if (StringUtils.inArray(str.charAt(from), '\'', '\"')) {
                    int index = XMLUtils.indexPropertyValueEndPos(str, from);
                    if (index == -1) {
                        throw new ModestRuntimeException("xml.stdout.message005", xml, name);
                    }

                    String value = StringUtils.unquotation(StringUtils.trimBlank(str.substring(i + 1, index + 1)));
                    setLastPropertyValue(str, list, value, false);

                    i = index;
                } else { // 无引号属性值开始位置
                    int index = XMLUtils.getProperty(str, from);
                    if (index == -1) { // 无属性值时进入下一个属性判断
                        i = from - 1;
                    } else {
                        String value = str.substring(i + 1, index + 1); // 截取属性值
                        setLastPropertyValue(str, list, value, false); // 设置xml属性的属性值
                        i = index;
                    }
                }
                continue;
            }

            // 忽略空白字符
            if (Character.isWhitespace(c)) {
                continue;
            }

            // 属性值开始
            if (c == '\'' || c == '\"') {
                int index = XMLUtils.indexPropertyValueEndPos(str, i);
                if (index == -1) {
                    throw new ModestRuntimeException("xml.stdout.message005", xml, name);
                }

                String value = StringUtils.unquotation(StringUtils.trimBlank(str.substring(i, index + 1)));
                setLastPropertyValue(str, list, value, true);

                i = index;
                continue;
            }

            // 属性名开始位置
            int end = XMLUtils.indexPropertyNameEndPos(str, i + 1); // 搜索XML属性名结尾字符的位置
            name = str.substring(i, end + 1);
            list.add(new Property(name, null)); // 增加属性

            i = end;
        }
    }

    /**
     * remove &lt;!DOCTYPE .. &gt; from xml content
     *
     * @param xml XML信息
     * @return 字符串
     */
    public static String removeDoctype(String xml) {
        if (xml == null) {
            return xml;
        }

        String head = "<!DOCTYPE";
        int begin = xml.indexOf(head); // index of String start position
        if (begin == -1) {
            return xml;
        }

        int index = xml.indexOf(">", begin + head.length());
        if (index == -1) {
            throw new ModestRuntimeException("xml.stdout.message004", xml, ">");
        } else {
            String newXml = StringUtils.remove(xml, begin, index + 1);
            return XMLUtils.removeDoctype(newXml);
        }
    }

    /**
     * 搜索属性名结束位置
     *
     * @param str  xml内容
     * @param from 属性名开始的位置
     * @return 不会有-1的返回值
     */
    private static int indexPropertyNameEndPos(String str, int from) {
        for (int i = from; i < str.length(); i++) {
            char c = str.charAt(i);
            // 空白字符或等号或引号的前一个字符是属性名最后一个字符
            if (Character.isWhitespace(c) || c == '=' || c == '\'' || c == '\"') {
                return i - 1;
            }
        }
        return str.length() - 1;
    }

    /**
     * 搜索属性值结尾字符所在位置
     *
     * @param str  xml内容
     * @param from 属性值开始位置, 单引号或双引号的起始位置
     * @return 位置信息
     */
    private static int indexPropertyValueEndPos(String str, int from) {
        char c = str.charAt(from); // 单引号或双引号
        for (int i = from + 1; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将属性值 val 保存到最后一个属性中
     *
     * @param str          xml内容
     * @param list         属性集合
     * @param value        属性值
     * @param ignoreNoName true表示属性值无对应属性名时忽略不会抛出异常, 否则抛出异常
     */
    protected static void setLastPropertyValue(String str, Collection<Property> list, String value, boolean ignoreNoName) {
        if (list.isEmpty()) {
            if (ignoreNoName) {
                return;
            } else {
                throw new ModestRuntimeException("xml.stdout.message006", str, value);
            }
        }

        Property property = CollectionUtils.last(list);
        if (property.getValue() != null) {
            if (ignoreNoName) {
                return;
            } else {
                throw new ModestRuntimeException("xml.stdout.message006", str, value);
            }
        }
        property.setValue(value); // 设置属性
    }

    /**
     * 从指定位置开始搜索xml查找属性值结束位置
     * v1=1 v2 = v3 = 3" v4 = 4' v5=
     *
     * @param str  xml内容
     * @param from 属性值开始位置
     * @return -1表示无属性值 大于0表示属性值结束位置
     */
    private static int getProperty(String str, int from) {
        for (int i = from; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                int nextStr = StringUtils.indexOfNotBlank(str, i, -1); // 搜索属性值结尾位置的字符
                return nextStr != -1 && str.charAt(nextStr) == '=' ? -1 : i - 1; // 如果下一个字符串以等号开始则属性值不存在
            } else if (c == '\"' || c == '\'') {
                return i;
            } else if (c == '=') {
                return -1;
            }
        }
        return from;
    }

    /**
     * 从 XML 指定位置开始，读取一个标签
     *
     * @param xml        XML
     * @param tagName    标签名，大小写敏感
     * @param from       搜索的起始位置信息，从0开始
     * @param ignoreCase true表示标签名忽略大小写
     * @return 返回标签内容，如: &lt;table&gt;...&lt;/table&gt;
     */
    public static String readTag(String xml, String tagName, int from, boolean ignoreCase) {
        for (int i = from; i < xml.length(); i++) {
            char c = xml.charAt(i);
            if (c == '<') {
                int next = i + 1;
                if (StringUtils.startsWith(xml, tagName, next, ignoreCase, true)) {
                    char nextChar = xml.charAt(next + tagName.length());
                    if (nextChar == '>' || nextChar == ' ') {
                        int end = indexOfEndTag(xml, tagName, i + tagName.length(), ignoreCase);
                        if (end != -1) {
                            return xml.substring(i, end);
                        }
                    }
                }
            }
        }
        return null;
    }

    protected static int indexOfEndTag(String xml, String tagName, int from, boolean ignoreCase) {
        int count = 1;
        for (int i = from; i < xml.length(); i++) {
            char c = xml.charAt(i);
            if (c == '<') {
                int next = i + 1;
                if (StringUtils.startsWith(xml, tagName, next, ignoreCase, true)) {
                    char nextChar = xml.charAt(next + tagName.length());
                    if (nextChar == '>' || nextChar == ' ') {
                        count++;
                        continue;
                    }
                }

                String endTag = "</" + tagName + ">";
                if (StringUtils.startsWith(xml, endTag, i, ignoreCase, false)) {
                    if (--count == 0) {
                        return i + endTag.length();
                    } else {
                        i = i + endTag.length() - 1;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * XML中节点的过滤条件
     */
    public interface Filter {

        /**
         * 过滤条件
         *
         * @param index 节点位置信息，从0开始
         * @param node  节点信息
         * @return 返回true表示满足条件，false表示不满足条件
         */
        boolean test(int index, Node node);
    }

    /**
     * XML中节点的处理规则
     */
    public interface Function {

        /**
         * 处理规则
         *
         * @param index 节点位置信息，从0开始
         * @param node  节点信息
         */
        void execute(int index, Node node);
    }
}
