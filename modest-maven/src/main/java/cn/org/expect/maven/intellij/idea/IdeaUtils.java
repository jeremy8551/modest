package cn.org.expect.maven.intellij.idea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchNotification;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapUtil;

public class IdeaUtils {

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

    public static void download(String urlString, File file) throws IOException {
        if (FileUtils.createDirectory(file.getParentFile()) && FileUtils.createFile(file, true)) {
            URL website = new URL(urlString);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                } finally {
                    IO.close(fos);
                }
            } finally {
                IO.close(rbc);
            }
        }
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
}
