package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContext;
import cn.org.expect.intellij.idea.plugin.maven.SearchDisplay;
import cn.org.expect.intellij.idea.plugin.maven.listener.SearchFieldListener;
import cn.org.expect.intellij.idea.plugin.maven.menu.SearchFieldMenu;
import cn.org.expect.intellij.idea.plugin.maven.menu.SearchResultMenu;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.concurrency.EdtExecutorService;

public class MavenSearchPluginInitJob extends MavenSearchPluginJob {

    public MavenSearchPluginInitJob() {
        super("");
    }

    public int execute() {
        // 如果 manager.getCurrentlyShownUI() 报错，则捕获异常直接退出
        try {
            MavenSearchPlugin plugin = this.getSearch();
            this.setSearchEverywhereUI(plugin); // 加载 UI 组件
            this.setPopupMenuUI(plugin); // 加载弹出菜单
            this.setEditorSelectText(plugin);
        } catch (SearchEverywhereUIShownException es) {
            log.warn("SearchEverywhereUI is Shown!");
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 检测Idea的UI组件
     *
     * @param plugin 事件
     */
    protected void setSearchEverywhereUI(MavenSearchPlugin plugin) throws SearchEverywhereUIShownException {
        MavenSearchPluginContext context = plugin.getContext();
        AnActionEvent event = context.getActionEvent();
        SearchEverywhereUI ui = this.getSearchEverywhereUI(event);
        plugin.getService().setParameter(MavenSearchExecutorService.PARAMETER, JavaDialectFactory.get().getField(ui, "rebuildListAlarm"));
        plugin.getIdeaUI().setSearchEverywhereUI(ui);
        ui.addSearchListener(plugin.getSearchListener());
    }

    public SearchEverywhereUI getSearchEverywhereUI(AnActionEvent event) throws SearchEverywhereUIShownException {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待 SearchEverywhere 显示
            if (System.currentTimeMillis() - startMillis >= 10000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        // 得到当前已显示的 SearchEverywhere 对象
        if (manager.isShown()) {
            return manager.getCurrentlyShownUI(); // TODO 修改注册项后，再打开查询界面，这个位置报错 isShown
        } else {
            throw new SearchEverywhereUIShownException();
        }
    }

    protected void setPopupMenuUI(MavenSearchPlugin plugin) {
        SearchDisplay display = plugin.getIdeaUI().getDisplay();
        display.addMouseListener(new SearchResultMenu(plugin)); // 在搜索结果上添加菜单

        JTextField searchField = plugin.getIdeaUI().getSearchField();
        searchField.addKeyListener(new SearchFieldListener(plugin));
        searchField.addMouseListener(new SearchFieldMenu(plugin));  // 在搜索输入框中添加菜单
    }

    /**
     * 将编辑器中选中的文本，复制到 Tab 页的输入框中
     *
     * @param plugin 搜索接口
     */
    private void setEditorSelectText(MavenSearchPlugin plugin) {
        // 等待 Idea 默认的搜索功能执行完毕
//        plugin.getIdeaUI().waitFor(3000); TODO ？？

        // 在已打开的编辑器中，如果选中了文本，则自动对文本进行查询
        Editor editor = plugin.getContext().getActionEvent().getDataContext().getData(CommonDataKeys.EDITOR);

        // 只能使用 EdtExecutorService.getInstance() 不能递归调用 plugin.execute() 方法
        if (editor != null) {
            EdtExecutorService.getInstance().execute(new MavenSearchEDTJob(() -> {
                String editorSelectText = StringUtils.trimBlank(editor.getSelectionModel().getSelectedText());
                if (StringUtils.isNotBlank(editorSelectText)) {

                    // 编辑器中选中的文本
                    String pattern = plugin.getPattern().parse(editorSelectText);
                    if (log.isDebugEnabled()) {
                        log.debug("{} Idea editor selected text: {} --> {}", getName(), editorSelectText, pattern);
                    }

                    // 复制选中的文本到搜索栏
                    plugin.getIdeaUI().getSearchField().setText(pattern);

                    // 自动切换 Tab 页
                    if (plugin.getSettings().isAutoSwitchTab() && plugin.getSettings().isTabVisible() && plugin.getPattern().isDependency(editorSelectText)) {
                        plugin.getIdeaUI().switchToTab(plugin.getContributor().getSearchProviderId());
                        plugin.asyncSearch();
                    }
                } else {
                    // 如果未选中任何内容，则自动搜索输入框中的文本
                    if (plugin.canSearch()) {
                        plugin.asyncSearch();
                    }
                }
            }, "maven.search.job.select.editor.text.description"));
        }
    }
}
