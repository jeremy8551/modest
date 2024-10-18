package cn.org.expect.modest.idea.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.util.Dates;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;

public class JListRenderer implements Runnable {

    public final static JListRenderer INSTANCE = new JListRenderer();

    protected volatile JList jlist;

    protected volatile MavenFinderContributor contributor;

    private JListRenderer() {
    }

    /**
     * 返回线程名
     *
     * @return 线程名
     */
    public String getThreadName() {
        return this.getClass().getSimpleName() + Dates.currentTimeStamp();
    }

    public void setList(JList list) {
        if (list != null) {
            this.jlist = list;
        }
    }

    public void setContributor(MavenFinderContributor contributor) {
        this.contributor = contributor;
    }

    public void run() {
        Dates.sleep(400);
        this.run(this.jlist);
    }

    public synchronized void run(JList list) {
        if (list == null) {
            System.out.println("renderer() not have JList !");
            return;
        }

//        System.out.println("renderer()");

        ListModel model = list.getModel();
        if (model instanceof SearchListModel) {
            SearchListModel listModel = (SearchListModel) model;

            if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
                try {
                    Field field = listModel.getClass().getDeclaredField("myElementsComparator");
                    field.setAccessible(true);
                    field.set(listModel, new SearchEverywhereFoundElementInfoComparator());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationItem) {
                    try {
                        SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
                        listModel.removeElement(object, info.getContributor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // 添加查询结果
            List<SearchEverywhereFoundElementInfo> listModelElements = new ArrayList<SearchEverywhereFoundElementInfo>(99);
            MavenFinderResult last = MavenFinderResultSet.INSTANCE.last();
            if (last != null) {
                for (MavenFinderNavigationItem item : last.getNavigationItems()) {
                    listModelElements.add(new SearchEverywhereFoundElementInfo(item, 50, this.contributor));
                }
            }

            try {
//                System.out.println("mode size: " + listModelElements.size());
                listModel.addElements(listModelElements);
                list.repaint();
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }
        }
    }
}
