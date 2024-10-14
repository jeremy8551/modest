package cn.org.expect.modest.idea.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;

public class MavenFinderNavigationItem implements NavigationItem {

    private final MavenFinderItemPresentation presentation;

    public MavenFinderNavigationItem(MavenFinderItem item) {
        this.presentation = new MavenFinderItemPresentation(item);
    }

    public String getName() {
        return this.presentation.getItem().getArtifact() + ", " + this.presentation.getItem().getId();
    }

    public ItemPresentation getPresentation() {
        return this.presentation;
    }

    public void navigate(boolean requestFocus) {
        BrowserUtil.browse(this.presentation.getItem().getRepositoryUrl());
    }

    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
