package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import java.lang.reflect.Field;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ui.Advertiser;

public class EveryWhereSearch {
    private static final Logger log = Logger.getInstance(EveryWhereSearch.class);

    private static volatile SearchEverywhereUI UI;

    private static volatile JList<?> JLIST;

    private static volatile JPanel SUGGESTIONS_PANEL;

    private static volatile JScrollPane SCROLL_PANE;

    public static volatile Advertiser MYHINTLABEL;

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
        EveryWhereSearch.UI = Ensure.notNull(ui);
        Class<?> jlistClass = ui.getClass().getSuperclass();
        if (jlistClass == null) {
            jlistClass = ui.getClass();
        }

        try {
            EveryWhereSearch.JLIST = JavaDialectFactory.get().getField(ui, "myResultsList");
        } catch (Exception e) {
            printError(e, jlistClass);
        }

        try {
            JPanel jpanel = JavaDialectFactory.get().getField(ui, "suggestionsPanel");
            BorderLayout layout = (BorderLayout) jpanel.getLayout();
            JScrollPane scrollPane = (JScrollPane) layout.getLayoutComponent(jpanel, BorderLayout.CENTER);

            EveryWhereSearch.SUGGESTIONS_PANEL = jpanel;
            EveryWhereSearch.SCROLL_PANE = scrollPane;
        } catch (Exception e) {
            printError(e, jlistClass);
        }

        try {
            Advertiser myHintLabel = JavaDialectFactory.get().getField(ui, "myHintLabel");
            EveryWhereSearch.MYHINTLABEL = myHintLabel;
        } catch (Exception e) {
            printError(e, jlistClass);
        }
    }

    private static void printError(Exception e, Class<?> jlistClass) {
        Field[] fields = jlistClass.getDeclaredFields();
        for (Field field : fields) {
            System.err.println(field.getName() + "   " + field.getType().getSimpleName());
        }
        e.printStackTrace();
    }

    public static SearchEverywhereUI getUI() {
        return UI;
    }

    public static JList<?> getJList() {
        return JLIST;
    }

    public static JPanel getSuggestionsPanel() {
        return SUGGESTIONS_PANEL;
    }

    public static boolean updateAdvertiser(String message) {
        Advertiser advertiser = EveryWhereSearch.MYHINTLABEL;
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
        } catch (Throwable ignored) {
            log.error(ignored.getMessage());
            return false;
        }
    }

    public static boolean clearAdvertiser() {
        return updateAdvertiser("");
    }
}
