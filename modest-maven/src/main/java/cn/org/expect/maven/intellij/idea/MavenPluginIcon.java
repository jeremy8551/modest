package cn.org.expect.maven.intellij.idea;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * Idea 官方图标: {@linkplain com.intellij.icons.AllIcons}
 */
public class MavenPluginIcon {

    public static final Icon LEFT_FOLD = load("/META-INF/maven-repository-left.svg");

    public static final Icon LEFT_HAS_QUERY = load("/META-INF/maven-repository-left-hasquery.svg");

    public static final Icon LEFT_UNFOLD = load("/META-INF/maven-repository-left-unfold.svg");

    public static final Icon LEFT_WAITING = load("/META-INF/maven-repository-left-waiting.svg");

    public static final Icon RIGHT = load("/META-INF/maven-repository-right.svg");

    public static final Icon BOTTOM = load("/META-INF/maven-repository-bottom.svg");

    public static final Icon BOTTOM_WAITING = load("/META-INF/maven-repository-bottom-waiting.svg");

    public static final Icon BOTTOM_ERROR = load("/META-INF/maven-repository-bottom-error.svg");

    public static final Icon RIGHT_LOCAL = load("/META-INF/maven-repository-right-local.svg");

    public static final Icon RIGHT_REMOTE = load("/META-INF/maven-repository-right-remote.svg");

    public static Icon load(String path) {
        return IconLoader.getIcon(path, MavenPluginIcon.class);
    }
}
