package cn.org.expect.maven.intellij.idea.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchNavigationResultSet {

    private final Map<String, SearchNavigation> map;

    public SearchNavigationResultSet(List<SearchNavigation> list) {
        this.map = new LinkedHashMap<>(list.size());
        for (SearchNavigation navigation : list) {
            SearchNavigationHead head = navigation.getHeader();
            String key = head.getName();
            map.put(key, navigation);
        }
    }

    public String[] getNavigationNames() {
        Set<String> keys = this.map.keySet();
        String[] array = new String[keys.size()];
        keys.toArray(array);
        return array;
    }

    public SearchNavigation getNavigation(String name) {
        return this.map.get(name);
    }

    public int size() {
        return this.map.size();
    }
}
