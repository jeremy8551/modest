package cn.org.expect.intellij.idea.plugin.maven.concurrent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginContributor;
import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPluginIcon;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenFoundElementInfoComparator;
import cn.org.expect.intellij.idea.plugin.maven.navigation.MavenSearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationItem;
import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.maven.concurrent.MavenSearchDownloadJob;
import cn.org.expect.maven.concurrent.MavenSearchEDTJob;
import cn.org.expect.maven.concurrent.MavenSearchExtraJob;
import cn.org.expect.maven.concurrent.MavenSearchMoreJob;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchAdvertiser;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchListModel;
import com.intellij.ui.components.JBList;

public class MavenSearchRepaintJob extends MavenSearchEDTJob {

    private final MavenSearchPlugin plugin;

    private final List<SearchEverywhereFoundElementInfo> elementInfoList;

    public MavenSearchRepaintJob(MavenSearchPlugin plugin, MavenSearchResult result) {
        super(null);
        this.plugin = Ensure.notNull(plugin);
        this.elementInfoList = new ArrayList<>();
        this.setRunnable(() -> this.paint(result));
    }

    protected void paint(MavenSearchResult result) {
        // 从 SearchEveryWhere UI 中读取 JList 与 Model
        JBList<Object> JBList = plugin.getIdeaUI().getJBList();
        SearchListModel model = plugin.getIdeaUI().getSearchListModel();
        boolean hasMore = this.hasMore(model);

        // 处理查询结果
        int foundNumber = 0;
        int size = 0;

        if (result != null) {
            foundNumber = result.getFoundNumber();
            size = result.size();
            this.processSearchResult(result);
        }

        // 一定要先删除 more 按钮
        model.clearMoreItems();

        // 将导航记录合并到数据模型中
        this.mergeTo(model);

        // 设置选中记录
        this.setSelectNavigation(JBList, model);

        // 设置 more 按钮
        try {
            model.setHasMore(plugin.getContributor(), //
                    !plugin.getService().isRunning(MavenSearchMoreJob.class, t -> true)  // 现在没有 more 功能运行
                            && ((hasMore && model.getSize() > 0 && plugin.isAllTab()) // ALL标签页，有 more 按钮
                            || (plugin.isSelfTab() && foundNumber > size) // 录数数 大于 查询结果
                    ) //
            );
            model.freezeElements();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        // 渲染 JBList
        try {
            JBList.revalidate();
            JBList.repaint();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("{}, size: {}, {}", MavenSearchRepaintJob.class.getSimpleName(), model.getSize(), JBList.getModel().getSize());
        }

        // 设置广告信息
        plugin.setStatusbarText(MavenSearchAdvertiser.NORMAL, MavenSearchMessage.get("maven.search.status.text", foundNumber, size));
    }

    /**
     * 选中记录
     *
     * @param JBList    组件
     * @param listModel 组件的数据模型
     */
    protected void setSelectNavigation(JBList<Object> JBList, SearchListModel listModel) {
        int selectedIndex = -1;

        SearchNavigationHead selectHead = plugin.getContext().getSelectNavigationHead();
        if (selectHead != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationHead) {
                    SearchNavigationHead head = (SearchNavigationHead) object;
                    if (selectHead.getArtifact().equals(head.getArtifact())) {
                        MavenArtifact artifact = selectHead.getArtifact();
                        if (artifact.isUnfold()) {
                            MavenSearchResult result = plugin.getDatabase().select(artifact.getGroupId(), artifact.getArtifactId());
                            if (result == null || result.isExpire(plugin.getContext().getExpireTimeMillis())) {
                                head.setIcon(MavenSearchPluginIcon.LEFT_WAITING); // 设置左侧等待图标
                            }
                        }

                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        SearchNavigationItem selectItem = plugin.getContext().getSelectNavigationItem();
        if (selectItem != null) {
            for (int i = 0, size = listModel.getSize(); i < size; i++) {
                Object object = listModel.getElementAt(i);
                if (object instanceof SearchNavigationItem) {
                    SearchNavigationItem item = (SearchNavigationItem) object;
                    if (selectItem.getArtifact().equals(item.getArtifact())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }

        if (selectedIndex == -1) {
            JBList.clearSelection();
        } else {
            JBList.setSelectedIndex(selectedIndex); // 选中
            JBList.ensureIndexIsVisible(selectedIndex); // 设置 JList 显示 selectedIndex 位置
        }
    }

    /**
     * 将查询结果转为导航记录
     */
    public void processSearchResult(MavenSearchResult result) {
        int priority = this.plugin.getContext().getElementPriority();
        MavenSearchPluginContributor contributor = plugin.getContributor();

        java.util.List<MavenArtifact> list = result.getList();
        for (MavenArtifact artifact : list) {
            SearchNavigationHead head = new SearchNavigationHead(artifact);
            this.elementInfoList.add(new SearchEverywhereFoundElementInfo(head, priority, contributor));

            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();

            MavenSearchResult itemResult = plugin.getDatabase().select(groupId, artifactId);
            if (itemResult != null && !itemResult.isExpire(plugin.getContext().getExpireTimeMillis())) {
                head.setIcon(MavenSearchPluginIcon.LEFT_HAS_QUERY);
            }

            // 如果当前是展开状态
            if (artifact.isUnfold()) {
                if (itemResult != null) {
                    head.setIcon(MavenSearchPluginIcon.LEFT_UNFOLD);
                    for (MavenArtifact itemArtifact : itemResult.getList()) {
                        SearchNavigationItem item = new SearchNavigationItem(itemArtifact, plugin.getLocalRepository().getJarfile(itemArtifact));
                        if (plugin.getService().isRunning(MavenSearchDownloadJob.class, job -> job.getArtifact().equals(itemArtifact))) { // 正在下载
                            item.setIcon(MavenSearchPluginIcon.RIGHT_DOWNLOAD);
                        } else if (plugin.getLocalRepository().exists(itemArtifact)) {
                            item.setIcon(MavenSearchPluginIcon.RIGHT_LOCAL);
                        }
                        this.elementInfoList.add(new SearchEverywhereFoundElementInfo(item, priority, contributor));
                    }
                }
            }

            // 判断是否正在查询详细信息
            if (plugin.getService().isRunning(MavenSearchExtraJob.class, job -> groupId.equals(job.getGroupId()) && artifactId.equals(job.getArtifactId()))) {
                head.setIcon(MavenSearchPluginIcon.LEFT_WAITING);
            }
        }
    }

    /**
     * 将导航记录添加到数据模型中
     *
     * @param model 数据模型
     */
    protected void mergeTo(SearchListModel model) {
        List<SearchEverywhereFoundElementInfo> all = new ArrayList<>(model.getSize() + this.elementInfoList.size());
        boolean addAll = true;

        // 保存其他搜索类别的记录
        for (int i = 0; i < model.getSize(); i++) {
            SearchEverywhereFoundElementInfo info = model.getRawFoundElementAt(i);
            Object element = info.getElement();
            if (element instanceof MavenSearchNavigation) {
                if (addAll) {
                    all.addAll(this.elementInfoList); // 将查询结果合并到 all 集合中
                    addAll = false;
                }
            } else {
                all.add(info);
            }
        }
        if (addAll) {
            all.addAll(this.elementInfoList);
        }

        this.setComparator(model, new MavenFoundElementInfoComparator());
        try {
            model.clear(); // 清空所有数据
            model.addElements(all);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            this.setComparator(model, SearchEverywhereFoundElementInfo.COMPARATOR.reversed());
        }
    }

    private void setComparator(SearchListModel listModel, Comparator comparator) {
        if (listModel.getClass().getSimpleName().equals("MixedSearchListModel")) {
            try {
                JavaDialectFactory.get().setField(listModel, "myElementsComparator", comparator);
            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    protected boolean hasMore(SearchListModel model) {
        for (int i = model.getSize() - 1; i >= 0; i--) {
            if (model.isMoreElement(i)) {
                return true;
            }
        }
        return false;
    }
}
