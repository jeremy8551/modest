package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 修改 Markdown 文档的名字，同步修改文档中本地图片的路径
 */
public class RenameMarkdown {
    private final static Log log = LogFactory.getLog(RenameMarkdown.class);

    public void rename(String markdownFilepath, String newFilename) throws IOException {
        File markdownFile = new File(markdownFilepath);
        FileUtils.assertFile(markdownFile);
        File newMarkdownFile = new File(markdownFile.getParentFile(), newFilename);

        File imageDir = new File(FileUtils.changeFilenameExt(markdownFilepath, "assets"));
        String oldImgName = imageDir.getName();
        String oldDecodeName = encoding(oldImgName); // 对中文进行转码
        log.info("markdown.stdout.message006", imageDir.getName(), oldDecodeName);
        Ensure.isTrue(markdownFile.renameTo(newMarkdownFile));

        if (!imageDir.exists()) {
            log.info(imageDir.getAbsolutePath());
            return;
        }

        FileUtils.assertDirectory(imageDir);
        String imgExt = FileUtils.getFilenameExt(imageDir.getAbsolutePath());
        String newImgDirName = imgExt.length() == 0 ? newMarkdownFile.getName() : FileUtils.getFilenameNoExt(newMarkdownFile.getName()) + "." + imgExt;
        File newImgDir = new File(imageDir.getParentFile(), newImgDirName);
        Ensure.isTrue(imageDir.renameTo(newImgDir));

        String searchStr = oldDecodeName + "/"; // 要替换的字符串
        String dest = encoding(newImgDirName) + "/"; // 替换后的字符串
        log.info("markdown.stdout.message006", newImgDirName, dest);
        String markdown = FileUtils.readline(newMarkdownFile, "", 0);

        // 按中文编译过后的名查找
        int start = 0;
        while ((start = markdown.indexOf(searchStr, start)) != -1) {
            int prefix = start - 1;
            if (prefix != -1) {
                char c = markdown.charAt(prefix);
                if (!StringUtils.inArray(c, '(', '[', '.', '/', '\"')) {
                    start += searchStr.length();
                    continue;
                }
            }

            // 图片标签
            String imageTag = this.find(markdown, start, start + searchStr.length());

            // 只替换本地图片，不替换Http链接图片
            if (StringUtils.indexOf(imageTag, "http:", 0, true) == -1 && StringUtils.indexOf(imageTag, "https:", 0, true) == -1) {
                markdown = StringUtils.replace(markdown, start, searchStr.length(), dest);
                String replace = this.find(markdown, start, start + 1); // 替换后的图片标签

                if (log.isInfoEnabled()) {
                    log.info("{} -> {}", imageTag, replace);
                }
            }

            start = start + searchStr.length();
        }

        // 按图片的目录名查找
        if (!oldImgName.endsWith(oldDecodeName)) {
            searchStr = imageDir.getName() + "/"; // 要替换的字符串
            start = 0;
            while ((start = markdown.indexOf(searchStr, start)) != -1) {
                int prefix = start - 1;
                if (prefix != -1) {
                    char c = markdown.charAt(prefix);
                    if (!StringUtils.inArray(c, '(', '[', '.', '/', '\"')) {
                        start += searchStr.length();
                        continue;
                    }
                }

                // 图片标签
                String imageTag = this.find(markdown, start, start + searchStr.length());

                // 只替换本地图片，不替换Http链接图片
                if (StringUtils.indexOf(imageTag, "http:", 0, true) == -1 && StringUtils.indexOf(imageTag, "https:", 0, true) == -1) {
                    markdown = StringUtils.replace(markdown, start, searchStr.length(), dest);
                    String replace = this.find(markdown, start, start + 1); // 替换后的图片标签

                    if (log.isInfoEnabled()) {
                        log.info("{} -> {}", imageTag, replace);
                    }
                }

                start = start + searchStr.length();
            }
        }

        if (log.isInfoEnabled()) {
            log.info("markdown.stdout.message007", markdownFile.getAbsolutePath(), newMarkdownFile.getName());
            log.info("markdown.stdout.message007", imageDir.getAbsolutePath(), newImgDir.getName());
        }

        FileUtils.write(newMarkdownFile, Settings.getFileEncoding(), false, markdown);
    }

    public String find(String str, int begin, int end) {
        int start = begin, over = end;
        for (int i = begin; i >= 0; i--) {
            char c = str.charAt(i);
            if (c == '!' || c == '<' || c == '\\') {
                start = i;
                break;
            }
        }

        for (int i = end; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ')' || c == '>' || c == '\\') {
                over = i;
                break;
            }
        }

        return str.substring(start, over + 1);
    }

    public static String encoding(String str) throws IOException {
        String encode = URLEncoder.encode(str, CharsetName.UTF_8);
        return StringUtils.replaceAll(encode, "+", "%20");
    }
}
