package cn.org.expect.modest.idea.plugin;

import java.lang.reflect.Field;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class EveryWhereSearch {

    private static volatile SearchEverywhereUI UI;

    private static volatile JList<?> JLIST;

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
            Field field = jlistClass.getDeclaredField("myResultsList");
            EveryWhereSearch.JLIST = (javax.swing.JList<?>) JavaDialectFactory.get().getField(ui, field);
        } catch (Exception e) {
            Field[] fields = jlistClass.getDeclaredFields();
            for (Field field : fields) {
                System.err.println(field.getName() + "   " + field.getType().getSimpleName());
            }
            e.printStackTrace();
        }
    }

    public static SearchEverywhereUI getUI() {
        return UI;
    }

    public static JList<?> getJList() {
        return JLIST;
    }
}
