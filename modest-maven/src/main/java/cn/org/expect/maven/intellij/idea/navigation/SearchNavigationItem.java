package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.intellij.idea.MavenPluginIcon;

public class SearchNavigationItem extends AbstractSearchNavigation {

    public SearchNavigationItem(MavenArtifact artifact) {
        super(artifact, MavenPluginIcon.RIGHT_REMOTE);
    }

    @Override
    public String getPresentableText() {
        return this.artifact.getVersion();
    }

    @Override
    public String getLocationString() {
        return "";
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }
}
