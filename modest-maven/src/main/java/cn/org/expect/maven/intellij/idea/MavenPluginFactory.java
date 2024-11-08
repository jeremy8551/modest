package cn.org.expect.maven.intellij.idea;

import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenContext;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenContext context = new MavenContext(event);
        MavenSearch mavenFinder = new MavenSearch(context);

        // 保存编辑器中选中的文本
        context.setEditorSelectText(mavenFinder.getEditorSelectText());

        // 启动线程
        new MavenPluginThread(mavenFinder).start();
        return mavenFinder.getContributor();
    }
}
