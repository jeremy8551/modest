package cn.org.expect.modest.idea.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.NavigationItem;

public class MavenFinderNavigationItem implements NavigationItem {

    private final MavenFinderItemPresentation presentation;

    private static volatile long NUMBER = 0;

    private long id;

    public MavenFinderNavigationItem(MavenFinderItem item) {
        this.id = NUMBER++;
        this.presentation = new MavenFinderItemPresentation(item);
    }

    public String getName() {
        return this.presentation.getItem().getArtifact() + ", " + this.presentation.getItem().getId();
    }

    public MavenFinderItemPresentation getPresentation() {
        return this.presentation;
    }

    public void navigate(boolean requestFocus) {
        BrowserUtil.browse(this.presentation.getItem().getRepositoryUrl());
    }

    public boolean canNavigate() {
        return true;
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass()) && ((MavenFinderNavigationItem) o).id == this.id;
    }
}
