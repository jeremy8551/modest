package cn.org.expect.maven.intellij.idea;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.intellij.idea.navigation.NavigationCellRenderer;
import cn.org.expect.maven.intellij.idea.navigation.SearchNavigationHead;
import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.search.MavenSearchUtils;
import cn.org.expect.util.Ensure;
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.util.gotoByName.FileTypeRef;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.ide.util.gotoByName.GotoFileConfiguration;
import com.intellij.ide.util.gotoByName.GotoFileModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * Idea 搜索的贡献者
 */
public class MavenPluginContributor extends AbstractGotoSEContributor {
    private final static Log log = LogFactory.getLog(MavenPluginContributor.class);

    private final MavenPluginChooseContributor contributor;

    private final MavenSearchPlugin plugin;

    private Runnable rebuildList;

    public MavenPluginContributor(MavenSearchPlugin plugin) {
        super(plugin.getContext().getActionEvent());
        this.contributor = new MavenPluginChooseContributor(plugin);
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return MavenPluginContributor.class.getSimpleName() + ".Tab";
    }

    @Override
    protected boolean processElement(@NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<Object>> consumer, FilteringGotoByModel<?> model, Object element, int degree) {
        return super.processElement(progressIndicator, consumer, model, element, degree);
    }

    @Override
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super Object> consumer) {
        if (log.isDebugEnabled()) {
            log.debug("fetchElements({}, {}, {}) ", pattern, progressIndicator, consumer);
        }

        super.fetchElements(pattern, progressIndicator, consumer);
    }

    @Override
    public @NotNull ListCellRenderer<Object> getElementsRenderer() {
        return new NavigationCellRenderer(this);
    }

    /**
     * 在搜索之前从模式中过滤掉特殊符号
     *
     * @param pattern 搜索模型
     * @return 过滤后的字符串
     */
    @Override
    public @NotNull String filterControlSymbols(String pattern) {
        if (pattern != null && pattern.length() > 0) {
            this.plugin.asyncSearch(MavenSearchUtils.parse(pattern));
        }
        return pattern;
    }

    @Override
    public boolean processSelectedItem(Object selectedObject, int modifiers, String searchText) {
        // 禁用来源的处理逻辑：自动打开 url
        // super.processSelectedItem(selectedObject, modifiers, searchText);

        if (selectedObject instanceof SearchNavigationHead) {
            SearchNavigationHead navigation = (SearchNavigationHead) selectedObject;
            this.plugin.getContext().setSelectedNavigation(navigation); // 保存选择记录
            MavenArtifact artifact = navigation.getArtifact();

            if (log.isDebugEnabled()) {
                log.debug("processSelectedItem({}, {}, {}) {}, fold: {}, version: {}", selectedObject, modifiers, searchText, artifact, artifact.isFold(), artifact.getVersionCount());
            }

            // 折叠或展开
            if (artifact.isFold()) { // 设置为：展开
                artifact.setFold(false);
                String groupId = artifact.getGroupId();
                String artifactId = artifact.getArtifactId();
                if (this.plugin.getDatabase().select(groupId, artifactId) == null) {
                    navigation.setIcon(MavenPluginIcon.LEFT_WAITING); // 更改为：等待图标
                    this.plugin.asyncSearch(groupId, artifactId); // 后台查询 maven 工件
                } else {
                    this.plugin.repaintSearchResult();
                }
            } else { // 设置为：折叠
                artifact.setFold(true);
                this.plugin.repaintSearchResult();
            }
            return false;
        }

        return false;
    }

    /**
     * 获取所选元素的上下文数据
     *
     * @param element 元素
     * @param dataId  数据编号
     * @return
     */
    @Override
    public Object getDataForItem(Object element, String dataId) {
        if (log.isDebugEnabled()) {
            log.debug("getDataForItem({}, {})", element, dataId);
        }

        return super.getDataForItem(element, dataId);
    }

    @Override
    public int getElementPriority(Object element, String searchPattern) {
        if (log.isDebugEnabled()) {
            log.debug("getElementPriority({}, {}) ", element, searchPattern);
        }
        return 50;
    }

    @Override
    public boolean isMultiSelectionSupported() {
        return false;
    }

    /**
     * 返回可显示在搜索字段右侧的广告文本
     *
     * @return 广告文本
     */
    @Override
    public String getAdvertisement() {
        return this.plugin.getRemoteRepository().getAddress();
    }

    @Override
    protected FilteringGotoByModel<?> createModel(Project project) {
        return new MavenPluginModel(project, this.contributor);
    }

    /**
     * 定义是否应在“搜索无处不在”对话框中为此贡献者显示单独的选项卡。<br>
     * 除非绝对必要，否则请不要重写此方法。太多单独的选项卡会导致“到处搜索”对话框无法使用。
     *
     * @return true表示有单独的选项卡
     */
    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    /**
     * 搜索框中标签页的名字
     *
     * @return 标签页名
     */
    @Override
    public String getFullGroupName() {
        return this.getGroupName();
    }

    /**
     * 搜索框中标签页的名字
     *
     * @return 标签页名
     */
    @Override
    public String getGroupName() {
        return "Maven";
    }

    /**
     * 搜索框中标签页的排序序号
     *
     * @return 排序编号
     */
    @Override
    public int getSortWeight() {
        return 0;
    }

    /**
     * 定义是否可以在“查找”工具窗口中显示找到的结果
     *
     * @return
     */
    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return false;
    }

    @Override
    public List<AnAction> getActions(@NotNull Runnable onChanged) {
        if (log.isTraceEnabled()) {
            log.trace("getActions({}) ", onChanged);
        }

        this.rebuildList = Ensure.notNull(onChanged);
        return new ArrayList<>();
    }

    /**
     * 切换UI界面Tab，触发的任务
     *
     * @return 任务
     */
    public Runnable getRebuildListTask() {
        return rebuildList;
    }

    @NotNull
    public PersistentSearchEverywhereContributorFilter<FileTypeRef> createFileTypeFilter(@NotNull Project project) {
        List<FileTypeRef> items = new ArrayList<>(FileTypeRef.forAllFileTypes());
        items.add(0, GotoFileModel.DIRECTORY_FILE_TYPE_REF);
        return new PersistentSearchEverywhereContributorFilter<>(items, GotoFileConfiguration.getInstance(project), FileTypeRef::getName, FileTypeRef::getIcon);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
