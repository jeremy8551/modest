package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

public class Icons {

    public static final Icon MAVEN_REPOSITORY = load("/META-INF/mavenRepository.png");

    private static Icon load(String path) {
        return IconLoader.getIcon(path, Icons.class);
    }
}
