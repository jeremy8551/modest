package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

/**
 * 导航记录集合
 */
public class MavenSearchNavigationList {

    /** 总记录数 */
    private final int foundNumber;

    /** true表示还有未读数据，false表示已全部读取 */
    private final boolean hasMore;

    /** 导航记录 */
    private List<SearchEverywhereFoundElementInfo> list;

    public MavenSearchNavigationList(List<SearchEverywhereFoundElementInfo> list, int foundNumber, boolean hasMore) {
        this.foundNumber = foundNumber;
        this.hasMore = hasMore;
        this.list = Ensure.notNull(list);
    }

    public int getFoundNumber() {
        return foundNumber;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public List<SearchEverywhereFoundElementInfo> getInfos() {
        return list;
    }

    public void setInfos(List<SearchEverywhereFoundElementInfo> list) {
        this.list = Ensure.notNull(list);
    }

    public SearchEverywhereFoundElementInfo getInfo(int index) {
        return this.list.get(index);
    }

    public List<MavenSearchNavigation> getNavigationList() {
        List<MavenSearchNavigation> list = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            MavenSearchNavigation navigation = this.getNavigation(i);
            list.add(navigation);
        }
        return list;
    }

    public MavenSearchNavigation getNavigation(int index) {
        return (MavenSearchNavigation) this.list.get(index).getElement();
    }

    public int size() {
        return this.list.size();
    }

    public int find(int from, int depth) {
        for (int i = from; i < this.list.size(); i++) {
            MavenSearchNavigation next = this.getNavigation(i);
            if (next.getDepth() == depth) {
                return i;
            }
        }
        return -1;
    }
}
