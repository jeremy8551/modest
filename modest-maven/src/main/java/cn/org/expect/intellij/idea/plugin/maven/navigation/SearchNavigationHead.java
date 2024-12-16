package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.concurrent.ArtifactSearchExtraJob;
import cn.org.expect.maven.repository.ArtifactSearchResult;

public class SearchNavigationHead extends AbstractSearchNavigation {

    /** 子节点 */
    private final List<SearchNavigationItem> child;

    public SearchNavigationHead(MavenSearchPlugin plugin, Artifact artifact) {
        super(plugin, artifact);
        this.child = new ArrayList<>();
        this.setDepth(1);
        this.setPresentableText(artifact.getArtifactId());
        this.setLocationString(" " + artifact.getGroupId());
        this.setLeftIcon(MavenSearchPluginIcon.LEFT_FOLD);
        this.setRightIcon(MavenSearchPluginIcon.RIGHT);
        this.setRightText(artifact.getType() + " ");
    }

    public List<? extends MavenSearchNavigation> getNavigationList() {
        return this.child;
    }

    public boolean supportFold() {
        MavenSearchPlugin plugin = this.getPlugin();
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = plugin.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null && !result.isExpire(plugin.getSettings().getExpireTimeMillis())) { // 如果导航记录有子节点，则更新图标
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
        }
        return true;
    }

    public void setUnfold() {
        this.setFold(false); // 设置为：展开
        MavenSearchPlugin plugin = this.getPlugin();
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = plugin.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result == null || result.isExpire(plugin.getSettings().getExpireTimeMillis())) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_WAITING); // 更改为：等待图标
            plugin.asyncSearch(artifact.getGroupId(), artifact.getArtifactId()); // 后台搜索
        }
    }

    public void setFold() {
        this.setFold(true); // 设置为：折叠
    }

    public void unfold() {
        MavenSearchPlugin plugin = this.getPlugin();
        Artifact artifact = this.getArtifact();
        ArtifactSearchResult result = plugin.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
        if (result != null) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_UNFOLD);

            if (this.child.isEmpty()) {
                for (Artifact itemArtifact : result.getList()) {
                    this.child.add(new SearchNavigationItem(plugin, itemArtifact));
                }
            }

            for (SearchNavigationItem item : this.child) {
                if (item.isFold()) {
                    item.fold();
                } else {
                    item.unfold();
                }
                item.update();
            }
        }

        this.updateWaitingIcon();
    }

    public void fold() {
        this.updateWaitingIcon();
        this.child.clear();
    }

    public boolean supportMenu() {
        return false;
    }

    public void displayMenu(MavenSearchNavigation navigation, JPopupMenu topMenu, int selectedIndex) {
    }

    /**
     * 如果正在执行精确查询，则更新图标
     */
    protected void updateWaitingIcon() {
        Artifact artifact = this.getArtifact();
        if (this.getPlugin().getService().isRunning(ArtifactSearchExtraJob.class, job -> artifact.getGroupId().equals(job.getGroupId()) && artifact.getArtifactId().equals(job.getArtifactId()))) {
            this.setLeftIcon(MavenSearchPluginIcon.LEFT_WAITING);
        }
    }
}
