package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.maven.MavenIcon;
import cn.org.expect.maven.Artifact;

public class SearchNavigationClass extends AbstractSearchNavigation {

    public SearchNavigationClass(Artifact artifact) {
        super(artifact);
        this.setDepth(1);
        this.setPresentableText(artifact.getArtifactId());
        this.setLocationString(" " + artifact.getGroupId() + "  (" + artifact.getVersion() + ")");
        this.setLeftIcon(MavenIcon.CLASS_LEFT);
        this.setRightIcon(MavenIcon.RIGHT_REMOTE);
        this.setRightText(artifact.getType() + " ");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return List.of();
    }

    public boolean supportFold(MavenSearch search) {
        this.update(search);
        return false;
    }

    public boolean supportMenu() {
        return true;
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
