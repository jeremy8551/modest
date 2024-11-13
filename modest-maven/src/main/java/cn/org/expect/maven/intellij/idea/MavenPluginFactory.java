package cn.org.expect.maven.intellij.idea;

import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.log.IdeaLogBuilder;
import cn.org.expect.maven.search.MavenSearchMessage;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class MavenPluginFactory implements SearchEverywhereContributorFactory<Object> {

    public @NotNull SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent event) {
        if (Boolean.parseBoolean(System.getProperty("idea.is.internal"))) { // 判断是否是开发测试阶段
            LogFactory.set("sout+:debug");
        } else {
            LogFactory.getContext().setBuilder(new IdeaLogBuilder());
        }

        MavenSearchMessage.setChineseCondition((key) -> "取消".equals(CommonBundle.getCancelButtonText()));
        MavenSearchPluginContext context = new MavenSearchPluginContext(event);
        MavenSearchPlugin plugin = new MavenSearchPlugin(context);
        MavenPluginContributor contributor = plugin.getContributor();
        plugin.updateTabTooltipText(contributor.getSearchProviderId());
        context.setEditorSelectText(plugin.getEditorSelectText()); // 保存选中的文本
        new MavenPluginThread(plugin).start(); // 启动线程
        return contributor;
    }
}
