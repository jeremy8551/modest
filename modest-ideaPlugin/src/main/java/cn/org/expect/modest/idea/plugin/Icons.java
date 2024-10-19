package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * 图标详见 {@linkplain com.intellij.icons.AllIcons}
 */
public class Icons {

    public static final Icon MAVEN_REPOSITORY = load("/META-INF/mavenRepository.png");

    public static final Icon MAVEN_REPOSITORY_RIGHT = load("/META-INF/mavenRepository-Right.svg");

    public static Icon load(String path) {
        return IconLoader.getIcon(path, Icons.class);
    }
}
