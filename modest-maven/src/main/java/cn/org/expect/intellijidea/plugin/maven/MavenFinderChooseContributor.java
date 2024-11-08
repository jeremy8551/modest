package cn.org.expect.intellijidea.plugin.maven;

import cn.org.expect.intellijidea.plugin.maven.navigation.MavenFinderBlankItem;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenNavigationList;
import cn.org.expect.intellijidea.plugin.maven.navigation.MavenNavigationResultSet;
import cn.org.expect.util.Ensure;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenFinderChooseContributor implements ChooseByNameContributor {

    private final MavenFinder mavenFinder;

    public MavenFinderChooseContributor(MavenFinder mavenFinder) {
        this.mavenFinder = Ensure.notNull(mavenFinder);
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
        MavenNavigationResultSet resultSet = this.mavenFinder.getContext().getNavigationResultSet();
        if (resultSet != null) {
            return resultSet.getNames();
        } else {
            return new String[]{""};
        }
    }

    public synchronized NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        MavenNavigationResultSet resultSet = this.mavenFinder.getContext().getNavigationResultSet();
        MavenNavigationList list = resultSet.getItems(name);
        if (list != null) {
            return list.toArray();
        } else {
            return new NavigationItem[]{new MavenFinderBlankItem()};
        }
    }
}
