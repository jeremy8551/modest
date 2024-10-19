package cn.org.expect.modest.idea.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.diagnostic.Logger;

public class JListRenderer {
    private static final Logger log = Logger.getInstance(JListRenderer.class);

    public final static JListRenderer INSTANCE = new JListRenderer();

    protected volatile JList jlist;

    protected volatile MavenFinderContributor contributor;

    protected JListRenderer() {
    }

    public void setList(JList list) {
        if (list != null) {
            this.jlist = list;
        }
    }

    public void setContributor(MavenFinderContributor contributor) {
        this.contributor = contributor;
    }

    public synchronized void execute(MavenFinderResult result) {
        JList list = this.jlist;
        if (list == null) {
            log.warn("--->      MavenFinder renderer not have JList !");
            return;
        }

        ListModel model = list.getModel();
        if (model instanceof SearchListModel) {
            SearchListModel listModel = (SearchListModel) model;

            if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
                try {
                    Field field = listModel.getClass().getDeclaredField("myElementsComparator");
                    field.setAccessible(true);
                    field.set(listModel, new ElementInfoComparator());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            boolean print = true;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationItem) {
                    try {
                        SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
                        listModel.removeElement(object, info.getContributor());
                    } catch (Exception e) {
                        if (print) {
                            print = false;
                            e.printStackTrace();
                        }
                    }
                }
            }

            // 添加查询结果
            int length = result == null ? 10 : result.size();
            List<SearchEverywhereFoundElementInfo> listModelElements = new ArrayList<SearchEverywhereFoundElementInfo>(length);
            if (result != null) {
                for (MavenFinderNavigationItem item : result.getNavigationItems()) {
                    listModelElements.add(new SearchEverywhereFoundElementInfo(item, 50, this.contributor));
                }
            }

            try {
                listModel.addElements(listModelElements);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                list.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
