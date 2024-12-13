package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.pom.PomInfo;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class SearchNavigationItem extends AbstractSearchNavigation {

    public SearchNavigationItem(Artifact artifact) {
        super(artifact);
        this.setDepth(2);
        this.setPresentableText(artifact.getVersion());
        this.setLocationString("");
        this.setLeftIcon(null);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT_REMOTE);
        this.setRightText("");
    }

    public boolean supportMenu() {
        return true;
    }

    public boolean supportFold(MavenSearch search) {
        return true;
    }

    public void setUnfold(MavenSearch search) {
        this.setFold(false);
        search.asyncPomInfo(this.getArtifact());
        search.display();
    }

    public void setFold(MavenSearch search) {
        this.setFold(true);
        search.display();
    }

    public void unfold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
        Artifact artifact = this.getArtifact();
        PomInfo pomInfo = search.getPomInfoRepository().select(artifact);
        if (pomInfo != null) {
            List<PomInfo.Developer> developers = pomInfo.getDevelopers();
            for (int i = 0; i < developers.size(); i++) {
                PomInfo.Developer developer = developers.get(i);
                SearchNavigationDetail detail = new SearchNavigationDetail(artifact);
                detail.setPresentableText("developer: ");
                detail.setLocationString(pomInfo.toDisplayString(developer.getName(), developer.getOrganization(), developer.getEmail(), StringUtils.join(developer.getRoles(), " "), developer.getTimezone()));
                list.add(new SearchEverywhereFoundElementInfo(detail, search.getSettings().getNavigationPriority(), search.getContributor()));
            }

            List<PomInfo.License> licenses = pomInfo.getLicenses();
            for (int i = 0; i < licenses.size(); i++) {
                PomInfo.License license = licenses.get(i);
                SearchNavigationDetail detail = new SearchNavigationDetail(artifact);
                detail.setPresentableText("license: ");
                detail.setLocationString(pomInfo.toDisplayString(license.getName(), license.getUrl(), license.getComments()));
                list.add(new SearchEverywhereFoundElementInfo(detail, search.getSettings().getNavigationPriority(), search.getContributor()));
            }
        }
    }

    public void fold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
    }
}
