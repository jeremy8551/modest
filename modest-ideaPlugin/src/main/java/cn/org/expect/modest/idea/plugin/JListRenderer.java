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
                    field.set(listModel, new NavigationItemComparator());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            boolean print = true;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationItem || object instanceof MavenFinderNavigationList) {
                    try {
                        SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
                        listModel.removeElement(object, info.getContributor());
                    } catch (Exception e) { // TODO 如果不能删除，则将导航记录清空，排序时放到最后
                        if (print) {
                            print = false;
                            e.printStackTrace();
                        }
                    }
                }
            }

            // 添加查询结果
            int length = (result == null) ? 0 : result.size();
            if (length > 0) {
                List<SearchEverywhereFoundElementInfo> listModelElements = new ArrayList<SearchEverywhereFoundElementInfo>(length);
                for (MavenArtifact artifact : result.getArtifacts()) {
                    MavenFinderNavigationItem item = new MavenFinderNavigationItem(artifact);
                    listModelElements.add(new SearchEverywhereFoundElementInfo(item, 50, this.contributor));

                    String groupId = artifact.getGroupId();
                    String artifactId = artifact.getArtifactId();
                    MavenFinderResult artifactList = MavenSearchStatement.INSTANCE.getResult(groupId, artifactId);
                    if (artifactList != null) {
                        if (item.isFold()) { // 如果当前是折叠状态，就展开
                            item.setFold(false);
                            for (MavenArtifact listItem : artifactList.getArtifacts()) {
                                MavenFinderNavigationList nodeItem = new MavenFinderNavigationList(listItem);
                                listModelElements.add(new SearchEverywhereFoundElementInfo(nodeItem, 50, this.contributor));
                            }
                        } else { // 如果当前是展开状态，就折叠
                            item.setFold(true);
                        }
                    }
                }

                try {
                    listModel.addElements(listModelElements);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            try {
                list.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
