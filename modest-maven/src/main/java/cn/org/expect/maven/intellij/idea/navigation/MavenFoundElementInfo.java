package cn.org.expect.maven.intellij.idea.navigation;

import cn.org.expect.jdk.JavaDialectFactory;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;

public class MavenFoundElementInfo extends SearchEverywhereFoundElementInfo {

    /** 默认排序权重 */
    public final static int DEFAULT_PRIORITY = 50;

    public int priority;
    public MavenSearchNavigation element;
    public SearchEverywhereContributor<?> contributor;

    public MavenFoundElementInfo(MavenSearchNavigation element, SearchEverywhereContributor<?> contributor) {
        super(element, MavenFoundElementInfo.DEFAULT_PRIORITY, contributor);
        this.priority = super.getPriority();
        this.element = (MavenSearchNavigation) super.getElement();
        this.contributor = contributor;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        SearchEverywhereFoundElementInfo elementInfo = (SearchEverywhereFoundElementInfo) this;
        JavaDialectFactory.get().setField(elementInfo, "priority", priority);
    }

    public void setElement(MavenSearchNavigation element) {
        this.element = element;
        SearchEverywhereFoundElementInfo elementInfo = (SearchEverywhereFoundElementInfo) this;
        JavaDialectFactory.get().setField(elementInfo, "element", element);
    }

    public void setContributor(SearchEverywhereContributor<?> contributor) {
        this.contributor = contributor;
        SearchEverywhereFoundElementInfo elementInfo = (SearchEverywhereFoundElementInfo) this;
        JavaDialectFactory.get().setField(elementInfo, "contributor", contributor);
    }

    public int getPriority() {
        return this.priority;
    }

    public MavenSearchNavigation getElement() {
        return this.element;
    }

    public SearchEverywhereContributor<?> getContributor() {
        return this.contributor;
    }

    public String getDescription() {
        return "contributor: " + (this.contributor != null ? this.contributor.getSearchProviderId() : "null") + "\n" + "weight: " + priority + "\n";
    }
}
