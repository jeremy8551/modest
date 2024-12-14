package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;

public class SearchNavigationDetail extends AbstractSearchNavigation {

    public SearchNavigationDetail(Artifact artifact, Icon rightIcon, String presentableText, String locationString, String rightText) {
        super(artifact);
        this.setDepth(3);
        this.setPresentableText(presentableText);
        this.setLocationString(locationString);
        this.setLeftIcon(null);
        this.setRightIcon(rightIcon);
        this.setRightText(rightText);
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return List.of();
    }

    public boolean supportMenu() {
        return true;
    }

    public void displayMenu(MavenSearchPlugin plugin, MavenSearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
        plugin.getResultMenu().displayDetailMenu(plugin, navigation, topMenu, selectedIndex);
    }

    public boolean supportFold(MavenSearch search) {
        return false;
    }

    public void setUnfold(MavenSearch search) {
    }

    public void setFold(MavenSearch search) {
    }

    public void unfold(MavenSearch search) {
    }

    public void fold(MavenSearch search) {
    }
}
