package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

/**
 * 图标详见 {@linkplain com.intellij.icons.AllIcons}
 */
public class MavenFinderIcons {

    public static final Icon MAVEN_REPOSITORY_LEFT = load("/META-INF/maven-repository-left.svg");

    public static final Icon MAVEN_REPOSITORY_LEFT_HAS_QUERY = load("/META-INF/maven-repository-left-hasquery.svg");

    public static final Icon MAVEN_REPOSITORY_LEFT_UNFOLD = load("/META-INF/maven-repository-left-unfold.svg");

    public static final Icon MAVEN_REPOSITORY_LEFT_WAITING = load("/META-INF/maven-repository-left-waiting.svg");

    public static final Icon MAVEN_REPOSITORY_RIGHT = load("/META-INF/maven-repository-right.svg");

    public static final Icon MAVEN_REPOSITORY_BOTTOM = load("/META-INF/maven-repository-bottom.svg");
    
    public static final Icon MAVEN_REPOSITORY_BOTTOM_WAITING = load("/META-INF/maven-repository-bottom-waiting.svg");

    public static Icon load(String path) {
        return IconLoader.getIcon(path, MavenFinderIcons.class);
    }
}
