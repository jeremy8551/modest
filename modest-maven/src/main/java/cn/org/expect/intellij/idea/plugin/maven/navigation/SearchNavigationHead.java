package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.MavenIcon;
import cn.org.expect.maven.concurrent.ArtifactSearchExtraJob;
import cn.org.expect.maven.repository.ArtifactSearchResult;

public class SearchNavigationHead extends AbstractSearchNavigation {

    /** 子节点 */
    private final List<SearchNavigationItem> child;

    public SearchNavigationHead(Artifact artifact) {
        super(artifact);
        this.child = new ArrayList<>();
        this.setDepth(1);
        this.setPresentableText(artifact.getArtifactId());
        this.setLocationString(" " + artifact.getGroupId());
        this.setLeftIcon(MavenIcon.LEFT_FOLD);
        this.setRightIcon(MavenIcon.RIGHT);
        this.setRightText(artifact.getType() + " ");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return this.child;
    }

    public boolean supportFold(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null && !result.isExpire(search.getSettings().getExpireTimeMillis())) { // 如果导航记录有子节点，则更新图标
            this.setLeftIcon(MavenIcon.LEFT_HAS_QUERY);
        }
        return true;
    }

    public void setUnfold(MavenSearch search) {
        this.setFold(false); // 设置为：展开
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result == null || result.isExpire(search.getSettings().getExpireTimeMillis())) {
            this.setLeftIcon(MavenIcon.LEFT_WAITING); // 更改为：等待图标
            search.asyncSearch(artifact.getGroupId(), artifact.getArtifactId()); // 后台搜索
        } else {
            search.display();
        }
    }

    public void setFold(MavenSearch search) {
        this.setFold(true); // 设置为：折叠
        search.display();
    }

    public void unfold(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = search.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null) {
            this.setLeftIcon(MavenIcon.LEFT_UNFOLD);

            if (this.child.isEmpty()) {
                for (Artifact itemArtifact : result.getList()) {
                    this.child.add(new SearchNavigationItem(itemArtifact));
                }
            }

            for (SearchNavigationItem item : this.child) {
                if (item.isFold()) {
                    item.fold(search);
                } else {
                    item.unfold(search);
                }
                item.update(search);
            }
        }

        this.updateWaitingIcon(search);
    }

    public void fold(MavenSearch search) {
        this.updateWaitingIcon(search);
        this.child.clear();
    }

    public boolean supportMenu() {
        return false;
    }

    public void displayMenu(MavenSearchPlugin plugin, MavenSearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
    }

    /**
     * 如果正在执行精确查询，则更新图标
     *
     * @param search 搜索接口
     */
    protected void updateWaitingIcon(MavenSearch search) {
        Artifact artifact = this.getArtifact();
        if (search.getService().isRunning(ArtifactSearchExtraJob.class, job -> artifact.getGroupId().equals(job.getGroupId()) && artifact.getArtifactId().equals(job.getArtifactId()))) {
            this.setLeftIcon(MavenIcon.LEFT_WAITING);
        }
    }
}
