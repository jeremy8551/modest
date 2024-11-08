package cn.org.expect.maven.intellij.idea.navigation;

import javax.swing.*;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.intellij.idea.MavenPluginIcon;
import com.intellij.util.TextWithIcon;

public class SearchNavigation extends AbstractSearchNavigation {

    public SearchNavigation(MavenArtifact artifact) {
        super(artifact, MavenPluginIcon.LEFT_FOLD);
    }

    @Override
    public TextWithIcon getRightIcon() {
        return new TextWithIcon(this.artifact.getType() + " ", MavenPluginIcon.RIGHT);
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
