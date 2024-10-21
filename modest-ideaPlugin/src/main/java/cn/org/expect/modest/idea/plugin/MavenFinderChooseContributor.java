package cn.org.expect.modest.idea.plugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;

public class MavenFinderChooseContributor implements ChooseByNameContributor {

    private Map<String, MavenFinderNavigationItem> map;

    public MavenFinderChooseContributor() {
        this.map = new LinkedHashMap<>();
    }

    /**
     * 先执行一次这个方法，然后再调用 {@linkplain #getItemsByName(String, String, Project, boolean)} 方法
     *
     * @param project                执行导航的项目
     * @param includeNonProjectItems if {@code true}, the names of non-project items (for example,
     *                               library classes) should be included in the returned array.
     * @return
     */
    public synchronized String[] getNames(Project project, boolean includeNonProjectItems) {
        this.map.clear();

        MavenFinderResult result = MavenSearchStatement.INSTANCE.last();
        if (!includeNonProjectItems || result == null) {
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
//            System.out.println("getItemsByName() blank result !" + name + ", pattern: " + pattern);
            return new NavigationItem[0];
        } else {
//            System.out.println("getItemsByName() " + name + ", " + pattern);
            return new MavenFinderNavigationItem[]{item};
        }
    }
}
