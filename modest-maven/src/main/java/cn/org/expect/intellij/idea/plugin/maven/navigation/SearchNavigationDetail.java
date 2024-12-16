package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;

public class SearchNavigationDetail extends AbstractSearchNavigation {

    public SearchNavigationDetail(MavenSearchPlugin plugin, Artifact artifact, Icon rightIcon, String presentableText, String locationString, String rightText) {
        super(plugin, artifact);
        this.setDepth(3);
        this.setPresentableText(presentableText);
        this.setLocationString(locationString);
        this.setLeftIcon(null);
        this.setRightIcon(rightIcon);
        this.setRightText(rightText);
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return new ArrayList<>();
    }

    public boolean supportMenu() {
        return true;
    }

    public void displayMenu(MavenSearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
        MavenSearchPlugin plugin = this.getPlugin();
        plugin.getResultMenu().displayDetailMenu(plugin, navigation, topMenu, selectedIndex);
    }

    public boolean supportFold() {
        return false;
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
