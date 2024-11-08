package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import com.intellij.util.TextWithIcon;

public class MavenFinderNavigationCatalog extends AbstractMavenFinderNavigation {

    public MavenFinderNavigationCatalog(MavenArtifact artifact) {
        super(artifact, MavenFinderIcon.LEFT_FOLD);
    }

    @Override
    public TextWithIcon getRightIcon() {
        return new TextWithIcon(this.artifact.getType() + " ", MavenFinderIcon.RIGHT);
    }

    @Override
    public String getLocationString() {
        return this.artifact.getGroupId();
    }

    @Override
    public Icon getIcon(boolean unused) {
        return this.icon;
    }
}
