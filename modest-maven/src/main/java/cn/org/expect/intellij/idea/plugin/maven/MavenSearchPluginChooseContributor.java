package cn.org.expect.intellij.idea.plugin.maven;

import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationResultSet;
import cn.org.expect.util.Ensure;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenSearchPluginChooseContributor implements ChooseByNameContributor {

    private final MavenSearchPlugin plugin;

    public MavenSearchPluginChooseContributor(MavenSearchPlugin plugin) {
        this.plugin = Ensure.notNull(plugin);
    }

    /**
     * 先执行一次这个方法，然后再调用 {@linkplain #getItemsByName(String, String, Project, boolean)} 方法
     *
     * @param project                执行导航的项目
     * @param includeNonProjectItems if {@code true}, the names of non-project items (for example,
     *                               library classes) should be included in the returned array.
     * @return 在搜索结果 ALL 选项卡中显示的记录
     */
    public synchronized String[] getNames(Project project, boolean includeNonProjectItems) {
        SearchNavigationResultSet resultSet = this.plugin.getContext().getNavigationResultSet();
        if (resultSet != null) {
            return resultSet.getNavigationNames();
        } else {
            return new String[0];
        }
    }

    public synchronized NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        SearchNavigationResultSet resultSet = this.plugin.getContext().getNavigationResultSet();
        SearchNavigation navigation = resultSet.getNavigation(name);
        if (navigation != null) {
            return navigation.getItems();
        } else {
            return new NavigationItem[0];
        }
    }
}
