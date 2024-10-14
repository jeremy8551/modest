package cn.org.expect.modest.idea.plugin;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class MavenFinderChooseContributor implements ChooseByNameContributor {

    private static final Logger log = Logger.getInstance(MavenFinderChooseContributor.class);

    protected final static MavenFinderResultSet resultSet = new MavenFinderResultSet();

    public MavenFinderChooseContributor() {
    }

    public void query(String pattern) {
        resultSet.query(pattern);
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
        System.out.println("getNames() " + project.getName());
        MavenFinderResult result = resultSet.getLast();
        return result == null ? new String[0] : result.canGetItems().getNames();
    }

    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        MavenFinderResult result = resultSet.getLast();
        return result == null ? new NavigationItem[0] : result.getItems();
    }
}
