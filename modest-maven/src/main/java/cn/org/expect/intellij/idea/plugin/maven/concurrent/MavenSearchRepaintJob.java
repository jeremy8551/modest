package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.SearchDisplay;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigationList;
import cn.org.expect.maven.concurrent.ArtifactSearchMoreJob;
import cn.org.expect.maven.search.ArtifactSearchStatusMessageType;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class MavenSearchRepaintJob extends MavenSearchPluginJob implements EDTJob {

    /** 锁 */
    protected final static Object lock = new Object();

    /** 查询结果 */
    protected final MavenSearchNavigationList result;

    public MavenSearchRepaintJob(MavenSearchNavigationList result) {
        super("maven.search.job.display.search.result.description");
        this.result = result;
    }

    /**
     * 需要同步锁保证同时只有一个渲染任务在执行
     *
     * @return 返回值
     */
    public int execute() {
        synchronized (lock) {
            this.paint();
        }
        return 0;
    }

    protected void paint() {
        MavenSearchPlugin plugin = this.getSearch();
        SearchDisplay display = plugin.getIdeaUI().getDisplay();
        boolean isAllTab = plugin.isAllTab();
        boolean isSelfTab = plugin.isSelfTab();

        // 如果不是All与自身的Tab，则删除搜索结果中的导航记录
        if (!isAllTab && !isSelfTab) {
            display.clearSelfNavigation();
            display.paint();
            return;
        }

        Rectangle visibleRect = plugin.getContext().getVisibleRect();
        plugin.getContext().setVisibleRect(null);
        boolean hasMore = display.hasMore();

        // 备份所有搜索类别的 more 值
        Map<SearchEverywhereContributor<?>, Boolean> backup = isAllTab ? display.getContributorMores() : null;

        // 生成导航记录
        MavenSearchNavigationList result = this.result;
        boolean hasResult = result != null;
        List<SearchEverywhereFoundElementInfo> infos = hasResult ? result.toInfos(plugin) : new ArrayList<>(0);

        if (log.isTraceEnabled()) {
            log.debug("---->");
            for (SearchEverywhereFoundElementInfo info : infos) {
                MavenSearchNavigation navigation = (MavenSearchNavigation) info.getElement();
                log.debug(navigation.getArtifact().toMavenId() + ", " + navigation.isFold());
            }
        }

        display.clearMore(); // 一定要先删除 more 按钮
        display.merge(infos, isAllTab); // 将导航记录合并到数据模型中
        display.select(plugin.getContext().getSelectNavigation());

        if (backup != null) {
            display.setContributorMores(backup); // 恢复所有搜索类别的 more 值
        }

        // 设置 more 按钮
        display.setContributorMore(plugin.getContributor(),  //
                !plugin.getService().isRunning(ArtifactSearchMoreJob.class)  // 在 MavenSearchPluginListener 中会重复生成 more 按钮，判断如果正在执行 more 搜索，则不能显示 more 按钮
                        && ( //
                        (hasMore && display.size() > 0 && isAllTab) // ALL标签页，有 more 按钮
                                || (isSelfTab && result != null && result.isHasMore()) //
                ) //
        );

        display.paint(); // 渲染
        display.setVisibleRange(visibleRect);

        if (log.isDebugEnabled()) {
            log.debug("{} size: {}", this.getName(), display.size());
        }

        // 设置广告信息
        if (hasResult) {
            plugin.setStatusBar(ArtifactSearchStatusMessageType.NORMAL, "maven.search.status.text", result.getFoundNumber(), result.size());
        } else {
            plugin.setStatusBar(ArtifactSearchStatusMessageType.NORMAL, "maven.search.status.text", 0, 0);
        }
    }
}
