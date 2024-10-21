package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.platform.backend.navigation.NavigationRequest;

public class MavenFinderNavigationList implements NavigationItem, ItemPresentation, MavenFinderNavigation {

    private final MavenFinderItem item;

    private static volatile long NUMBER = 0;

    private final long id;

    public MavenFinderNavigationList(MavenFinderItem item) {
        this.id = NUMBER++;
        this.item = Ensure.notNull(item);
    }

    public String getName() {
        return MavenFinderNavigationList.class.getSimpleName() + ":" + item.getGroupId() + ":" + item.getArtifact() + ":" + item.getVersion();
    }

    public ItemPresentation getPresentation() {
        return this;
    }

    public void navigate(boolean requestFocus) {
        BrowserUtil.browse(this.item.getNavigateUrl());
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
        return this.item.getArtifact() + "     " + StringUtils.right(this.item.getVersion(), 40, ' ') + StringUtils.right(Dates.format19(this.item.getTimestamp()), 27, ' ');
    }

    @Override
    public String getLocationString() {
        return "";
    }

    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }
}
