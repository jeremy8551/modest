package cn.org.expect.maven;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import cn.org.expect.log.ResourceBundle;
import cn.org.expect.util.StringUtils;
import com.intellij.CommonBundle;

public class MavenResourceBundle implements ResourceBundle {

    /** 国际化资源文件 */
    public final static String BUNDLE_NAME = "messages.MavenSearchPluginBundle";

    /** 国际化资源 */
    public final java.util.ResourceBundle bundle;

    /** 中文资源 */
    public final java.util.ResourceBundle bundleCn;

    /** 函数式接口返回true，表示使用中文的资源文件 */
    private final Predicate<String> useChinese;

    public MavenResourceBundle() {
        this.bundle = java.util.ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT);
        this.bundleCn = java.util.ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINESE);
        this.useChinese = (key) -> "取消".equals(CommonBundle.getCancelButtonText());
    }

    public boolean contains(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }

        Enumeration<String> enumeration = this.bundle.getKeys();
        while (enumeration.hasMoreElements()) {
            if (key.equals(enumeration.nextElement())) {
                return true;
            }
        }
        return false;
    }

    public String get(String key) {
        if (this.useChinese.test(key)) {
            return this.bundleCn.getString(key);
        } else {
            return this.bundle.getString(key);
        }
    }

    public Set<String> getKeys() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        Enumeration<String> keys = this.bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            set.add(key);
        }
        return set;
    }
}
