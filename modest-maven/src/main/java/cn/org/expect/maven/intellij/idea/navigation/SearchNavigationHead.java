package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.intellij.idea.MavenSearchPluginIcon;
import cn.org.expect.maven.repository.MavenArtifact;
import com.intellij.util.TextWithIcon;

public class SearchNavigationHead extends AbstractSearchNavigation {

    public SearchNavigationHead(MavenArtifact artifact) {
        super(artifact, MavenSearchPluginIcon.LEFT_FOLD);
    }

    @Override
    public TextWithIcon getRightIcon() {
        return new TextWithIcon(this.artifact.getType() + " ", MavenSearchPluginIcon.RIGHT);
    }

    @Override
    public String getLocationString() {
        return " " + this.artifact.getGroupId();
    }

    @Override
    public Icon getIcon(boolean unused) {
        return this.icon;
    }
}
