package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.util.TextWithIcon;

public class MavenFinderNavigationCatalog implements NavigationItem, ItemPresentation, MavenFinderNavigation {

    private final MavenArtifact artifact;

    private static volatile long NUMBER = 0;

    private final long id;

    private volatile Icon icon;

    public MavenFinderNavigationCatalog(MavenArtifact artifact) {
        this.id = NUMBER++;
        this.artifact = Ensure.notNull(artifact);
        this.icon = MavenFinderIcon.LEFT_FOLD;
    }

    public String getName() {
        return MavenFinderNavigationCatalog.class.getSimpleName() + ":" + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId() + ":" + this.artifact.getVersion();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public MavenArtifact getArtifact() {
        return this.artifact;
    }

    @Override
    public TextWithIcon getRightLabel() {
        return new TextWithIcon(this.artifact.getType() + " ", MavenFinderIcon.RIGHT);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public void navigate(boolean requestFocus) {
        BrowserUtil.browse("");
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass()) && ((MavenFinderNavigationCatalog) obj).id == this.id;
    }

    @Override
    public String getPresentableText() {
        if (StringUtils.isBlank(this.artifact.getArtifactId())) {
            return "";
        } else {
            return this.artifact.getArtifactId();
        }
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
