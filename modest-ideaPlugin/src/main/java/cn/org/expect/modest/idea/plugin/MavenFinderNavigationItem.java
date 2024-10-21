package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;

public class MavenFinderNavigationItem implements NavigationItem, ItemPresentation, MavenFinderNavigation {

    private final MavenArtifact artifact;

    private static volatile long NUMBER = 0;

    private final long id;

    private volatile boolean fold;

    public MavenFinderNavigationItem(MavenArtifact artifact) {
        this.id = NUMBER++;
        this.artifact = artifact;
        this.fold = false;
    }

    public String getName() {
        return MavenFinderNavigationItem.class.getSimpleName() + ":" + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId() + ":" + this.artifact.getVersion();
    }

    public MavenArtifact getArtifact() {
        return artifact;
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
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((MavenFinderNavigationItem) o).id == this.id;
    }

    @Override
    public String getPresentableText() {
        if (StringUtils.isBlank(this.artifact.getGroupId()) && StringUtils.isBlank(this.artifact.getArtifactId())) {
            return "";
        } else {
            return this.artifact.getGroupId() + ":" + this.artifact.getArtifactId();
        }
    }

    @Override
    public String getLocationString() {
        return this.artifact.getType();
    }

    @Override
    public Icon getIcon(boolean unused) {
        return MavenFinderIcons.MAVEN_REPOSITORY_LEFT;
    }

    public boolean isFold() {
        return fold;
    }

    public void setFold(boolean fold) {
        this.fold = fold;
    }
}
