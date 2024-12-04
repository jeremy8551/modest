package cn.org.expect.intellij.idea.plugin.maven;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.*;

import ai.grazie.utils.mpp.StringBuilder;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.ArtifactOption;
import cn.org.expect.maven.search.ArtifactSearchAdvertiser;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import org.json.JSONObject;

public class MavenSearchPluginUtils {
    private final static Log log = LogFactory.getLog(MavenSearchPluginUtils.class);

    public static String escape(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (StringUtils.isLetter(c) || StringUtils.isNumber(c) || StringUtils.inArray(c, '.', '!', '@', '#', '*', '-', '_', '+', '=', '?', ':', '|')) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static NotificationType toNotification(ArtifactSearchNotification type) {
        if (type == null) {
            return NotificationType.INFORMATION;
        }

        if (type == ArtifactSearchNotification.ERROR) {
            return NotificationType.ERROR;
        }

        return NotificationType.INFORMATION;
    }

    public static Icon getIcon(ArtifactSearchAdvertiser type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case NORMAL:
                return MavenSearchPluginIcon.BOTTOM;

            case RUNNING:
                return MavenSearchPluginIcon.BOTTOM_WAITING;

            case ERROR:
                return MavenSearchPluginIcon.BOTTOM_ERROR;

            default:
                throw new UnsupportedOperationException(type.name());
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
            if (log.isErrorEnabled()) {
                log.error(e.getLocalizedMessage(), e);
            }
            return keystroke;
        }
    }

    public static String parseJDKVersion(File file) {
        if (file != null && file.exists() && file.isFile() && file.length() > 0) {
            JarFile jarfile = null;
            try {
                jarfile = new JarFile(file);
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
                log.error(e.getLocalizedMessage());
            } finally {
                IO.closeQuietly(jarfile);
            }
        }
        return null;
    }

    public final static String SETTINGS_TABLE = "MAVEN_SEARCH_PLUGIN_SETTINGS.json";

    public static synchronized void save(MavenSearchPluginSettings settings) {
        File file = new File(settings.getWorkHome(), SETTINGS_TABLE);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(settings);

            if (log.isDebugEnabled()) {
                log.debug("save {}, {}, {}", settings.getClass().getSimpleName(), file.getAbsolutePath(), jsonStr);
            }

            FileUtils.write(file, CharsetName.UTF_8, false, jsonStr);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static synchronized void load(MavenSearchPluginSettings settings) {
        File file = new File(settings.getWorkHome(), SETTINGS_TABLE);
        try {
            if (file.exists() && file.isFile()) {
                String jsonStr = FileUtils.readline(file, CharsetName.UTF_8, 0);

                if (log.isDebugEnabled()) {
                    log.debug("load {}, {}", MavenSearchPluginSettings.class.getSimpleName(), jsonStr);
                }

                JSONObject jsonObject = new JSONObject(jsonStr);
                //  String id = jsonObject.getString("id");
                //  String name = jsonObject.getString("name");
                //  String workHome = jsonObject.getString("workHome");
                long inputIntervalTime = jsonObject.getLong("inputIntervalTime");
                ArtifactOption repositoryId = (ArtifactOption) jsonObject.opt("repository");
                boolean autoSwitchTab = jsonObject.getBoolean("autoSwitchTab");
                int tabIndex = jsonObject.getInt("tabIndex");
                boolean tabVisible = jsonObject.getBoolean("tabVisible");
                int elementPriority = jsonObject.getInt("elementPriority");
                long expireTimeMillis = jsonObject.getLong("expireTimeMillis");
                boolean searchInAllTab = jsonObject.getBoolean("useAllTab");

                //   settings.setId(id);
                //   settings.setName(name);
                //   settings.setWorkHome(new File(workHome));
                settings.setInputIntervalTime(inputIntervalTime);
                settings.setRepositoryInfo(repositoryId);
                settings.setAutoSwitchTab(autoSwitchTab);
                settings.setTabIndex(tabIndex);
                settings.setTabVisible(tabVisible);
                settings.setElementPriority(elementPriority);
                settings.setExpireTimeMillis(expireTimeMillis);
                settings.setUseAllTab(searchInAllTab);
            }
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static String format(long millis) {
        Duration duration = Duration.ofMillis(millis); // 将毫秒数转换为 Duration
        long hours = duration.toHours(); // 提取小时、分钟、秒
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds); // 格式化为 hh:mm:ss
    }

    public static String getTabName() {
        return ArtifactSearchMessage.get("maven.search.tab.name");
    }

    public static String getAllTabName() {
        return "All";
    }
}
