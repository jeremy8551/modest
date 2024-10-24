package cn.org.expect.modest.idea.plugin;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationList;
import cn.org.expect.modest.idea.plugin.navigation.NavigationItemComparator;
import cn.org.expect.modest.idea.plugin.query.MavenFinderResult;
import cn.org.expect.modest.idea.plugin.query.MavenSearchStatement;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.openapi.diagnostic.Logger;

public class MavenFinderRenderer {
    private static final Logger log = Logger.getInstance(MavenFinderRenderer.class);

    public final static MavenFinderRenderer INSTANCE = new MavenFinderRenderer();

    private volatile JList jlist;

    private volatile MavenFinderContributor contributor;

    protected MavenFinderRenderer() {
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
            list = IdeaUI.getJList();
        }

        if (list == null) {
            log.warn("--->      MavenFinder renderer not have JList !");
            String message = "<html><span style='color:orange;'>MavenFinder renderer fail: have not JList</span></html>";
            IdeaUI.updateAdvertiser(message);
            return;
        }

        int size = 0;
        ListModel model = list.getModel();
        if (model instanceof SearchListModel) {
            SearchListModel listModel = (SearchListModel) model;

            if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
                try {
                    JavaDialectFactory.get().setField(listModel, "myElementsComparator", new NavigationItemComparator());
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
                            log.error(e.getMessage());
                        }
                    }
                }
            }

            // 添加查询结果
            size = (result == null) ? 0 : result.size();
            if (size > 0) {
                List<SearchEverywhereFoundElementInfo> listModelElements = new ArrayList<SearchEverywhereFoundElementInfo>(size);
                for (MavenArtifact artifact : result.getArtifacts()) {
                    MavenFinderNavigationItem item = new MavenFinderNavigationItem(artifact);
                    listModelElements.add(new SearchEverywhereFoundElementInfo(item, 50, this.contributor));

                    String groupId = artifact.getGroupId();
                    String artifactId = artifact.getArtifactId();

                    MavenFinderResult artifactList = MavenSearchStatement.INSTANCE.getResult(groupId, artifactId);
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

                    if (MavenSearchStatement.INSTANCE.isQuerying(groupId, artifactId)) {
                        item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_WAITING);
                    }
                }

                try {
                    listModel.addElements(listModelElements);
                } catch (Throwable ignored) {
                    log.error(ignored.getMessage());
                }
            }

            // 选中某个记录
            String selectText = IdeaUI.JLIST_SELECT_TEXT;
            if (selectText != null) {
                int selectedIndex = -1;
                for (int i = listModel.getSize() - 1; i >= 0; i--) {
                    Object object = listModel.getElementAt(i);
                    if (object instanceof MavenFinderNavigationItem item) {
                        if (selectText.equals(item.getPresentableText())) {
                            if (item.getArtifact().isFold()) {
                                selectedIndex = -1;
                            } else {
                                selectedIndex = i;
                            }
                            break;
                        }
                    }
                }

                if (selectedIndex == -1) {
                    list.clearSelection();
                } else {
                    list.setSelectedIndex(selectedIndex);
                }
            }

            try {
                list.repaint();
            } catch (Throwable ignored) {
                log.error(ignored.getMessage());
            }
        }

        String message = "<html><span style='color:orange;'>There are " + size + " matching maven artifact!</span></html>";
        IdeaUI.updateAdvertiser(message);
    }
}
