package cn.org.expect.modest.idea.plugin;

import cn.org.expect.modest.idea.plugin.db.MavenSearchExtraThread;
import cn.org.expect.modest.idea.plugin.db.MavenSearchThread;
import cn.org.expect.modest.idea.plugin.ui.IntelliJIdea;
import cn.org.expect.modest.idea.plugin.ui.JListRenderer;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

public class MavenFinderFactory implements SearchEverywhereContributorFactory<Object> {
    private static final Logger log = Logger.getInstance(MavenFinderFactory.class);

    static {
        MavenSearchThread.INSTANCE.start();
        MavenSearchExtraThread.INSTANCE.start();
    }

    public SearchEverywhereContributor<Object> createContributor(AnActionEvent event) {
        MavenFinderContributor contributor = new MavenFinderContributor(event);
        JListRenderer.INSTANCE.setContributor(contributor);

        // 使用选中的文本进行搜索
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
            IntelliJIdea.EDETOR_SELECT_TEXT = selectedText; // 编辑器中选中的文本
            if (StringUtils.isNotBlank(selectedText)) {
                log.warn("--->      Selected text: " + selectedText);
            }
        }

        // 启动线程
        String threadName = "MavenFinderDetectedThread";
        Thread thread = new Thread(() -> {
            log.warn("start " + threadName + " ..");
            IntelliJIdea.detect(event);
            log.warn("MavenFinder Detected end!");
            IntelliJIdea.waitForIdea();

            SearchEverywhereUI ui = IntelliJIdea.get();
            String editorSelectText = IntelliJIdea.EDETOR_SELECT_TEXT;
            if (StringUtils.isNotBlank(editorSelectText)) {
                ui.getSearchField().setText(MavenFinderPattern.parse(editorSelectText)); // 更新搜索内容
//                try {
//                    if (MavenFinderPattern.isXML(editorSelectText)) {
//                        ui.switchToTab(contributor.getSearchProviderId()); // 选择标签页
//                        System.out.println("select Tab: " + ui.getSelectedTabID());
//                    }
//                } catch (Exception e) {
//                    log.error(e.getLocalizedMessage(), e);
//                }
            }
        });
        thread.setName(threadName);
        thread.start();

        return contributor;
    }
}
