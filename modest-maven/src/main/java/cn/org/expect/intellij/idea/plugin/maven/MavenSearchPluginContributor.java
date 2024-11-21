package cn.org.expect.intellij.idea.plugin.maven;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.navigation.NavigationCellRenderer;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchNavigationHead;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.MavenSearchMessage;
import cn.org.expect.util.CollectionUtils;
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.ExtendedInfo;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.actions.searcheverywhere.ScopeChooserAction;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.ide.util.scopeChooser.ScopeDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Search Everywhere 自定义的搜索类别
 */
public class MavenSearchPluginContributor extends AbstractGotoSEContributor {
    private final static Log log = LogFactory.getLog(MavenSearchPluginContributor.class);

    private final MavenSearchPluginChooseContributor contributor;

    private final MavenSearchPlugin plugin;

    private volatile Runnable onChanged;

    public MavenSearchPluginContributor(@NotNull MavenSearchPlugin plugin) {
        super(plugin.getContext().getActionEvent());
        this.contributor = new MavenSearchPluginChooseContributor(plugin);
        this.plugin = plugin;
    }

    public @NotNull String getSearchProviderId() {
        return MavenSearchPlugin.class.getSimpleName() + ".Tab";
    }

    protected boolean processElement(@NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<Object>> consumer, FilteringGotoByModel<?> model, Object element, int degree) {
        return super.processElement(progressIndicator, consumer, model, element, degree);
    }

    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super Object> consumer) {
        if (log.isDebugEnabled()) {
            log.debug("fetchElements({}, {}, {}) ", pattern, progressIndicator, consumer);
        }

        super.fetchElements(pattern, progressIndicator, consumer);
    }

    public @NotNull ListCellRenderer<Object> getElementsRenderer() {
        return new NavigationCellRenderer(this);
    }

    /**
     * 在搜索之前从模式中过滤掉特殊符号
     *
     * @param pattern 搜索模型
     * @return 过滤后的字符串
     */
    public @NotNull String filterControlSymbols(@NotNull String pattern) {
        return pattern;
    }

    /**
     * 选中查询结果触发的事件
     *
     * @param selectedObject 查询结果
     * @param modifiers      快捷键
     * @param searchText     搜索文本
     * @return 默认返回 false
     */
    public boolean processSelectedItem(Object selectedObject, int modifiers, String searchText) {
        if (selectedObject instanceof SearchNavigationHead) {
            SearchNavigationHead head = (SearchNavigationHead) selectedObject;
            this.plugin.getContext().setSelectNavigationHead(head); // 保存选择记录
            this.plugin.getContext().setSelectNavigationItem(null); // 清空子选项

            MavenArtifact artifact = head.getArtifact();
            if (log.isDebugEnabled()) {
                log.debug("processSelectedItem({}, {}, {}) {}, fold: {}, version: {}", selectedObject, modifiers, searchText, artifact, artifact.isFold(), artifact.getVersionCount());
            }

            // 折叠或展开
            if (artifact.isFold()) { // 设置为：展开
                artifact.setFold(false);
                String groupId = artifact.getGroupId();
                String artifactId = artifact.getArtifactId();

                MavenSearchResult result = this.plugin.getDatabase().select(groupId, artifactId);
                if (result == null || result.isExpire(this.plugin.getSettings().getExpireTimeMillis())) {
                    head.setIcon(MavenSearchPluginIcon.LEFT_WAITING); // 更改为：等待图标
                    this.plugin.asyncSearch(groupId, artifactId); // 后台查询 maven 工件
                } else {
                    this.plugin.showSearchResult();
                }
            } else { // 设置为：折叠
                artifact.setFold(true);
                this.plugin.showSearchResult();
            }
            return false;
        }

        return false;
    }

    /**
     * 从参数 element 中读取数据
     *
     * @param element 元素
     * @param dataId  数据编号
     * @return 返回数据
     */
    public Object getDataForItem(Object element, String dataId) {
        if (log.isDebugEnabled()) {
            log.debug("getDataForItem({}, {})", element, dataId);
        }

        return super.getDataForItem(element, dataId);
    }

    public int getElementPriority(Object element, String searchPattern) {
        if (log.isDebugEnabled()) {
            log.debug("getElementPriority({}, {}) ", element, searchPattern);
        }
        return this.plugin.getSettings().getElementPriority();
    }

    public ExtendedInfo createExtendedInfo() {
        if (plugin.isSelfTab()) {
            return new ExtendedInfo(this.plugin.getIdeaUI()::getAdvertiserText, (o -> new AnAction() {
                public void actionPerformed(@NotNull AnActionEvent event) {
                    if (log.isDebugEnabled()) {
                        log.debug("ExtendedInfo actionPerformed()");
                    }
                }
            }));
        }
        return null;
    }

    public boolean isMultiSelectionSupported() {
        return false;
    }

    /**
     * 返回可显示在搜索字段右侧的广告文本
     *
     * @return 广告文本
     */
    public String getAdvertisement() {
        return this.plugin.getRepository().getAddress();
    }

    protected FilteringGotoByModel<?> createModel(Project project) {
        return new MavenSearchPluginModel(project, this.contributor);
    }

    /**
     * 定义是否应在“搜索无处不在”对话框中为此贡献者显示单独的选项卡。<br>
     * 除非绝对必要，否则请不要重写此方法。太多单独的选项卡会导致“到处搜索”对话框无法使用。
     *
     * @return true表示有单独的选项卡
     */
    public boolean isShownInSeparateTab() {
        return this.plugin.getSettings().isTabVisible();
    }

    /**
     * 搜索框中标签页的名字
     *
     * @return 标签页名
     */
    public String getFullGroupName() {
        return this.getGroupName();
    }

    /**
     * 搜索框中标签页的名字
     *
     * @return 标签页名
     */
    public String getGroupName() {
        return MavenSearchMessage.get("maven.search.tab.name");
    }

    /**
     * 搜索框中标签页的排序序号
     *
     * @return 排序编号
     */
    public int getSortWeight() {
        return this.plugin.getSettings().getTabIndex();
    }

    /**
     * 定义是否可以在“查找”工具窗口中显示找到的结果，对应搜索窗口右上角的图标
     *
     * @return 返回true表示可以
     */
    public boolean showInFindResults() {
        return false;
    }

    /**
     * 是否支持查询空字符串
     *
     * @return 返回true表示支持
     */
    public boolean isEmptyPatternSupported() {
        return false;
    }

    /**
     * 搜索界面UI右上角的按钮：可以选择不同的 Maven仓库
     *
     * @param onChanged 重建 JList 的事件
     * @return 处理逻辑（包含UI）集合
     */
    public @NotNull List<AnAction> getActions(@NotNull Runnable onChanged) {
        if (log.isTraceEnabled()) {
            log.trace("getActions({}) ", onChanged);
        }

        this.onChanged = onChanged;

        List<MavenSearchScopeDescriptor> descriptors = MavenSearchScopeDescriptor.getList();

        ArrayList<AnAction> result = new ArrayList<>();
        result.add(new ScopeChooserAction() {

            public boolean isEverywhere() {
                return true;
            }

            public void setEverywhere(boolean everywhere) {
            }

            public boolean canToggleEverywhere() {
                return true;
            }

            /**
             * 在下拉列表中，光标覆盖选项时出发的事件
             *
             * @param e 事件
             * @return 事件的处理逻辑
             */
            public @NotNull AnAction[] getChildren(AnActionEvent e) {
                if (log.isTraceEnabled()) {
                    log.trace("getChildren({}) ", e);
                }
                return new AnAction[0];
            }

            /**
             * 在下拉列表中，选中某一个选项触发的事件
             *
             * @param descriptor 选项信息
             */
            protected void onScopeSelected(@NotNull ScopeDescriptor descriptor) {
                String repositoryId = ((MavenSearchScope) descriptor.getScope()).getRepositoryId();
                plugin.setRepositoryId(repositoryId);
                onChanged.run(); // 更新：搜索框右侧的广告信息
                plugin.asyncSearch();
            }

            protected @NotNull ScopeDescriptor getSelectedScope() {
                MavenRepository repository = plugin.getRepository();
                if (repository != null) {
                    Class<?> type = repository.getClass();
                    for (MavenSearchScopeDescriptor descriptor : descriptors) {
                        if (type.equals(descriptor.getScope().getType())) {
                            return descriptor;
                        }
                    }
                }
                return CollectionUtils.firstElement(descriptors);
            }

            protected void onProjectScopeToggled() {
            }

            protected boolean processScopes(@NotNull Processor<? super ScopeDescriptor> processor) {
                return ContainerUtil.process(descriptors, processor);
            }
        });

//        result.add(new FixedWindowAction(this.plugin));
        return result;
    }

    public void dispose() {
        super.dispose();
    }

//    public static class FixedWindowAction extends ToggleAction {
//
//        private MavenSearchPlugin plugin;
//
//        private MavenSearchPluginContext context;
//
//        public FixedWindowAction(MavenSearchPlugin plugin) {
//            super("pin Window", "pin Window", AllIcons.General.Pin_tab);
//            int mask = SystemInfo.isMac ? 256 : 128;
//            this.plugin = plugin;
//            this.context = plugin.getContext();
//            this.registerCustomShortcutSet(68, mask, plugin.getIdeaUI().getSearchEverywhereUI());
//        }
//
//        public boolean isSelected(AnActionEvent e) {
//            return this.context.isPinWindow();
//        }
//
//        public void setSelected(AnActionEvent e, boolean state) {
//            System.out.println("setSelected()");
//            this.context.setPinWindow(state);
//
//            SearchEverywhereUI ui = this.plugin.getIdeaUI().getSearchEverywhereUI();
//            Project project = context.getActionEvent().getProject();
//
//            ui.addAncestorListener(new AncestorListenerAdapter() {
//                @Override
//                public void ancestorRemoved(AncestorEvent event) {
//                    System.out.println("ancestorRemoved");
//
//                    JFrame frame = new JFrame("Always On Top Panel");
//                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//                    frame.add(ui);
//
//                    // 设置 JFrame 属性
//                    frame.setSize(ui.getWidth(), ui.getHeight());
//                    frame.setAlwaysOnTop(true); // 窗口始终在最前
//                    frame.setFocusableWindowState(true); // 窗口始终可获取焦点
//                    frame.setLocationRelativeTo(null); // 居中显示
//                    frame.setVisible(true);
//                }
//            });
//        }
//
//        public void actionPerformed(@NotNull AnActionEvent e) {
//            System.out.println("actionPerformed()");
//            super.actionPerformed(e);
//        }
//    }
}
