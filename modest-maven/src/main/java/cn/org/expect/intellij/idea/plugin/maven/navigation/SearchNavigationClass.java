package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class SearchNavigationClass extends AbstractSearchNavigation {

    public SearchNavigationClass(Artifact artifact) {
        super(artifact);
        this.setDepth(1);
        this.setPresentableText(this.artifact.getArtifactId());
        this.setLocationString(" " + this.artifact.getGroupId() + "  (" + this.artifact.getVersion() + ")");
        this.setLeftIcon(MavenSearchPluginIcon.CLASS_LEFT);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
        this.setRightText(artifact.getType() + " ");
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

    public void unfold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
    }

    public void fold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
    }
}
