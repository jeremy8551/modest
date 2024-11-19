package cn.org.expect.intellij.idea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.impl.MavenArtifactImpl;

public class EmptySearchNavigation extends SearchNavigationHead {

    public EmptySearchNavigation() {
        super(new MavenArtifactImpl());
    }

    public Icon getIcon(boolean unused) {
        return null;
    }

    public String getLocationString() {
        return "";
    }
}
