package cn.org.expect.maven.intellij.idea;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        MavenPluginContext context = new MavenPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);

        // 保存编辑器中选中的文本
        context.setEditorSelectText(plugin.getEditorSelectText());

        // 启动线程
        new MavenPluginThread(plugin).start();
        return plugin.getContributor();
    }
}
