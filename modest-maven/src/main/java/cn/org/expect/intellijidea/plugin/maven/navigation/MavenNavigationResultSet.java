package cn.org.expect.intellijidea.plugin.maven.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MavenNavigationResultSet {

    private final Map<String, MavenNavigationList> map;

    public MavenNavigationResultSet(List<MavenNavigationList> list) {
        this.map = new LinkedHashMap<>(list.size());
        for (MavenNavigationList navigation : list) {
            MavenFinderNavigationCatalog catalog = navigation.getCatalog();
            String key = catalog.getName();
            map.put(key, navigation);
        }
    }

    public MavenNavigationList getItems(String name) {
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
