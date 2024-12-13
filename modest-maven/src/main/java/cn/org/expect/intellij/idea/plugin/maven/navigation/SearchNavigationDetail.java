package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;

public class SearchNavigationDetail extends AbstractSearchNavigation {

    public SearchNavigationDetail(Artifact artifact) {
        super(artifact);
        this.setDepth(3);
        this.setPresentableText(artifact.getVersion());
        this.setLocationString("");
        this.setLeftIcon(null);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
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
