package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.search.SearchNavigationCollection;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public interface SearchEverywhereNavigationCollection extends SearchNavigationCollection {

    /**
     * 转为Idea的导航记录集合
     *
     * @param plugin 搜索接口
     * @return 导航记录集合
     */
    List<SearchEverywhereFoundElementInfo> toInfos(MavenSearchPlugin plugin);
}
