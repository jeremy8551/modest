package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * 图标详见 {@linkplain com.intellij.icons.AllIcons}
 */
public class Icons {

    public static final Icon MAVEN_REPOSITORY_LEFT = load("/META-INF/maven-repository-left.svg");

    public static final Icon MAVEN_REPOSITORY_RIGHT = load("/META-INF/maven-repository-right.svg");

    public static Icon load(String path) {
        return IconLoader.getIcon(path, Icons.class);
    }
}
