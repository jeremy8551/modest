package cn.org.expect.intellij.idea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.repository.Artifact;
import com.intellij.util.TextWithIcon;

public class SearchNavigationHead extends AbstractSearchNavigation {

    public SearchNavigationHead(Artifact artifact) {
        super(artifact, MavenSearchPluginIcon.LEFT_FOLD);
    }

    public TextWithIcon getRightIcon() {
        return new TextWithIcon(this.artifact.getType() + " ", MavenSearchPluginIcon.RIGHT);
    }

    public String getLocationString() {
        return " " + this.artifact.getGroupId();
    }

    public Icon getIcon(boolean unused) {
        return this.icon;
    }
}
