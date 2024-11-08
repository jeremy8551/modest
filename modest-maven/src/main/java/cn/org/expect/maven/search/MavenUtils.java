package cn.org.expect.maven.search;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.StringUtils;

public class MavenUtils {

    public static String parse(String pattern) {
        if (pattern == null) {
            return null;
        }

        String groupId = parseTagValue(pattern, "groupId");
        String artifactId = parseTagValue(pattern, "artifactId");

        if (groupId != null && artifactId != null) {
            return groupId + ":" + artifactId;
        } else if (groupId != null || artifactId != null) {
            return groupId == null ? artifactId : groupId;
        } else {
            return StringUtils.trimBlank(pattern);
        }
    }

    public static String parseTagValue(String pattern, String tagName) {
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
     * 判断字符串是否是精确查询（groupId:artifactId）
     *
     * @param str 字符串
     * @return 返回true表示精确查询
     */
    public static boolean isExtraSearch(String str) {
        if (str.indexOf(':') != -1) {
            List<String> list = new ArrayList<String>(10);
            StringUtils.split(str, ':', list);
            return list.size() == 2 && StringUtils.isNotBlank(list.get(0)) && StringUtils.isNotBlank(list.get(1));
        }
        return false;
    }

    /**
     * 判断字符串是否是xml格式的
     *
     * @param str 字符串
     * @return 返回true表示xml格式
     */
    public static boolean isXML(String str) {
        return StringUtils.indexOf(str, "<artifactId>", 0, true) != -1 || StringUtils.indexOf(str, "<groupId>", 0, true) != -1;
    }
}
