package cn.org.expect.modest.idea.plugin.ui;

import java.util.List;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import org.jetbrains.annotations.NotNull;

public class SynchronizedSearchListModel extends SearchListModel {

    private SearchListModel model;

    public SynchronizedSearchListModel(SearchListModel model) {
        this.model = model;
    }

    @Override
    public boolean hasMoreElements(SearchEverywhereContributor<?> contributor) {
        return false;
    }

    @Override
    public void setHasMore(SearchEverywhereContributor<?> contributor, boolean contributorHasMore) {

    }

    @Override
    public void addElements(List<? extends SearchEverywhereFoundElementInfo> items) {

    }

    @Override
    public void removeElement(@NotNull Object item, SearchEverywhereContributor<?> contributor) {

    }

    @Override
    public void clearMoreItems() {

    }

    @Override
    public int getIndexToScroll(int currentIndex, boolean scrollDown) {
        return 0;
    }
}
