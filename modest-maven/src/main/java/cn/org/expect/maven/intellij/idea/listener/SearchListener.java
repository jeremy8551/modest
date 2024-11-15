package cn.org.expect.maven.intellij.idea.listener;

import java.util.Map;

import cn.org.expect.maven.intellij.idea.MavenSearchPlugin;
import com.intellij.ide.actions.searcheverywhere.SearchAdapter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.util.ui.Advertiser;
import org.jetbrains.annotations.NotNull;

public class SearchListener extends SearchAdapter {

    private final MavenSearchPlugin plugin;

    public SearchListener(@NotNull MavenSearchPlugin plugin) {
        this.plugin = plugin;
    }

    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
        if (plugin.notMavenSearchTab()) { // 如果不是 Maven+ 的 Tab，则将状态栏中的文本更换为广告
            Advertiser advertiser = plugin.getContext().getAdvertiser();
            if (advertiser != null) {
                advertiser.showRandomText();
            }
        }
    }
}
