package cn.org.expect.maven.intellij.idea.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchNavigationResultSet {

    private final Map<String, SearchNavigationList> map;

    public SearchNavigationResultSet(List<SearchNavigationList> list) {
        this.map = new LinkedHashMap<>(list.size());
        for (SearchNavigationList navigation : list) {
            SearchNavigation catalog = navigation.getCatalog();
            String key = catalog.getName();
            map.put(key, navigation);
        }
    }

    public SearchNavigationList getItems(String name) {
        return this.map.get(name);
    }

    public String[] getNames() {
        Set<String> keys = this.map.keySet();
        String[] array = new String[keys.size()];
        keys.toArray(array);
        return array;
    }

    public int size() {
        return this.map.size();
    }
}
