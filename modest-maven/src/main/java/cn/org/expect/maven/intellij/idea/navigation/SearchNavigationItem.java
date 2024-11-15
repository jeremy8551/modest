package cn.org.expect.maven.intellij.idea.navigation;

import java.io.File;
import javax.swing.*;

import cn.org.expect.maven.intellij.idea.MavenSearchPluginIcon;
import cn.org.expect.maven.intellij.idea.MavenSearchUtils;
import cn.org.expect.maven.repository.MavenArtifact;
import com.intellij.util.TextWithIcon;

public class SearchNavigationItem extends AbstractSearchNavigation {

    protected File jarfile;

    public SearchNavigationItem(MavenArtifact artifact, File jarfile) {
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
        String version = MavenSearchUtils.parseJDKVersion(this.jarfile);
        return new TextWithIcon(version == null ? "" : version + " ", this.icon);
    }
}
