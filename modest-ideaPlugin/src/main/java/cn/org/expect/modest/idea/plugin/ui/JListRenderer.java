package cn.org.expect.modest.idea.plugin.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.modest.idea.plugin.MavenFinderContributor;
import cn.org.expect.modest.idea.plugin.MavenFinderIcons;
import cn.org.expect.modest.idea.plugin.db.MavenFinderDB;
import cn.org.expect.modest.idea.plugin.db.MavenFinderResult;
import cn.org.expect.modest.idea.plugin.db.MavenSearchStatement;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationList;
import cn.org.expect.modest.idea.plugin.navigation.NavigationItemComparator;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.diagnostic.Logger;

public class JListRenderer {
    private static final Logger log = Logger.getInstance(JListRenderer.class);

    public final static JListRenderer INSTANCE = new JListRenderer();

    private volatile JList jlist;

    private volatile MavenFinderContributor contributor;

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
        JList jlist = this.jlist;
        if (jlist == null) {
            jlist = IntelliJIdea.getJList();
        }

        if (jlist == null) {
            log.warn("--->      MavenFinder renderer not have JList !");
            String message = "<html><span style='color:red;'>MavenFinder render fail: have not JList</span></html>";
            IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
            return;
        }

//        Set<Thread> threads = Thread.getAllStackTraces().keySet();
//        for (Thread thread : threads) {
//            if (thread.getName().startsWith("AWT-EventQueue")) {
//                System.out.println(thread.getName());
//            }
//        }

        ListModel model = jlist.getModel();
        if (!(model instanceof SearchListModel)) {
            String message = "<html><span style='color:red;'>" + model.getClass().getName() + " not instanceof " + SearchListModel.class.getName() + " </span></html>";
            IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
            return;
        }

        SearchListModel listModel = (SearchListModel) model;
        if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
            try {
                JavaDialectFactory.get().setField(listModel, "myElementsComparator", new NavigationItemComparator());
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
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
                        log.error(e.getMessage());
                    }
                }
            }
        }

        // 添加查询结果
        int size = (result == null) ? 0 : result.size();
        if (size > 0) {
            List<SearchEverywhereFoundElementInfo> listModelElements = new ArrayList<SearchEverywhereFoundElementInfo>(size);
            for (MavenArtifact artifact : result.getArtifacts()) {
                MavenFinderNavigationItem item = new MavenFinderNavigationItem(artifact);
                listModelElements.add(new SearchEverywhereFoundElementInfo(item, 50, this.contributor));

                String groupId = artifact.getGroupId();
                String artifactId = artifact.getArtifactId();

                MavenFinderResult artifactList = MavenFinderDB.INSTANCE.select(groupId, artifactId);
                if (artifactList != null) {
                    item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_HAS_QUERY);
                }

                // 如果当前是展开状态
                if (artifact.isUnfold()) {
                    if (artifactList != null) {
                        item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_UNFOLD);
                        for (MavenArtifact listItem : artifactList.getArtifacts()) {
                            MavenFinderNavigationList nodeItem = new MavenFinderNavigationList(listItem);
                            listModelElements.add(new SearchEverywhereFoundElementInfo(nodeItem, 50, this.contributor));
                        }
                    }
                }

                if (MavenSearchStatement.INSTANCE.isExtraQuerying(groupId, artifactId)) {
                    item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_WAITING);
                }
            }

            try {
                listModel.addElements(listModelElements);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        // 选中某个记录
        MavenFinderNavigationItem selectedItem = IntelliJIdea.JLIST_SELECT_ITEM;
        if (selectedItem != null) {
            int selectedIndex = -1;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationItem) {
                    MavenFinderNavigationItem item = (MavenFinderNavigationItem) object;
                    if (selectedItem.getArtifact().equals(item.getArtifact()) && item.getArtifact().isUnfold()) {
                        selectedIndex = i;
                        break;
                    }
                }
            }

            if (selectedIndex == -1) {
                jlist.clearSelection();
            } else {
                jlist.setSelectedIndex(selectedIndex);
            }
        }

        try {
            jlist.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        String message = "<html><span style='color:orange;'>There are " + size + " matching maven artifact!</span></html>";
        IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
    }
}
