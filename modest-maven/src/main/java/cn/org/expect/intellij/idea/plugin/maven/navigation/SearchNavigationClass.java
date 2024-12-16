package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.search.SearchNavigation;

public class SearchNavigationClass extends AbstractSearchNavigation {

    public SearchNavigationClass(MavenSearchPlugin plugin, Artifact artifact) {
        super(plugin, artifact);
        this.setDepth(1);
        this.setPresentableText(artifact.getArtifactId());
        this.setLocationString(" " + artifact.getGroupId() + "  (" + artifact.getVersion() + ")");
        this.setLeftIcon(MavenSearchPluginIcon.CLASS_LEFT);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
        this.setRightText(artifact.getType() + " ");
    }

    public List<? extends SearchNavigation> getNavigationList() {
        return new ArrayList<>();
    }

    public boolean supportFold() {
        this.update();
        return false;
    }

    public boolean supportMenu() {
        return true;
    }

    public void displayMenu(SearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
        MavenSearchPlugin plugin = this.getSearch();
        plugin.getResultMenu().displayItemMenu(plugin, navigation, topMenu, selectedIndex, 22);
    }

    public void setUnfold() {
    }

    public void setFold() {
    }

    public void unfold() {
    }

    public void fold() {
    }
}
