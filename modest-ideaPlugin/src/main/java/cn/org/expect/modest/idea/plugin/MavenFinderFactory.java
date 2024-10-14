package cn.org.expect.modest.idea.plugin;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MavenFinderFactory implements SearchEverywhereContributorFactory {

    public SearchEverywhereContributor createContributor(AnActionEvent initEvent) {
        return new MavenFinderContributor(initEvent);
    }
}
