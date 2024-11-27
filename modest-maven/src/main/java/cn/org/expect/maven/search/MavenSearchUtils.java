package cn.org.expect.maven.search;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class MavenSearchUtils {

    /**
     * 解析模糊查询的文本，如果文本是一个 pom 依赖信息，则自动将文本信息转为 groupId:artifactId 格式的字符串
     *
     * @param pattern 文本信息
     * @return 文本信息
     */
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

    /**
     * 获取指定 URL 目录下的文件列表
     */
    public static List<String> fetchFileList(String httpUrl) throws IOException {

        // 创建一个 URL 对象并打开连接
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 读取服务器返回的 HTML 页面内容
        List<String> fileList = new ArrayList<>();
        try (InputStream inputStream = connection.getInputStream()) {
            String html = new String(inputStream.readAllBytes());
            Pattern pattern = Pattern.compile("href=\"([^\"]+)\""); // 使用正则表达式查找 HTML 中的文件链接
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                String fileName = matcher.group(1);
                String ext = FileUtils.getFilenameExt(fileName);
                if (ext.length() > 1) { // 过滤需要的文件类型
                    fileList.add(fileName);
                }
            }
        }
        return fileList;
    }
}
