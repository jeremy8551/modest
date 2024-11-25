package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
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
public class MavenSearchPluginListener extends SearchAdapter {
    private final static Log log = LogFactory.getLog(MavenSearchPluginListener.class);

    private final MavenSearchPlugin plugin;

    /** 线程任务队列 */
    private final Queue<Runnable> queue;

    public MavenSearchPluginListener(MavenSearchPlugin plugin, Queue<Runnable> queue) {
        this.plugin = Ensure.notNull(plugin);
        this.queue = Ensure.notNull(queue);
    }

    public void searchStarted(@NotNull String pattern, @NotNull Collection<? extends SearchEverywhereContributor<?>> contributors) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchStarted({})", MavenSearchPluginListener.class.getSimpleName(), pattern);
        }

        // 扩展 pin 窗口大小
        MavenSearchPluginPinAction.PIN.extend();

        if (this.plugin.canSearch()) {
            Runnable command = this.queue.poll(); // 取一个任务
            if (command != null) {
                if (log.isDebugEnabled()) {
                    log.debug("{}.run {} ", MavenSearchPluginListener.class.getSimpleName(), command);
                }
                command.run();
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("{}.async ", MavenSearchPluginListener.class.getSimpleName());
            }
            this.plugin.asyncSearch();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{}.show", MavenSearchPluginListener.class.getSimpleName());
            }
            this.plugin.showSearchResult(null); // 切换到其他选项卡，需要删除搜索结果
        }
    }

    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchFinished()", MavenSearchPluginListener.class.getSimpleName());
        }

        // 在搜索UI界面中，如果选中的不是当前插件的 Tab，则将状态栏中的文本更换为广告
        plugin.setStatusbarText(MavenSearchAdvertiser.NORMAL, "");
    }
}
