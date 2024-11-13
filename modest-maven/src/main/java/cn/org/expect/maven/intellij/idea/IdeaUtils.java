package cn.org.expect.maven.intellij.idea;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.util.FileUtils;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapUtil;

public class IdeaUtils {
    private final static Log log = LogFactory.getLog(IdeaUtils.class);

    public static NotificationType toNotification(MavenSearchNotification type) {
        if (type == null) {
            return NotificationType.INFORMATION;
        }

        if (type == MavenSearchNotification.ERROR) {
            return NotificationType.ERROR;
        }

        return NotificationType.INFORMATION;
    }

    public static Icon getIcon(MavenSearchAdvertiser type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case NORMAL:
                return MavenPluginIcon.BOTTOM;

            case RUNNING:
                return MavenPluginIcon.BOTTOM_WAITING;

            case ERROR:
                return MavenPluginIcon.BOTTOM_ERROR;

            default:
                throw new UnsupportedOperationException(type.name());
        }
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

    /**
     * 返回快捷键的文本
     *
     * @param keystroke 快捷键信息，如：press shift
     * @return 快捷键的文本，如: ⇧
     */
    public static String getShortcutText(String keystroke) {
        try {
            KeyStroke shiftKeyStroke = KeyStroke.getKeyStroke(keystroke);
            Shortcut shiftShortcut = new KeyboardShortcut(shiftKeyStroke, null);
            return KeymapUtil.getShortcutText(shiftShortcut);
        } catch (Throwable e) {
            return keystroke;
        }
    }

    public static String parseJDKVersion(File file) {
        if (file != null) {
            try (JarFile jarfile = new JarFile(file)) {
                JarEntry entry = jarfile.stream().filter(e -> e.getName().endsWith(".class")).findFirst().orElse(null);
                if (entry != null) {
                    try (InputStream in = jarfile.getInputStream(entry)) {
                        in.skip(6); // Skip the first 6 bytes
                        int major = in.read() << 8 | in.read();
                        switch (major) {
                            case 45:
                                return "JDK 1.1";
                            case 46:
                                return "JDK 1.2";
                            case 47:
                                return "JDK 1.3";
                            case 48:
                                return "JDK 1.4";
                            case 49:
                                return "JDK 5";
                            case 50:
                                return "JDK 6";
                            case 51:
                                return "JDK 7";
                            case 52:
                                return "JDK 8";
                            case 53:
                                return "JDK 9";
                            case 54:
                                return "JDK 10";
                            case 55:
                                return "JDK 11";
                            case 56:
                                return "JDK 12";
                            case 57:
                                return "JDK 13";
                            case 58:
                                return "JDK 14";
                            case 59:
                                return "JDK 15";
                            case 60:
                                return "JDK 16";
                            case 61:
                                return "JDK 17";
                            case 62:
                                return "JDK 18";
                            case 63:
                                return "JDK 19";
                            case 64:
                                return "JDK 20";
                            case 65:
                                return "JDK 21";
                            case 66:
                                return "JDK 22";
                        }
                    }
                }
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }
}
