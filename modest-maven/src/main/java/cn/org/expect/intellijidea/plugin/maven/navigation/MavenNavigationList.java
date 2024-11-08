package cn.org.expect.intellijidea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.util.Ensure;
import com.intellij.navigation.NavigationItem;

public class MavenNavigationList {

    private final MavenFinderNavigationCatalog catalog;

    private final List<MavenFinderNavigationItem> list;

    public MavenNavigationList(MavenFinderNavigationCatalog catalog, List<MavenFinderNavigationItem> list) {
        this.catalog = Ensure.notNull(catalog);
        this.list = Ensure.notNull(list);
    }

    public MavenFinderNavigationCatalog getCatalog() {
        return catalog;
    }

    public List<MavenFinderNavigationItem> getList() {
        return list;
    }

    public NavigationItem[] toArray() {
        NavigationItem[] array = new NavigationItem[list.size() + 1];
        int i = 0;
        array[i++] = this.catalog;
        for (MavenFinderNavigationItem item : this.list) {
            array[i++] = item;
        }
        return array;
    }
}
