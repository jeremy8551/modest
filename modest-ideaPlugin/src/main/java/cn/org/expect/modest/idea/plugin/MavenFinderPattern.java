package cn.org.expect.modest.idea.plugin;

import cn.org.expect.util.StringUtils;

public class MavenFinderPattern {

    public static String parse(String pattern) {
        if (pattern == null) {
            return null;
        }

        String tag = "<artifactId>";
        int begin = StringUtils.indexOf(pattern, tag, 0, true);
        if (begin != -1) {
            pattern = pattern.substring(begin + tag.length());
        }

        int end = StringUtils.indexOf(pattern, "</artifactId>", 0, true);
        if (end != -1) {
            pattern = pattern.substring(0, end);
        }

        return StringUtils.trimBlank(pattern);
    }
}
