package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.util.Ensure;
import com.intellij.navigation.NavigationItem;

public class SearchNavigation {

    private final SearchNavigationHead navigation;

    private final List<SearchNavigationItem> items;

    public SearchNavigation(SearchNavigationHead navigation, List<SearchNavigationItem> list) {
        this.navigation = Ensure.notNull(navigation);
        this.items = Ensure.notNull(list);
    }

    public SearchNavigationHead getHeader() {
        return this.navigation;
    }

    public NavigationItem[] getItems() {
        NavigationItem[] array = new NavigationItem[items.size() + 1];
        int i = 0;
        array[i++] = this.navigation;
        for (SearchNavigationItem item : this.items) {
            array[i++] = item;
        }
        return array;
    }
}
