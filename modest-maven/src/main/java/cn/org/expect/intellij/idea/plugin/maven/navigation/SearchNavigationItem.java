package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.concurrent.MavenSearchPomInfoJob;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.MavenIcon;
import cn.org.expect.maven.pom.PomInfo;
import cn.org.expect.util.StringUtils;

public class SearchNavigationItem extends AbstractSearchNavigation {

    /** 子节点 */
    private final List<SearchNavigationDetail> child;

    public SearchNavigationItem(Artifact artifact) {
        super(artifact);
        this.child = new ArrayList<>();
        this.setDepth(2);
        this.setPresentableText(artifact.getVersion());
        this.setLocationString("");
        this.setLeftIcon(null);
        this.setRightIcon(MavenIcon.RIGHT_REMOTE);
        this.setRightText("");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return this.child;
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
        this.update(search, this.getArtifact());
        search.display();
    }

    public void setFold(MavenSearch search) {
        this.setFold(true);
        this.setLeftIcon(null);
        search.display();
    }

    public void unfold(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        PomInfo pomInfo = search.getPomInfoRepository().select(artifact);
        if (pomInfo != null) {
            if (this.child.isEmpty()) {
                List<PomInfo.Developer> developers = pomInfo.getDevelopers();
                for (int i = 0; i < developers.size(); i++) {
                    PomInfo.Developer developer = developers.get(i);
                    SearchNavigationDetail detail = new SearchNavigationDetail(artifact);
                    detail.setRightIcon(MavenIcon.RIGHT_DEVELOPER);
                    detail.setPresentableText(developer.getName());
                    detail.setLocationString(pomInfo.toDisplayString(developer.getOrganization(), developer.getEmail(), StringUtils.join(developer.getRoles(), " "), developer.getTimezone()));
                    this.child.add(detail);
                }

                List<PomInfo.License> licenses = pomInfo.getLicenses();
                for (int i = 0; i < licenses.size(); i++) {
                    PomInfo.License license = licenses.get(i);
                    SearchNavigationDetail detail = new SearchNavigationDetail(artifact);
                    detail.setRightIcon(MavenIcon.RIGHT_LICENSE);
                    detail.setPresentableText(license.getName());
                    detail.setLocationString(pomInfo.toDisplayString(license.getUrl(), license.getComments()));
                    this.child.add(detail);
                }
            }

            this.setLeftIcon(null);
        } else {
            this.update(search, artifact);
        }
    }

    public void fold(MavenSearch search) {
        this.child.clear();
        this.setLeftIcon(null);
    }

    private void update(MavenSearch search, Artifact artifact) {
        if (search.getService().isRunning(MavenSearchPomInfoJob.class, job -> job.getArtifact().equals(artifact))) {
            this.setLeftIcon(MavenIcon.LEFT_WAITING);
        } else {
            this.setLeftIcon(null);
        }
    }
}
