package cn.org.expect.modest.idea.plugin;

import java.util.Comparator;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class NavigationItemComparator implements Comparator<SearchEverywhereFoundElementInfo> {

    @Override
    public int compare(SearchEverywhereFoundElementInfo o1, SearchEverywhereFoundElementInfo o2) {
//        if (o1 == null && o1 == null) {
//            return 0;
//        }
//        if (o1 == null) {
//            return -1;
//        }
//        if (o2 == null) {
//            return 1;
//        }
//
//        Object e1 = o1.getElement();
//        Object e2 = o2.getElement();
//
//        if (e1 instanceof MavenFinderNavigation && e2 instanceof MavenFinderNavigation) {
//            return 0;
//        }
//
//        int nv = e1.getClass().getName().compareTo(e2.getClass().getName());
//        if (nv != 0) {
//            return nv;
//        }

        return 0;
    }
}
