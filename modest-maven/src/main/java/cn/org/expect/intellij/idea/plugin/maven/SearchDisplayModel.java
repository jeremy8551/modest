package cn.org.expect.intellij.idea.plugin.maven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenFoundElementInfoComparator;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;

public abstract class SearchDisplayModel {
    protected final static Log log = LogFactory.getLog(SearchDisplayModel.class);

    protected SearchListModel model;

    public SearchDisplayModel(SearchListModel model) {
        this.model = Ensure.notNull(model);
    }

    public int size() {
        return this.model.getSize();
    }

    public boolean isMore(int index) {
        return this.model.isMoreElement(index);
    }

    public boolean hasMore() {
        for (int i = this.model.getSize() - 1; i >= 0; i--) {
            if (this.model.isMoreElement(i)) {
                return true;
            }
        }
        return false;
    }

    public void clearMore() {
        this.model.clearMoreItems();
    }

    public Object getElement(int index) {
        return this.model.getElementAt(index);
    }

    /**
     * 将导航记录添加到数据模型中
     *
     * @param list                     导航记录
     * @param keepOtherContributorItem true表示保留其他搜索类别的导航记录 false表示不保留
     */
    public void merge(List<SearchEverywhereFoundElementInfo> list, boolean keepOtherContributorItem) {
        if (keepOtherContributorItem) {
            List<SearchEverywhereFoundElementInfo> all = new ArrayList<>(this.model.getSize() + list.size());

            // 保存其他搜索类别的记录
            boolean add = true;
            for (int i = 0; i < model.getSize(); i++) {
                SearchEverywhereFoundElementInfo info = model.getRawFoundElementAt(i);
                Object element = info.getElement();
                if (element instanceof MavenSearchNavigation) {
                    if (add) {
                        all.addAll(list); // 将查询结果合并到 all 集合中
                        add = false;
                    }
                } else if (!model.isMoreElement(i)) {
                    all.add(info);
                }
            }
            if (add) {
                all.addAll(list);
            }

            this.merge(all);
        } else {
            this.merge(list);
        }
    }

    public void merge(List<SearchEverywhereFoundElementInfo> list) {
        this.setComparator(this.model, new MavenFoundElementInfoComparator());
        try {
            this.model.clear(); // 清空所有数据
            this.model.addElements(list);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            this.setComparator(this.model, SearchEverywhereFoundElementInfo.COMPARATOR.reversed());
        }
    }

    /**
     * 删除其他搜索类别的导航记录
     */
    public void clearOtherContributorItem() {
        for (int i = this.model.getSize() - 1; i >= 0; i--) {
            SearchEverywhereFoundElementInfo info = this.model.getRawFoundElementAt(i);
            Object element = info.getElement();
            if (element instanceof MavenSearchNavigation) {
                this.model.removeElement(element, info.getContributor());
            }
        }
    }

    public Map<SearchEverywhereContributor<?>, Boolean> getContributorMores() {
        Map<SearchEverywhereContributor<?>, Boolean> result = new LinkedHashMap<>();
        Map<SearchEverywhereContributor<?>, Collection<SearchEverywhereFoundElementInfo>> map = this.model.getFoundElementsMap();
        for (SearchEverywhereContributor<?> contributor : map.keySet()) {
            result.put(contributor, this.model.hasMoreElements(contributor));
        }
        return result;
    }

    public void setContributorMores(Map<SearchEverywhereContributor<?>, Boolean> map) {
        for (Map.Entry<SearchEverywhereContributor<?>, Boolean> entry : map.entrySet()) {
            this.model.setHasMore(entry.getKey(), entry.getValue());
        }
    }

    public void setContributorMore(SearchEverywhereContributor<?> contributor, boolean more) {
        try {
            this.model.setHasMore(contributor, more);
            this.model.freezeElements();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    protected void setComparator(SearchListModel listModel, Comparator comparator) {
        if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
            try {
                JavaDialectFactory.get().setField(listModel, "myElementsComparator", comparator);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
