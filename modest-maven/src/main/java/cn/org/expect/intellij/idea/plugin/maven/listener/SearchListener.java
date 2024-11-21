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
 *
 */
public class SearchListener extends SearchAdapter {
    private final static Log log = LogFactory.getLog(SearchListener.class);

    private final MavenSearchPlugin plugin;

    /** 记录上一次使用的 tab 标签页 */
    private String lastSelectTabID;

    public SearchListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
        this.lastSelectTabID = "";
    }

    public void searchStarted(@NotNull String pattern, @NotNull Collection<? extends SearchEverywhereContributor<?>> contributors) {
        if (log.isDebugEnabled()) {
            log.debug("{}.searchStarted()", SearchListener.class.getSimpleName());
        }

        String currentTabID = plugin.getIdeaUI().getSelectedTabID();
        if (!currentTabID.equals(this.lastSelectTabID)) {
            this.lastSelectTabID = currentTabID;

            if (plugin.canSearch()) {
                plugin.asyncSearch();
            } else {
                plugin.showSearchResult(null);
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
