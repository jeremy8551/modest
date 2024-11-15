package cn.org.expect.maven.intellij.idea.navigation;

import java.util.Comparator;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class MavenFoundElementInfoComparator implements Comparator<SearchEverywhereFoundElementInfo> {

    public int compare(SearchEverywhereFoundElementInfo o1, SearchEverywhereFoundElementInfo o2) {
        // 空白导航记录移动到最后
        if (o1.getElement() instanceof EmptySearchNavigation) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }
}
