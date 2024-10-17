package cn.org.expect.modest.idea.plugin;

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
        MavenFinderResult result = MavenFinderResultSet.INSTANCE.getLast();
        System.out.println("getNames() " + project.getName() + " " + (result == null ? "" : result.getPattern()));
        if (result == null) {
            return new String[0];
        } else {
            String[] names = result.canGetItems().getNames();
            System.out.println("names length: " + names.length);
            return names;
        }
    }

    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        MavenFinderResult result = MavenFinderResultSet.INSTANCE.get(pattern);
        if (result == null) {
            return new NavigationItem[0];
        } else {
            MavenFinderNavigationItem[] array = result.getItems();
            if (array.length > 0) {
                new Thread(this.contributor).start();
            }
            return array;
        }
    }
}
