package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.Dates;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ui.Advertiser;

public class IdeaUI {
    private static final Logger log = Logger.getInstance(IdeaUI.class);

    /** IDea 编辑器中选中的文本 */
    public static volatile String EDETOR_SELECT_TEXT;

    /** 查找结果中选中记录的文本 */
    public static volatile String JLIST_SELECT_TEXT;

    /** Idea查询对话框对象 */
    private static volatile SearchEverywhereUI UI;

    /** 查询结果中的列表 */
    private static volatile JList<?> JLIST;

    /** 查询结果中的列表所在的滚动组件 */
    private static volatile JScrollPane SCROLL_PANE;

    /** 滚动组件所在的面板 */
    private static volatile JPanel SUGGESTIONS_PANEL;

    /** 查询结果最下面的广告栏 */
    private static volatile Advertiser ADVERTISER;

    /**
     * 检测Idea中的组件
     *
     * @param event
     */
    public static void detect(AnActionEvent event) {
        SearchEverywhereManager manager = SearchEverywhereManager.getInstance(event.getProject());
        long startMillis = System.currentTimeMillis();
        while (!manager.isShown()) { // 等待对话框显示
            if (System.currentTimeMillis() - startMillis >= 2000) {
                break;
            } else {
                Dates.sleep(100);
            }
        }

        SearchEverywhereUI ui = manager.getCurrentlyShownUI();
        IdeaUI.UI = ui;

        try {
            IdeaUI.JLIST = JavaDialectFactory.get().getField(ui, "myResultsList");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            JPanel jpanel = JavaDialectFactory.get().getField(ui, "suggestionsPanel");
            IdeaUI.SUGGESTIONS_PANEL = jpanel;
            IdeaUI.SCROLL_PANE = (JScrollPane) ((BorderLayout) jpanel.getLayout()).getLayoutComponent(jpanel, BorderLayout.CENTER);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            IdeaUI.ADVERTISER = JavaDialectFactory.get().getField(ui, "myHintLabel");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static SearchEverywhereUI get() {
        return UI;
    }

    public static JList<?> getJList() {
        return JLIST;
    }

    public static JPanel getSuggestionsPanel() {
        return SUGGESTIONS_PANEL;
    }

    /**
     * 更新 SearchEverywhereUI 最下方广告栏中的信息
     *
     * @param message 文本信息
     * @return 返回true表示更新成功
     */
    public static boolean updateAdvertiser(String message) {
        Advertiser advertiser = IdeaUI.ADVERTISER;
        if (advertiser == null) {
            return false;
        }

        try {
            JLabel myTextPanel = JavaDialectFactory.get().getField(advertiser, "myTextPanel");
            myTextPanel.setText(message);
            myTextPanel.setIcon(null);
            myTextPanel.repaint();

            JLabel myNextLabel = JavaDialectFactory.get().getField(advertiser, "myNextLabel");
            myNextLabel.setText(null);
            myNextLabel.repaint();
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
