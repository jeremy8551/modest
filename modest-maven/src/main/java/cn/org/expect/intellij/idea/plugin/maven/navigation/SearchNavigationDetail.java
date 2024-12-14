package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.maven.Artifact;

public class SearchNavigationDetail extends AbstractSearchNavigation {

    public SearchNavigationDetail(Artifact artifact, Icon rightIcon, String presentableText, String locationString) {
        super(artifact);
        this.setDepth(3);
        this.setPresentableText(presentableText);
        this.setLocationString(locationString);
        this.setLeftIcon(null);
        this.setRightIcon(rightIcon);
        this.setRightText("");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return List.of();
    }

    public boolean supportMenu() {
        return false;
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
