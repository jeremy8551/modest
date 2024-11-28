package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.intellij.idea.plugin.maven.action.MavenSearchPluginPinAction;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchAdapter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MavenSearchPluginListener extends SearchAdapter {
    private final static Log log = LogFactory.getLog(MavenSearchPluginListener.class);

    private final MavenSearchPlugin plugin;

    public MavenSearchPluginListener(MavenSearchPlugin plugin) {
        super();
        this.plugin = Ensure.notNull(plugin);
    }

    public String getName() {
        return MavenSearchPluginListener.class.getSimpleName();
    }

    public synchronized void searchStarted(@NotNull String pattern, @NotNull Collection<? extends SearchEverywhereContributor<?>> contributors) {
        if (StringUtils.isBlank(pattern) && !this.plugin.getContributor().isEmptyPatternSupported()) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("{}.searchStarted({})", this.getName(), pattern);
        }

        // 扩展 pin 窗口大小
        MavenSearchPluginPinAction.PIN.extend();

        if (this.plugin.canSearch()) {
            if (log.isDebugEnabled()) {
                log.debug("{}.async", this.getName());
            }

            MavenRepositoryDatabase database = this.plugin.getDatabase();
            MavenSearchResult result = database.select(pattern);
            if (result != null) {
                this.plugin.display(result);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{}.show", this.getName());
            }
            this.plugin.display(); // 切换到其他选项卡，需要删除搜索结果
        }
    }

    /**
     * 搜索结束前，执行第一步
     *
     * @param contributor
     * @param hasMore
     */
    public void contributorFinished(@NotNull SearchEverywhereContributor<?> contributor, boolean hasMore) {
        if (log.isDebugEnabled()) {
            log.debug("{}.contributorFinished({}, {})", this.getName(), contributor, hasMore);
        }
    }

    /**
     * 搜索结束前，执行第二步
     *
     * @param hasMoreContributors
     */
    public synchronized void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        Boolean value = null;
        for (Map.Entry<SearchEverywhereContributor<?>, Boolean> entry : hasMoreContributors.entrySet()) {
            if (entry.getKey() instanceof MavenSearchPluginContributor) {
                value = entry.getValue();
                break;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("{}.searchFinished() {}", this.getName(), value);
        }

        // 在搜索UI界面中，如果选中的不是当前插件的 Tab，则将状态栏中的文本更换为广告
        if (!this.plugin.isSelfTab()) {
            this.plugin.setStatusBar(null, "");
        }
    }

    /**
     * 搜索结束前，执行第三步
     *
     * @param items
     */
    public void searchFinished(@NotNull List<Object> items) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchFinished({})", this.getName(), items.size());
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------

    public void contributorWaits(@NotNull SearchEverywhereContributor<?> contributor) {
        if (log.isDebugEnabled()) {
            log.debug("{}.contributorWaits({})", this.getName(), contributor);
        }
    }

    public void elementsAdded(@NotNull List<? extends SearchEverywhereFoundElementInfo> list) {
        if (log.isDebugEnabled()) {
            log.debug("{}.elementsAdded({})", this.getName(), list.size());
        }
    }

    public void elementsRemoved(@NotNull List<? extends SearchEverywhereFoundElementInfo> list) {
        if (log.isDebugEnabled()) {
            log.debug("{}.elementsRemoved({})", this.getName(), list.size());
        }
    }
}
