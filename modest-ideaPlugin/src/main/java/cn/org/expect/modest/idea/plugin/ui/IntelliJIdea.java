package cn.org.expect.modest.idea.plugin.ui;

import java.awt.*;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.modest.idea.plugin.MavenFinderContributor;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.util.Dates;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.Advertiser;

public class IntelliJIdea {
    private static final Logger log = Logger.getInstance(IntelliJIdea.class);

    /** IDea 编辑器中选中的文本 */
    public static volatile String EDETOR_SELECT_TEXT;

    /** 查找结果中选中记录的文本 */
    public static volatile MavenFinderNavigationItem JLIST_SELECT_ITEM;

    /** Idea查询对话框对象 */
    private static volatile SearchEverywhereUI UI;

    /** 查询结果中的列表 */
    private static volatile JBList JLIST;

    /** 查询结果中的列表所在的滚动组件 */
    private static volatile JScrollPane SCROLL_PANE;

    /** 滚动组件所在的面板 */
    private static volatile JPanel SUGGESTIONS_PANEL;

    /** 查询结果最下面的广告栏 */
    private static volatile Advertiser ADVERTISER;

    private static volatile SearchListModel MYLIST_MODEL;

    private static volatile ProgressIndicator mySearchProgressIndicator;

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
        IntelliJIdea.UI = ui;

        try {
            IntelliJIdea.JLIST = JavaDialectFactory.get().getField(ui, "myResultsList");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            IntelliJIdea.MYLIST_MODEL = JavaDialectFactory.get().getField(ui, "myListModel");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            IntelliJIdea.mySearchProgressIndicator = JavaDialectFactory.get().getField(ui, "mySearchProgressIndicator");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            JPanel jpanel = JavaDialectFactory.get().getField(ui, "suggestionsPanel");
            IntelliJIdea.SUGGESTIONS_PANEL = jpanel;
            IntelliJIdea.SCROLL_PANE = (JScrollPane) ((BorderLayout) jpanel.getLayout()).getLayoutComponent(jpanel, BorderLayout.CENTER);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        try {
            IntelliJIdea.ADVERTISER = JavaDialectFactory.get().getField(ui, "myHintLabel");
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

    public static boolean notMavenFinderTab() {
        SearchEverywhereUI ui = IntelliJIdea.UI;
        if (ui == null) {
            return false;
        }

        // 如果搜索的标签页不是 MavenFinder，就不显示广告信息
        String selectedTabID = ui.getSelectedTabID();
        return !MavenFinderContributor.TABID.equals(selectedTabID);
    }

    /**
     * 更新 SearchEverywhereUI 最下方广告栏中的信息
     *
     * @param message 文本信息
     * @param icon    图标
     * @return 返回true表示更新成功
     */
    public static boolean updateAdvertiser(String message, Icon icon) {
        if (IntelliJIdea.notMavenFinderTab()) {
            return false;
        }

        Advertiser advertiser = IntelliJIdea.ADVERTISER;
        if (advertiser == null) {
            return false;
        }

        try {
            JLabel myTextPanel = JavaDialectFactory.get().getField(advertiser, "myTextPanel");
            myTextPanel.setText(message);
            myTextPanel.setIcon(icon);
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

    /**
     * 设置 JList 显示的文本 <br>
     * 不能使用 Idea 的渲染线程执行这个方法，需要有单独的线程
     *
     * @param message
     */
    public static void updateJListText(String message) {
        if (IntelliJIdea.notMavenFinderTab()) {
            return;
        }

        JBList jlist = IntelliJIdea.JLIST;
        if (jlist != null) {
            jlist.setEmptyText(message);
        }
    }

    public static void clearJListText() {
        if (IntelliJIdea.notMavenFinderTab()) {
            return;
        }

        JBList jlist = IntelliJIdea.JLIST;
        if (jlist != null) {
            jlist.disableEmptyText();
        }
    }

    public static ProgressIndicator getProgressIndicator() {
        SearchEverywhereUI ui = IntelliJIdea.UI;
        if (ui != null) {
            return JavaDialectFactory.get().getField(ui, "mySearchProgressIndicator");
        }
        return null;
    }

    public static void waitForIdea() {
        ProgressIndicator progress = IntelliJIdea.getProgressIndicator();
        if (progress != null) {
            long startMillis = System.currentTimeMillis();
            while (progress.isRunning() && System.currentTimeMillis() - startMillis >= 2000) {
                Dates.sleep(100);
            }
        }
    }

    public static void clearJList() {
        if (IntelliJIdea.notMavenFinderTab()) {
            return;
        }

        JBList jlist = IntelliJIdea.JLIST;
        if (jlist != null) {
            ListModel model = jlist.getModel();
            if (model instanceof SearchListModel) {
                SearchListModel listModel = (SearchListModel) model;
                System.out.println("clear " + listModel.getSize());
                listModel.clear();
            }
        }
    }

}
