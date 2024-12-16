package cn.org.expect.intellij.idea.plugin.maven;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * Idea 官方图标: {@linkplain com.intellij.icons.AllIcons}
 */
public class MavenSearchPluginIcon {

    public static final Icon CLASS_LEFT = load("/cn/org/expect/intellij/idea/plugin/maven/maven-search-class-left.svg");

    public static final Icon LEFT_FOLD = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-left.svg");

    public static final Icon LEFT_HAS_QUERY = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-left-hasquery.svg");

    public static final Icon LEFT_UNFOLD = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-left-unfold.svg");

    public static final Icon LEFT_WAITING = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-left-waiting.svg");

    public static final Icon RIGHT = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right.svg");

    public static final Icon BOTTOM = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-bottom.svg");

    public static final Icon BOTTOM_WAITING = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-bottom-waiting.svg");

    public static final Icon BOTTOM_ERROR = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-bottom-error.svg");

    public static final Icon RIGHT_LOCAL = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-local.svg");

    public static final Icon RIGHT_REMOTE = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-remote.svg");

    public static final Icon RIGHT_DOWNLOAD = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-download.svg");

    public static final Icon RIGHT_DEVELOPER = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-developer.svg");

    public static final Icon RIGHT_LICENSE = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-license.svg");

    public static final Icon RIGHT_PROJECT = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-project.svg");

    public static final Icon RIGHT_PARENT = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-parent.svg");

    public static final Icon RIGHT_SCM = load("/cn/org/expect/intellij/idea/plugin/maven/maven-repository-right-scm.svg");

    public static Icon load(String filepath) {
        return IconLoader.getIcon(filepath, MavenSearchPluginIcon.class);
    }
}
