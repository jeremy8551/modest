package cn.org.expect.modest.idea.plugin.ui;

import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigation;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class MavenFinderFoundElementInfo extends SearchEverywhereFoundElementInfo {

    /** 默认排序权重 */
    public final static int DEFAULT_PRIORITY = 50;

    protected int priority;
    protected Object element;
    protected SearchEverywhereContributor<?> contributor;

    public MavenFinderFoundElementInfo(Object element, SearchEverywhereContributor<?> contributor) {
        super(element, MavenFinderFoundElementInfo.DEFAULT_PRIORITY, contributor);
        this.priority = MavenFinderFoundElementInfo.DEFAULT_PRIORITY;
        this.element = Ensure.notNull(element);
        this.contributor = Ensure.notNull(contributor);
    }

    public int getPriority() {
        return this.priority;
    }

    public Object getElement() {
        return this.element;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setElement(MavenFinderNavigation element) {
        this.element = element;
    }

    public void setContributor(SearchEverywhereContributor<?> contributor) {
        this.contributor = contributor;
    }

    public SearchEverywhereContributor<?> getContributor() {
        return this.contributor;
    }

    public String getDescription() {
        return "contributor: " + (this.contributor != null ? this.contributor.getSearchProviderId() : "null") + "\n" + "weight: " + priority + "\n";
    }
}
