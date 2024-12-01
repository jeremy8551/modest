package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.intellij.idea.plugin.maven.SearchDisplay;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.maven.concurrent.MavenSearchDownloadJob;
import cn.org.expect.maven.concurrent.MavenSearchExtraJob;
import cn.org.expect.maven.concurrent.MavenSearchMoreJob;
import cn.org.expect.maven.repository.Artifact;
import cn.org.expect.maven.repository.ArtifactSearchResult;
import cn.org.expect.maven.search.ArtifactSearchAdvertiser;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class MavenSearchRepaintJob extends MavenSearchEDTJob {

    /** 查询结果 */
    protected final ArtifactSearchResult result;

    public MavenSearchRepaintJob(ArtifactSearchResult result) {
        super();
        this.result = result;
        this.setRunnable(this::show);
    }

    /**
     * 需要同步锁保证同时只有一个渲染任务在执行
     */
    protected final void show() {
        synchronized (MavenSearchRepaintJob.class) {
            this.paint();
        }
    }

    protected void paint() {
        MavenSearchPlugin plugin = this.getSearch();
        SearchDisplay display = plugin.getIdeaUI().getDisplay();
        boolean isAllTab = plugin.isAllTab();
        boolean isSelfTab = plugin.isSelfTab();
        Rectangle visibleRect = plugin.getContext().getVisibleRect();
        plugin.getContext().setVisibleRect(null);

        // 如果不是All与自身的Tab，则删除搜索结果中的导航记录
        if (!isAllTab && !isSelfTab) {
            display.clearOtherContributorItem();
            return;
        }

        ArtifactSearchResult result = this.result;
        boolean hasMore = display.hasMore();

        // 备份所有搜索类别的 more 值
        Map<SearchEverywhereContributor<?>, Boolean> backup = isAllTab ? display.getContributorMores() : null;

        // 处理查询结果
        int foundNumber = 0;
        int size = 0;
        List<SearchEverywhereFoundElementInfo> infos = new ArrayList<>();

        // 将搜索结果转为  List<SearchEverywhereFoundElementInfo>
        if (result != null) {
            foundNumber = result.getFoundNumber();
            size = result.size();
            this.process(plugin, result, infos);
        }

        display.clearMore(); // 一定要先删除 more 按钮
        display.merge(infos, isAllTab); // 将导航记录合并到数据模型中
        display.select(plugin.getContext().getSelectNavigationHead(), plugin.getContext().getSelectNavigationItem());

        if (backup != null) {
            display.setContributorMores(backup); // 恢复所有搜索类别的 more 值
        }

        // 设置 more 按钮
        display.setContributorMore(plugin.getContributor(),  //
                !plugin.getService().isRunning(MavenSearchMoreJob.class, t -> true)  // 在 MavenSearchPluginListener 中会重复生成 more 按钮，判断如果正在执行 more 搜索，则不能显示 more 按钮
                        && ( //
                        (hasMore && display.size() > 0 && isAllTab) // ALL标签页，有 more 按钮
                                || (isSelfTab && foundNumber > size) // 记录数 大于 查询结果
                ) //
        );

        display.paint(); // 渲染
        display.setVisibleRange(visibleRect);

        if (log.isDebugEnabled()) {
            log.debug("{} size: {}", this.getName(), display.size());
        }

        // 设置广告信息
        plugin.setStatusBar(ArtifactSearchAdvertiser.NORMAL, ArtifactSearchMessage.get("maven.search.status.text", foundNumber, size));
    }

    /**
     * 将查询结果转为导航记录
     */
    public void process(MavenSearchPlugin plugin, ArtifactSearchResult result, List<SearchEverywhereFoundElementInfo> elementInfoList) {
        int priority = plugin.getSettings().getElementPriority();
        MavenSearchPluginContributor contributor = plugin.getContributor();

        java.util.List<Artifact> list = result.getList();
        for (Artifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            elementInfoList.add(new SearchEverywhereFoundElementInfo(head, priority, contributor));
            List<SearchNavigationItem> items = new ArrayList<>();

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            ArtifactSearchResult itemResult = plugin.getDatabase().select(groupId, artifactId);
            if (itemResult != null && !itemResult.isExpire(plugin.getSettings().getExpireTimeMillis())) {
                head.setIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenSearchPluginIcon.LEFT_UNFOLD);
                    for (Artifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact, plugin.getLocalRepository().getJarfile(itemArtifact));
                        if (plugin.getService().isRunning(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(itemArtifact))) { // 正在下载
                            item.setIcon(MavenSearchPluginIcon.RIGHT_DOWNLOAD);
                        } else if (plugin.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenSearchPluginIcon.RIGHT_LOCAL);
                        }
                        elementInfoList.add(new SearchEverywhereFoundElementInfo(item, priority, contributor));
                        items.add(item);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (plugin.getService().isRunning(MavenSearchExtraJob.class, job -> groupId.equals(job.getGroupId()) && artifactId.equals(job.getArtifactId()))) {
                head.setIcon(MavenSearchPluginIcon.LEFT_WAITING);
            }
        }
    }
}
