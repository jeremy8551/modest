package cn.org.expect.maven.search;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.StringUtils;

/**
 * 搜索文本的处理器
 */
public class ArtifactSearchPattern {

    public ArtifactSearchPattern() {
    }

    /**
     * 过滤特殊字符
     *
     * @param str 文本信息
     * @return 过滤后的文本信息
     */
    public String filter(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (StringUtils.isLetter(c) || StringUtils.isNumber(c) || StringUtils.inArray(c, '.', '!', '@', '#', '*', '-', '_', '+', '=', '?', ':', '|')) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * 判断字符串是否是精确查询（groupId:artifactId）
     *
     * @param str 字符串
     * @return 返回true表示精确查询
     */
    public boolean isExtra(String str) {
        if (str.indexOf(':') != -1) {
            List<String> list = new ArrayList<String>(10);
            StringUtils.split(str, ':', list);
            return list.size() == 2 && StringUtils.isNotBlank(list.get(0)) && StringUtils.isNotBlank(list.get(1));
        }
        return false;
    }

    /**
     * 解析模糊查询的文本，如果文本是一个 pom 依赖信息，则自动将文本信息转为 groupId:artifactId 格式的字符串
     *
     * @param str 文本信息
     * @return 文本信息
     */
    public String parse(String str) {
        if (str == null) {
            return null;
        }

        String groupId = parseTagValue(str, "groupId");
        String artifactId = parseTagValue(str, "artifactId");

        if (groupId != null && artifactId != null) {
            return groupId + ":" + artifactId;
        } else if (groupId != null || artifactId != null) {
            return groupId == null ? artifactId : groupId;
        } else {
            return StringUtils.trimBlank(str);
        }
    }

    private String parseTagValue(String pattern, String tagName) {
        String tag = "<" + tagName + ">";
        int begin = StringUtils.indexOf(pattern, tag, 0, true);
        if (begin != -1) {
            int end = StringUtils.indexOf(pattern, "</" + tagName + ">", begin, true);
            if (end != -1) {
                return StringUtils.trimBlank(pattern.substring(begin + tag.length(), end));
            }
        }
        return null;
    }

    /**
     * 判断字符串是否是 pom 中的 &lt;dependency&gt;
     *
     * @param str 字符串
     * @return 返回true表示是一个Maven工件坐标
     */
    public boolean isDependency(String str) {
        return StringUtils.indexOf(str, "<artifactId>", 0, true) != -1 || StringUtils.indexOf(str, "<groupId>", 0, true) != -1;
    }
}
