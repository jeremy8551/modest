package cn.org.expect.modest.idea.plugin;

import java.util.List;

import cn.org.expect.util.Dates;
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

    public void query(String pattern) {
        MavenFinderResultSet.INSTANCE.query(pattern);
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
        MavenFinderResult result = MavenFinderResultSet.INSTANCE.last();
        if (result == null) {
//            System.out.println("getNames() ");
            return new String[0];
        } else {
            String[] names = result.getNames();
//            System.out.println("getNames() pattern: " + result.getPattern() + ", length: " + names.length + ": " + StringUtils.toString(names));
            new Thread(this.contributor.getRenderer(), JListRenderer.class.getSimpleName() + Dates.currentTimeStamp()).start();
            return names;
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

//        System.out.println("getItemsByName() " + name + ", " + pattern);
        MavenFinderResult result = MavenFinderResultSet.INSTANCE.getResult(pattern);
        if (result == null) {
//            System.out.println("getItemsByName() blank result !" + name + ", pattern: " + pattern);
            return new NavigationItem[0];
        } else {
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
