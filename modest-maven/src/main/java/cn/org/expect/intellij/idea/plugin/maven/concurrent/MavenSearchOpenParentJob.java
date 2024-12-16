package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigationList;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.ArtifactSearchExtraJob;
import cn.org.expect.maven.concurrent.ArtifactSearchPatternJob;
import cn.org.expect.maven.pom.PomInfo;
import cn.org.expect.maven.repository.ArtifactSearchResult;

public class MavenSearchOpenParentJob extends MavenSearchArtifactJob {

    public MavenSearchOpenParentJob(Artifact artifact) {
        super(artifact, "maven.search.job.open.parent.project.description", artifact.toMavenId());
    }

    public int execute() throws Exception {
        Artifact artifact = this.getArtifact();
        MavenSearchPlugin plugin = this.getSearch();
        PomInfo pomInfo = plugin.getPomInfoRepository().select(artifact);
        if (pomInfo != null) {
            PomInfo.Parent parent = pomInfo.getParent();
            String pattern = parent.getGroupId() + ":" + parent.getArtifactId();

            ArtifactSearchResult result = plugin.aware(new ArtifactSearchPatternJob(pattern, false)).queryExtra(plugin.getDatabase(), pattern);
            if (result != null) {
                MavenSearchNavigationList navigationList = plugin.toNavigationList(result);
                for (int i = 0; i < navigationList.size(); i++) {
                    MavenSearchNavigation navigation = navigationList.get(i);
                    if (navigation.getDepth() == 1) {
                        navigation.setUnfold(plugin); // 展开
                        plugin.getService().waitFor(ArtifactSearchExtraJob.class, navigation.getArtifact()); // 等待后台任务执行完毕
                        navigation.unfold(plugin); // 展开操作

                        List<? extends MavenSearchNavigation> childList = navigation.getNavigationList();
                        for (int j = childList.size() - 1; j >= 0; j--) {
                            MavenSearchNavigation child = childList.get(j);

                            Artifact childArtifact = child.getArtifact();
                            if (childArtifact.equals(parent.getGroupId(), parent.getArtifactId(), parent.getVersion())) {
                                child.setUnfold(plugin);
                                plugin.getContext().setSelectNavigation(child);
                                break;
                            }
                        }
                        break;
                    }
                }

                plugin.getSearchListener().setDisplay(true);
                plugin.getContext().setSearchText(pattern);
                plugin.getContext().setVisibleRect(null);
                plugin.getContext().setSearchResult(result);
                plugin.getContext().setNavigationList(navigationList);
                plugin.getIdeaUI().getSearchField().setText(pattern);
                // 等待 child.setUnfold(plugin); 代码中执行的显示操作
            }
        }
        return 0;
    }
}
