package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchEverywhereNavigationCollection;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.MavenArtifactJob;
import cn.org.expect.maven.concurrent.SearchExtraJob;
import cn.org.expect.maven.concurrent.SearchPatternJob;
import cn.org.expect.maven.pom.Pom;
import cn.org.expect.maven.repository.ArtifactSearchResult;

public class MavenPluginOpenParentJob extends MavenArtifactJob {

    public MavenPluginOpenParentJob(Artifact artifact) {
        super(artifact, "maven.search.job.open.parent.project.description", artifact.toMavenId());
    }

    public int execute() throws Exception {
        Artifact artifact = this.getArtifact();
        MavenSearchPlugin plugin = (MavenSearchPlugin) this.getSearch();
        Pom pomInfo = plugin.getPomRepository().select(artifact);
        if (pomInfo != null) {
            Pom.Parent parent = pomInfo.getParent();
            String pattern = parent.getGroupId() + ":" + parent.getArtifactId();

            ArtifactSearchResult result = plugin.aware(new SearchPatternJob(pattern)).queryExtra(plugin.getDatabase(), pattern);
            if (result != null) {
                plugin.saveSearchResult(result);
                SearchEverywhereNavigationCollection navigationList = plugin.getContext().getNavigationList();
                for (int i = 0; i < navigationList.size(); i++) {
                    SearchNavigation navigation = navigationList.get(i);
                    if (navigation.getDepth() == 1) {
                        navigation.setUnfold(); // 展开
                        plugin.getService().waitFor(SearchExtraJob.class, job -> navigation.getArtifact().equals(job.getGroupId(), job.getArtifactId()), 10 * 1000); // 等待后台任务执行完毕
                        navigation.unfold(); // 展开操作

                        List<? extends SearchNavigation> childList = navigation.getNavigationList();
                        for (int j = childList.size() - 1; j >= 0; j--) {
                            SearchNavigation child = childList.get(j);
                            Artifact childArtifact = child.getArtifact();
                            if (childArtifact.equals(parent.getGroupId(), parent.getArtifactId(), parent.getVersion())) {
                                child.setUnfold();
                                plugin.getContext().setSelectedNavigation(child);
                                break;
                            }
                        }
                        break;
                    }
                }

                plugin.getSearchListener().setDisplay(true);
                plugin.getContext().setVisibleRect(null);
                plugin.getIdeaUI().getSearchField().setText(pattern);
            }
        }
        return 0;
    }
}
