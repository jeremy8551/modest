package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.ArtifactSearchExtraJob;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class SearchNavigationHead extends AbstractSearchNavigation {

    /** 子节点 */
    private final List<SearchNavigationItem> child;

    public SearchNavigationHead(Artifact artifact) {
        super(artifact);
        this.child = new ArrayList<>();
        this.setDepth(1);
        this.setPresentableText(artifact.getArtifactId());
        this.setLocationString(" " + artifact.getGroupId());
        this.setLeftIcon(MavenSearchPluginIcon.LEFT_FOLD);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT);
        this.setRightText(artifact.getType() + " ");
    }

    public boolean supportFold(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null && !result.isExpire(search.getSettings().getExpireTimeMillis())) { // 如果导航记录有子节点，则更新图标
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
        }
        return true;
    }

    public void setUnfold(MavenSearch search) {
        this.setFold(false); // 设置为：展开
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result == null || result.isExpire(search.getSettings().getExpireTimeMillis())) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_WAITING); // 更改为：等待图标
            search.asyncSearch(artifact.getGroupId(), artifact.getArtifactId()); // 后台搜索
        } else {
            search.display();
        }
    }

    public void setFold(MavenSearch search) {
        this.setFold(true); // 设置为：折叠
        search.display();
    }

    public void unfold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_UNFOLD);

            if (this.child.isEmpty()) {
                for (Artifact itemArtifact : result.getList()) {
                    this.child.add(new SearchNavigationItem(itemArtifact));
                }
            }

            for (SearchNavigationItem item : this.child) {
                list.add(new SearchEverywhereFoundElementInfo(item, search.getSettings().getNavigationPriority(), search.getContributor()));
                if (item.isFold()) {
                    item.fold(search, list);
                } else {
                    item.unfold(search, list);
                }
                item.update(search);
            }
        }

        this.updateWaitingIcon(search);
    }

    public void fold(MavenSearch search, List<SearchEverywhereFoundElementInfo> list) {
        this.updateWaitingIcon(search);
    }

    public boolean supportMenu() {
        return false;
    }

    /**
     * 如果正在执行精确查询，则更新图标
     *
     * @param search 搜索接口
     */
    protected void updateWaitingIcon(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        if (search.getService().isRunning(ArtifactSearchExtraJob.class, job -> artifact.getGroupId().equals(job.getGroupId()) && artifact.getArtifactId().equals(job.getArtifactId()))) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_WAITING);
        }
    }
}
