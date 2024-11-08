package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;

public class MavenFinderNavigationItem extends AbstractMavenFinderNavigation {

    public MavenFinderNavigationItem(MavenArtifact artifact) {
        super(artifact, MavenFinderIcon.RIGHT_REMOTE);
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
