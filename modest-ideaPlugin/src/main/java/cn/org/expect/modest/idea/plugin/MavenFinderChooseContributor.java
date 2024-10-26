package cn.org.expect.modest.idea.plugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.modest.idea.plugin.db.MavenFinderResult;
import cn.org.expect.modest.idea.plugin.db.MavenSearchStatement;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItemWrapper;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenFinderChooseContributor implements ChooseByNameContributor {

    private Map<String, MavenFinderNavigationItem> map;

    public MavenFinderChooseContributor() {
        this.map = new LinkedHashMap<String, MavenFinderNavigationItem>();
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
        this.map.clear();

        MavenFinderResult result = MavenSearchStatement.INSTANCE.last();
        if (result == null) {
            return new String[0];
        }

        List<MavenArtifact> list = result.getArtifacts();
        for (int i = 0; i < list.size(); i++) {
            MavenArtifact artifact = list.get(i);
            MavenFinderNavigationItem item = new MavenFinderNavigationItem(artifact);
            this.map.put(item.getPresentableText(), item);
        }
        return this.map.keySet().toArray(new String[list.size()]);
    }

    public synchronized NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        MavenFinderNavigationItem item = this.map.get(name);
        if (item == null) {
            return new NavigationItem[0];
        } else {
            return new NavigationItem[]{new MavenFinderNavigationItemWrapper(item)};
        }
//        return new NavigationItem[0];
    }
}
