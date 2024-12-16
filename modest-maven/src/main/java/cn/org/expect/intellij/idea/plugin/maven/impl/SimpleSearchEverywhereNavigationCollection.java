package cn.org.expect.intellij.idea.plugin.maven.impl;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchEverywhereNavigationCollection;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.maven.search.ArtifactSearchAware;
import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

/**
 * 导航记录集合
 */
public class SimpleSearchEverywhereNavigationCollection implements SearchEverywhereNavigationCollection {

    /** 总记录数 */
    private final int foundNumber;

    /** true表示还有未读数据，false表示已全部读取 */
    private final boolean hasMore;

    /** 导航记录集合 */
    private final List<SearchNavigation> list;

    public SimpleSearchEverywhereNavigationCollection(List<SearchNavigation> list, int foundNumber, boolean hasMore) {
        this.list = Ensure.notNull(list);
        this.foundNumber = foundNumber;
        this.hasMore = hasMore;
    }

    public int getFoundNumber() {
        return foundNumber;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int size() {
        return this.list.size();
    }

    public SearchNavigation get(int index) {
        return this.list.get(index);
    }

    public List<SearchEverywhereFoundElementInfo> toInfos(MavenSearchPlugin plugin) {
        // 展开/折叠导航记录
        for (int i = 0; i < this.list.size(); i++) {
            SearchNavigation navigation = this.list.get(i);
            if (navigation.getDepth() == 1) {
                if (navigation.supportFold()) {
                    if (navigation.isFold()) {
                        navigation.fold(); // 折叠
                    } else {
                        navigation.unfold(); // 展开
                    }
                }
            }
        }

        List<SearchEverywhereFoundElementInfo> infos = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            SearchNavigation navigation = this.list.get(i);
            this.addInfo(navigation, infos, plugin.getSettings().getNavigationPriority(), plugin.getContributor());
        }
        return infos;
    }

    protected void addInfo(SearchNavigation navigation, List<SearchEverywhereFoundElementInfo> infos, int navigationPriority, SearchEverywhereContributor<?> contributor) {
        infos.add(new SearchEverywhereFoundElementInfo(navigation, navigationPriority, contributor));
        List<? extends SearchNavigation> navigationList = navigation.getNavigationList();
        for (SearchNavigation child : navigationList) {
            this.addInfo(child, infos, navigationPriority, contributor);
        }
    }

    public void setSearch(ArtifactSearch search) {
        SearchEverywhereNavigationCollection navigationList = this;
        for (int i = 0; i < navigationList.size(); i++) {
            SearchNavigation navigation = navigationList.get(i);
            if (navigation instanceof ArtifactSearchAware) {
                ((ArtifactSearchAware) navigation).setSearch(search);
            }
            this.setSearch(search, navigation);
        }
    }

    protected void setSearch(ArtifactSearch search, SearchNavigation navigation) {
        List<? extends SearchNavigation> childList = navigation.getNavigationList();
        for (SearchNavigation child : childList) {
            if (child instanceof ArtifactSearchAware) {
                ((ArtifactSearchAware) child).setSearch(search);
            }
            this.setSearch(search, child);
        }
    }
}
