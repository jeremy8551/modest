package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.util.Ensure;

public class SearchNavigationResultSet {

    private final List<SearchNavigation> list;

    private final Map<String, SearchNavigation> map;

    private final String[] names;

    public SearchNavigationResultSet(List<SearchNavigation> list) {
        this.map = new LinkedHashMap<>();
        this.list = Ensure.notNull(list);
        for (SearchNavigation navigation : this.list) {
            String key = String.valueOf(navigation.getHead().getId());
            this.map.put(key, navigation);
        }
        this.names = this.map.keySet().toArray(new String[this.list.size()]);
    }

    public String[] getNavigationNames() {
        return this.names;
    }

    public SearchNavigation getNavigation(String name) {
        return this.map.get(name);
    }
}
