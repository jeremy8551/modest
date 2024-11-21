package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.util.Collection;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import com.intellij.ide.actions.searcheverywhere.SearchAdapter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import org.jetbrains.annotations.NotNull;

/**
 * 在搜索UI界面中，如果选中的不是当前插件的 Tab，则将状态栏中的文本更换为广告
 */
public class SearchListener extends SearchAdapter {
    private final static Log log = LogFactory.getLog(SearchListener.class);

    private final MavenSearchPlugin plugin;

    public SearchListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void searchStarted(@NotNull String pattern, @NotNull Collection<? extends SearchEverywhereContributor<?>> contributors) {
//        plugin.getContext().setNavigationResultSet(null);
    }

    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        plugin.setStatusbarText(MavenSearchAdvertiser.NORMAL, "");

        // 因为 Idea 自身会根据查询结果进行过滤，所以需要等 Idea 过滤完数据后，重新渲染 JList
        if (plugin.canSearch()) {
            if (log.isDebugEnabled()) {
                log.debug("{}.searchFinished()", SearchListener.class.getSimpleName());
            }
            plugin.showSearchResult();
        }
    }
}
