package cn.org.expect.maven.intellij.idea.navigation;

import java.io.File;
import javax.swing.*;

import cn.org.expect.maven.intellij.idea.IdeaUtils;
import cn.org.expect.maven.intellij.idea.MavenPluginIcon;
import cn.org.expect.maven.repository.MavenArtifact;
import com.intellij.util.TextWithIcon;

public class SearchNavigationItem extends AbstractSearchNavigation {

    protected File jarfile;

    public SearchNavigationItem(MavenArtifact artifact, File jarfile) {
        super(artifact, MavenPluginIcon.RIGHT_REMOTE);
        this.jarfile = jarfile;
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

    @Override
    public TextWithIcon getRightIcon() {
        String version = IdeaUtils.parseJDKVersion(this.jarfile);
        return new TextWithIcon(version == null ? "" : version + " ", this.icon);
    }
}
