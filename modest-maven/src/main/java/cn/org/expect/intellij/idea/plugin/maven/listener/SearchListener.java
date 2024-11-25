package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchAdapter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SearchListener extends SearchAdapter {
    private final static Log log = LogFactory.getLog(SearchListener.class);

    private final MavenSearchPlugin plugin;

    /** 记录上一次使用的 tab 标签页 */
    private String lastSelectTabID;

    /** 线程任务队列 */
    private final Queue<Runnable> queue;

    public SearchListener(MavenSearchPlugin plugin, Queue<Runnable> queue) {
        this.plugin = Ensure.notNull(plugin);
        this.queue = Ensure.notNull(queue);
        this.lastSelectTabID = "";
    }

    public void searchStarted(@NotNull String pattern, @NotNull Collection<? extends SearchEverywhereContributor<?>> contributors) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchStarted()", SearchListener.class.getSimpleName());
        }

        boolean canSearch = this.plugin.canSearch();
        if (canSearch) {
            Runnable command = this.queue.poll(); // 取一个任务
            if (command != null) {
                if (log.isDebugEnabled()) {
                    log.debug("{}.run {} ", SearchListener.class.getSimpleName(), command);
                }
                command.run();
                return;
            }
        }

        // 切换选项卡时，不会自动执行搜索。如果连续二次执行搜索是不是在不同的选项卡，则需要重新执行搜索
        String tabID = this.plugin.getIdeaUI().getSelectedTabID();
        if (!tabID.equals(this.lastSelectTabID)) {
            this.lastSelectTabID = tabID;

            if (canSearch) {
                if (log.isDebugEnabled()) {
                    log.debug("{}.async ", SearchListener.class.getSimpleName());
                }
                this.plugin.asyncSearch();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("{}.show {}", SearchListener.class.getSimpleName(), tabID);
                }
                this.plugin.showSearchResult(null); // 切换到其他选项卡，需要删除搜索结果
            }
        }
    }

    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchFinished()", SearchListener.class.getSimpleName());
        }

        // 在搜索UI界面中，如果选中的不是当前插件的 Tab，则将状态栏中的文本更换为广告
        plugin.setStatusbarText(MavenSearchAdvertiser.NORMAL, "");
    }
}
