package cn.org.expect.intellijidea.plugin.maven.navigation;

import javax.swing.*;

import cn.org.expect.intellijidea.plugin.maven.MavenArtifact;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderIcon;
import cn.org.expect.util.Ensure;
import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.platform.backend.navigation.NavigationRequest;
import com.intellij.util.TextWithIcon;

public class MavenFinderNavigationItem implements NavigationItem, ItemPresentation, MavenFinderNavigation {

    private final MavenArtifact artifact;

    private static volatile long NUMBER = 0;

    private final long id;

    private volatile Icon icon;

    public MavenFinderNavigationItem(MavenArtifact artifact) {
        this.id = NUMBER++;
        this.artifact = Ensure.notNull(artifact);
        this.icon = MavenFinderIcon.RIGHT_REMOTE;
    }

    public String getName() {
        return MavenFinderNavigationItem.class.getSimpleName() + ":" + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }

    public MavenArtifact getArtifact() {
        return artifact;
    }

    @Override
    public TextWithIcon getRightLabel() {
        return new TextWithIcon("", this.icon);
    }

    public ItemPresentation getPresentation() {
        return this;
    }

    public void navigate(boolean requestFocus) {
        BrowserUtil.browse("");
    }

    public boolean canNavigate() {
        return false;
    }

    public boolean canNavigateToSource() {
        return false;
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((MavenFinderNavigationItem) o).id == this.id;
    }

    @Override
    public NavigationRequest navigationRequest() {
        return NavigationItem.super.navigationRequest();
    }

    @Override
    public String getPresentableText() {
        return "      " + this.artifact.getVersion() + " ";
    }

    @Override
    public String getLocationString() {
        return "";
    }

    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }
}
