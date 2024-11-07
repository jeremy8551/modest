package cn.org.expect.intellijidea.plugin.maven;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenFinderFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenFinderContext context = new MavenFinderContext(event);
        MavenFinder mavenFinder = new MavenFinder(context);

        // 保存编辑器中选中的文本
        context.setEditorSelectText(mavenFinder.getEditorSelectText());

        // 启动线程
        new MavenFinderThread(mavenFinder).start();
        return mavenFinder.getContributor();
    }
}
