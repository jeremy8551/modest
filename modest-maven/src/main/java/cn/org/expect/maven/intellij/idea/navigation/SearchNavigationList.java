package cn.org.expect.maven.intellij.idea.navigation;

import java.util.List;

import cn.org.expect.util.Ensure;
import com.intellij.navigation.NavigationItem;

public class SearchNavigationList {

    private final SearchNavigation catalog;

    private final List<SearchNavigationItem> list;

    public SearchNavigationList(SearchNavigation catalog, List<SearchNavigationItem> list) {
        this.catalog = Ensure.notNull(catalog);
        this.list = Ensure.notNull(list);
    }

    public SearchNavigation getCatalog() {
        return catalog;
    }

    public List<SearchNavigationItem> getList() {
        return list;
    }

    public NavigationItem[] toArray() {
        NavigationItem[] array = new NavigationItem[list.size() + 1];
        int i = 0;
        array[i++] = this.catalog;
        for (SearchNavigationItem item : this.list) {
            array[i++] = item;
        }
        return array;
    }
}
