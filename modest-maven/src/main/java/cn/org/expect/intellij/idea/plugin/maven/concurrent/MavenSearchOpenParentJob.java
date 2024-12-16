package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigationList;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.ArtifactSearchExtraJob;
import cn.org.expect.maven.concurrent.ArtifactSearchPatternJob;
import cn.org.expect.maven.pom.Pom;
import cn.org.expect.maven.repository.ArtifactSearchResult;

public class MavenSearchOpenParentJob extends MavenSearchArtifactJob {

    public MavenSearchOpenParentJob(Artifact artifact) {
        super(artifact, "maven.search.job.open.parent.project.description", artifact.toMavenId());
    }

    public int execute() throws Exception {
        Artifact artifact = this.getArtifact();
        MavenSearchPlugin plugin = this.getSearch();
        Pom pomInfo = plugin.getPomRepository().select(artifact);
        if (pomInfo != null) {
            Pom.Parent parent = pomInfo.getParent();
            String pattern = parent.getGroupId() + ":" + parent.getArtifactId();

            ArtifactSearchResult result = plugin.aware(new ArtifactSearchPatternJob(pattern)).queryExtra(plugin.getDatabase(), pattern);
            if (result != null) {
                plugin.saveSearchResult(result);
                MavenSearchNavigationList navigationList = plugin.getContext().getNavigationList();
                for (int i = 0; i < navigationList.size(); i++) {
                    MavenSearchNavigation navigation = navigationList.get(i);
                    if (navigation.getDepth() == 1) {
                        navigation.setUnfold(); // 展开
                        plugin.getService().waitFor(ArtifactSearchExtraJob.class, navigation.getArtifact()); // 等待后台任务执行完毕
                        navigation.unfold(); // 展开操作

                        List<? extends MavenSearchNavigation> childList = navigation.getNavigationList();
                        for (int j = childList.size() - 1; j >= 0; j--) {
                            MavenSearchNavigation child = childList.get(j);
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
