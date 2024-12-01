package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.io.File;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginUtils;
import cn.org.expect.maven.repository.Artifact;
import com.intellij.util.TextWithIcon;

public class SearchNavigationItem extends AbstractSearchNavigation {

    protected File jarfile;

    public SearchNavigationItem(Artifact artifact, File jarfile) {
        super(artifact, MavenSearchPluginIcon.RIGHT_REMOTE);
        this.jarfile = jarfile;
    }

    public String getPresentableText() {
        return this.artifact.getVersion();
    }

    public String getLocationString() {
        return "";
    }

    public Icon getIcon(boolean unused) {
        return null;
    }

    public TextWithIcon getRightIcon() {
        String version = MavenSearchPluginUtils.parseJDKVersion(this.jarfile);
        return new TextWithIcon(version == null ? "" : version + " ", this.icon);
    }
}
