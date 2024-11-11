package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.impl.MavenArtifactImpl;

public class EmptySearchNavigation extends SearchNavigationHead {

    public EmptySearchNavigation() {
        super(new MavenArtifactImpl());
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }

    @Override
    public String getLocationString() {
        return "";
    }
}
