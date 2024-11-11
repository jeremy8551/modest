package cn.org.expect.maven.intellij.idea;

import cn.org.expect.log.LogFactory;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        if (Boolean.parseBoolean(System.getProperty("idea.is.internal"))) {
            LogFactory.set("sout+:debug");
        }
        
        MavenPluginContext context = new MavenPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        context.setEditorSelectText(plugin.getEditorSelectText()); // 保存选中的文本
        new MavenPluginInit(plugin).start(); // 启动线程
        return plugin.getContributor();
    }
}
