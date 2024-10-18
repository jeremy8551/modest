package cn.org.expect.modest.idea.plugin;

import java.util.Comparator;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class SearchEverywhereFoundElementInfoComparator implements Comparator<SearchEverywhereFoundElementInfo> {

    @Override
    public int compare(SearchEverywhereFoundElementInfo o1, SearchEverywhereFoundElementInfo o2) {
        if (o1 == null && o1 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }

        String n1 = o1.getElement().getClass().getName();
        String n2 = o2.getElement().getClass().getName();
        int nv = n1.compareTo(n2);
        if (nv != 0) {
            return nv;
        }

        String c1 = o1.getContributor().getClass().getName();
        String c2 = o2.getContributor().getClass().getName();
        int cv = c1.compareTo(c2);
        if (cv != 0) {
            return cv;
        }

        return o1.getPriority() - o2.getPriority();
    }
}
