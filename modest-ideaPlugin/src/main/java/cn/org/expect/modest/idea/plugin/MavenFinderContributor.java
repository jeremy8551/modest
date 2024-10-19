package cn.org.expect.modest.idea.plugin;

import java.util.List;
import javax.swing.*;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public class MavenFinderContributor extends AbstractGotoSEContributor {
    private static final Logger log = Logger.getInstance(MavenFinderContributor.class);

    protected final MavenFinderChooseContributor contributor;

    public MavenFinderContributor(AnActionEvent event) {
        super(event);
        this.contributor = new MavenFinderChooseContributor(this);
    }

    public String getSearchProviderId() {
        return MavenFinderContributor.class.getSimpleName() + ".Tab";
    }

    @Override
    protected boolean processElement(@NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<Object>> consumer, FilteringGotoByModel<?> model, Object element, int degree) {
        return super.processElement(progressIndicator, consumer, model, element, degree);
    }

    @Override
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super Object> consumer) {
//        System.out.println("fetchElements() " + pattern);
        super.fetchElements(pattern, progressIndicator, consumer);
    }

    @Override
    public ListCellRenderer<Object> getElementsRenderer() {
        return new MavenFinderRenderer(this);
    }

    /**
     * 在搜索之前从模式中过滤掉特殊符号
     *
     * @param pattern 搜索模型
     * @return 过滤后的字符串
     */
    @Override
    public String filterControlSymbols(String pattern) {
        if (pattern != null && pattern.length() > 0) {
            MavenFinderThread.INSTANCE.search(pattern);
        }
        return pattern;
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
        log.info("getDataForItem " + element + ", " + dataId);
        return super.getDataForItem(element, dataId);
    }

    @Override
    public int getElementPriority(Object element, String searchPattern) {
        return 50;
    }

    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
        return super.processSelectedItem(selected, modifiers, searchText);
    }

    /**
     * 返回可显示在搜索字段右侧的广告文本
     *
     * @return 广告文本
     */
    @Override
    public String getAdvertisement() {
        return "Maven Repository";
    }

    @Override
    protected FilteringGotoByModel<?> createModel(Project project) {
        return new MavenFinderModel(project, this.contributor);
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
        return "Repository";
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
        return true;
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return false;
    }

    @Override
    public List<AnAction> getActions(@NotNull Runnable onChanged) {
        return super.getActions(onChanged);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
