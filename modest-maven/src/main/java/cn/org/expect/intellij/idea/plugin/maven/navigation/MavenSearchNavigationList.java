package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.maven.search.ArtifactSearch;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

/**
 * 导航记录集合 TODO 转为接口
 */
public class MavenSearchNavigationList {

    /** 总记录数 */
    private final int foundNumber;

    /** true表示还有未读数据，false表示已全部读取 */
    private final boolean hasMore;

    /** 导航记录 */
    private final List<MavenSearchNavigation> list;

    public MavenSearchNavigationList(List<MavenSearchNavigation> list, int foundNumber, boolean hasMore) {
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

    public MavenSearchNavigation get(int index) {
        return this.list.get(index);
    }

    public List<SearchEverywhereFoundElementInfo> toInfos(MavenSearchPlugin plugin) {
        // 展开/折叠导航记录
        for (int i = 0; i < this.list.size(); i++) {
            MavenSearchNavigation navigation = this.list.get(i);
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
            MavenSearchNavigation navigation = this.list.get(i);
            this.addInfo(navigation, infos, plugin.getSettings().getNavigationPriority(), plugin.getContributor());
        }
        return infos;
    }

    protected void addInfo(MavenSearchNavigation navigation, List<SearchEverywhereFoundElementInfo> infos, int navigationPriority, SearchEverywhereContributor<?> contributor) {
        infos.add(new SearchEverywhereFoundElementInfo(navigation, navigationPriority, contributor));
        List<? extends MavenSearchNavigation> navigationList = navigation.getNavigationList();
        for (MavenSearchNavigation child : navigationList) {
            this.addInfo(child, infos, navigationPriority, contributor);
        }
    }

    public void updateSearch(ArtifactSearch search) {
        MavenSearchNavigationList navigationList = this;
        for (int i = 0; i < navigationList.size(); i++) {
            MavenSearchNavigation navigation = navigationList.get(i);
            navigation.setSearch(search);
            this.loopUpdateSearch(search, navigation);
        }
    }

    protected void loopUpdateSearch(ArtifactSearch search, MavenSearchNavigation navigation) {
        List<? extends MavenSearchNavigation> childList = navigation.getNavigationList();
        for (MavenSearchNavigation child : childList) {
            child.setSearch(search);
            this.loopUpdateSearch(search, child);
        }
    }
}
