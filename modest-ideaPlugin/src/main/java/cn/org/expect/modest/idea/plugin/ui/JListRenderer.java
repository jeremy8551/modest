package cn.org.expect.modest.idea.plugin.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.modest.idea.plugin.MavenFinderContributor;
import cn.org.expect.modest.idea.plugin.MavenFinderIcons;
import cn.org.expect.modest.idea.plugin.db.MavenFinderDB;
import cn.org.expect.modest.idea.plugin.db.MavenFinderResult;
import cn.org.expect.modest.idea.plugin.db.MavenSearchExtraThread;
import cn.org.expect.modest.idea.plugin.navigation.MavenArtifact;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigation;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItem;
import cn.org.expect.modest.idea.plugin.navigation.MavenFinderNavigationItemWrapper;
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

        this.removeElementInfo(listModel, this.getNavigationItemWrapper(listModel));
        List<MavenFinderNavigation> navigations = this.toList(result);
        Iterator<MavenFinderNavigation> it = navigations.iterator();

        for (int i = 0; i < listModel.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            if (info instanceof MavenFinderFoundElementInfo) {
                if (it.hasNext()) { // 向 ListModel 添加记录
                    MavenFinderNavigation item = it.next();
                    MavenFinderFoundElementInfo element = (MavenFinderFoundElementInfo) info;
                    element.setElement(item);
                    element.setContributor(this.contributor);
                } else { // 从 ListModel 删除记录
                    MavenFinderFoundElementInfo element = (MavenFinderFoundElementInfo) info;
                    Object object = element.getElement();
                    if (object != null) {
                        try {
                            listModel.removeElement(object, info.getContributor());
                        } catch (Exception e) { // TODO 如果不能删除，则将导航记录清空，排序时放到最后
                            element.setElement(null);
                        }
                    }
                }
            }
        }

        // 向 ListModel 添加记录
        if (it.hasNext()) {
            List<MavenFinderFoundElementInfo> list = new ArrayList<MavenFinderFoundElementInfo>(navigations.size());
            while (it.hasNext()) {
                MavenFinderNavigation navigation = it.next();
                MavenFinderFoundElementInfo info = new MavenFinderFoundElementInfo(navigation, this.contributor);
                list.add(info);
            }

            try {
                listModel.addElements(list);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }

        // 选中某个记录
        this.selectJlist(jlist, listModel);

        try {
            jlist.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        int size = (result == null) ? 0 : result.size();
        String message = "<html><span style='color:orange;'>There are " + size + " matching maven artifact!</span></html>";
        IntelliJIdea.updateAdvertiser(message, MavenFinderIcons.MAVEN_REPOSITORY_BOTTOM);
    }

    protected List<SearchEverywhereFoundElementInfo> getNavigationItemWrapper(SearchListModel listModel) {
        List<SearchEverywhereFoundElementInfo> list = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = listModel.getRawFoundElementAt(i);
            if (info.getElement() instanceof MavenFinderNavigationItemWrapper navigation) {
                list.add(info);
            }
        }
        return list;
    }

    protected void removeElementInfo(SearchListModel listModel, List<SearchEverywhereFoundElementInfo> list) {
        for (SearchEverywhereFoundElementInfo info : list) {
            try {
                listModel.removeElement(info.getElement(), info.getContributor());
            } catch (Exception e) { // TODO 如果不能删除，则将导航记录清空，排序时放到最后
                e.printStackTrace();
            }
        }
    }

    protected void selectJlist(JList jlist, SearchListModel listModel) {
        MavenFinderNavigationItem selectedItem = IntelliJIdea.JLIST_SELECT_ITEM;
        if (selectedItem != null) {
            int selectedIndex = -1;
            for (int i = listModel.getSize() - 1; i >= 0; i--) {
                Object object = listModel.getElementAt(i);
                if (object instanceof MavenFinderNavigationItem) {
                    MavenFinderNavigationItem item = (MavenFinderNavigationItem) object;
                    if (selectedItem.getArtifact().equals(item.getArtifact()) && item.getArtifact().isUnfold()) {
                        selectedIndex = i;

                        // 设置左侧等待图标
                        MavenArtifact artifact = selectedItem.getArtifact();
                        if (artifact.isUnfold() && MavenFinderDB.INSTANCE.select(artifact.getGroupId(), artifact.getArtifactId()) == null) {
                            item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_WAITING);
                        }
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
    }

    protected List<MavenFinderNavigation> toList(MavenFinderResult result) {
        if (result == null) {
            return new ArrayList<MavenFinderNavigation>(0);
        }

        List<MavenArtifact> artifacts = result.getArtifacts();
        int size = artifacts.size();
        List<MavenFinderNavigation> list = new ArrayList<MavenFinderNavigation>(size);
        for (MavenArtifact artifact : artifacts) {
            MavenFinderNavigationItem item = new MavenFinderNavigationItem(artifact);
            list.add(item);

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenFinderResult artifactList = MavenFinderDB.INSTANCE.select(groupId, artifactId);
            boolean exists = artifactList != null;
            if (exists) {
                item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (exists) {
                    item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_UNFOLD);
                    for (MavenArtifact listItem : artifactList.getArtifacts()) {
                        MavenFinderNavigationList nodeItem = new MavenFinderNavigationList(listItem);
                        list.add(nodeItem);
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (MavenSearchExtraThread.INSTANCE.isExtraQuerying(groupId, artifactId)) {
                item.setIcon(MavenFinderIcons.MAVEN_REPOSITORY_LEFT_WAITING);
            }
        }
        return list;
    }
}
