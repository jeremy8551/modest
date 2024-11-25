package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import com.intellij.navigation.NavigationItem;

public class SearchNavigation {

    private final SearchNavigationHead head;

    private final List<SearchNavigationItem> items;

    public SearchNavigation(SearchNavigationHead head, List<SearchNavigationItem> items) {
        this.head = head;
        this.items = items;
    }

    public SearchNavigationHead getHead() {
        return this.head;
    }

    public NavigationItem[] getItems() {
        int index = 0;
        NavigationItem[] array = new NavigationItem[this.items.size() + 1];
        array[index++] = this.head;
        for (int i = 0; i < this.items.size(); i++) {
            array[index++] = this.items.get(i);
        }
        return array;
    }
}
