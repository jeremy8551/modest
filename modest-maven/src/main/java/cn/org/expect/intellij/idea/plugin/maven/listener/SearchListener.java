package cn.org.expect.intellij.idea.plugin.maven.listener;

import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import com.intellij.ide.actions.searcheverywhere.SearchAdapter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.util.ui.Advertiser;
import org.jetbrains.annotations.NotNull;

/**
 * 在搜索UI界面中，如果选中的不是当前插件的 Tab，则将状态栏中的文本更换为广告
 */
public class SearchListener extends SearchAdapter {

    private final MavenSearchPlugin plugin;

    public SearchListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        if (plugin.notMavenSearchTab()) {
            Advertiser advertiser = plugin.getContext().getAdvertiser();
            if (advertiser != null) {
                advertiser.showRandomText();
            }
        }
    }
}
