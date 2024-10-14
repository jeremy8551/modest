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
        return this.presentation.getItem().getArtifact();
    }

    public ItemPresentation getPresentation() {
        return this.presentation;
    }

    public void navigate(boolean requestFocus) {
        StringBuilder buf = new StringBuilder();
        buf.append("https://repo1.maven.org/maven2/");
        buf.append(this.presentation.getItem().getGroupId().replace('.', '/'));
        buf.append('/');
        buf.append(this.presentation.getItem().getArtifact().replace('.', '/'));
        BrowserUtil.browse(buf.toString());
    }

    public boolean canNavigate() {
        return true;
    }
}
