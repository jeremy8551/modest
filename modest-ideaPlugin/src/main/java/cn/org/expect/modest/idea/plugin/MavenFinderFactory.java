package cn.org.expect.modest.idea.plugin;

import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

public class MavenFinderFactory implements SearchEverywhereContributorFactory<Object> {
    private static final Logger log = Logger.getInstance(MavenFinderFactory.class);

    public SearchEverywhereContributor<Object> createContributor(AnActionEvent event) {
        MavenFinderContributor contributor = new MavenFinderContributor(event);
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText(); // 编辑器中选中的文本
            if (StringUtils.isNotBlank(selectedText)) {
                log.warn("--->      Selected text: " + selectedText);
                MavenFinderThread.INSTANCE.search(selectedText);

                new Thread(new IdeaSearchTask(event, ui -> {
                    ui.getSearchField().setText(MavenFinderPattern.parse(selectedText)); // 更新搜索内容
                    try {
                        ui.switchToTab(contributor.getSearchProviderId()); // 选择标签页
                        ui.repaint();
                    } catch (Exception ignored) {
                    }
                })).start();
            }
        }

        JListRenderer.INSTANCE.setContributor(contributor);
        return contributor;
    }
}
