package cn.org.expect.modest.idea.plugin;

import java.util.List;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class MavenFinderChooseContributor implements ChooseByNameContributor {

    private static final Logger log = Logger.getInstance(MavenFinderChooseContributor.class);

    private MavenFinderContributor contributor;

    public MavenFinderChooseContributor(MavenFinderContributor contributor) {
        this.contributor = contributor;
    }

    /**
     * 先执行一次这个方法，然后再调用 {@linkplain #getItemsByName(String, String, Project, boolean)} 方法
     *
     * @param project                the project in which the navigation is performed.
     * @param includeNonProjectItems if {@code true}, the names of non-project items (for example,
     *                               library classes) should be included in the returned array.
     * @return
     */
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        MavenFinderResult result = MavenSearchStatement.INSTANCE.last();
        if (result == null) {
            return new String[0];
        } else {
            return result.getNames();
        }
    }

    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
//        System.out.println("getItemsByName() " + name + ", " + pattern);
//        MavenFinderNavigationItem[] items = new MavenFinderNavigationItem[10];
//        MavenFinderNavigationItem[] array = new MavenFinderNavigationItem[items.length];
//        for (int i = 0; i < array.length; i++) {
//            MavenFinderItemBlank item = new MavenFinderItemBlank();
//            items[i] = new MavenFinderNavigationItem(item);
//        }
//        return items;

        MavenFinderResult result = MavenSearchStatement.INSTANCE.getResult(pattern);
        if (result == null) {
//            System.out.println("getItemsByName() blank result !" + name + ", pattern: " + pattern);
            return new NavigationItem[0];
        } else {
//            System.out.println("getItemsByName() " + name + ", " + pattern);
            List<MavenFinderItem> list = result.getItems();
            for (int i = 0, size = list.size(); i < size; i++) {
                MavenFinderItem item = list.get(i);
                if (item.getPresentableText().equals(name)) {
                    return new MavenFinderNavigationItem[]{new MavenFinderNavigationItem(item)};
                }
            }
            throw new UnsupportedOperationException(name + ", " + pattern);
        }
    }
}
