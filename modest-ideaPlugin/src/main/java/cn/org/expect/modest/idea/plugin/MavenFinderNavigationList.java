package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.platform.backend.navigation.NavigationRequest;

public class MavenFinderNavigationList implements NavigationItem, ItemPresentation, MavenFinderNavigation {

    private final MavenArtifact artifact;

    private static volatile long NUMBER = 0;

    private final long id;

    public MavenFinderNavigationList(MavenArtifact artifact) {
        this.id = NUMBER++;
        this.artifact = Ensure.notNull(artifact);
    }

    public String getName() {
        return MavenFinderNavigationList.class.getSimpleName() + ":" + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }

    public MavenArtifact getArtifact() {
        return artifact;
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
        return o != null && o.getClass().equals(this.getClass()) && ((MavenFinderNavigationList) o).id == this.id;
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
        return Dates.format19(this.artifact.getTimestamp());
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }
}
