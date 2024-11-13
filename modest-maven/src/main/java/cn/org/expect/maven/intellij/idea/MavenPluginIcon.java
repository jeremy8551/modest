package cn.org.expect.maven.intellij.idea;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * Idea 官方图标: {@linkplain com.intellij.icons.AllIcons}
 */
public class MavenPluginIcon {

    public static final Icon LEFT_FOLD = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-left.svg");

    public static final Icon LEFT_HAS_QUERY = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-left-hasquery.svg");

    public static final Icon LEFT_UNFOLD = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-left-unfold.svg");

    public static final Icon LEFT_WAITING = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-left-waiting.svg");

    public static final Icon RIGHT = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-right.svg");

    public static final Icon BOTTOM = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-bottom.svg");

    public static final Icon BOTTOM_WAITING = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-bottom-waiting.svg");

    public static final Icon BOTTOM_ERROR = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-bottom-error.svg");

    public static final Icon RIGHT_LOCAL = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-right-local.svg");

    public static final Icon RIGHT_REMOTE = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-right-remote.svg");
    
    public static final Icon RIGHT_DOWNLOAD = load("/cn/org/expect/maven/intellij/idea/icon/maven-repository-right-download.svg");

    public static Icon load(String filepath) {
        return IconLoader.getIcon(filepath, MavenPluginIcon.class);
    }
}
