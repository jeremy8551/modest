package cn.org.expect.modest.idea.plugin;

import java.util.List;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

public class MavenFinderContributor extends AbstractGotoSEContributor {

    private static final Logger log = Logger.getInstance(MavenFinderContributor.class);

    protected final MavenFinderChooseContributor contributor;

    public MavenFinderContributor(AnActionEvent event) {
        super(event);
        this.contributor = new MavenFinderChooseContributor();
    }

    /**
     * 在搜索之前从模式中过滤掉特殊符号
     *
     * @param pattern 搜索模型
     * @return 过滤后的字符串
     */
    public String filterControlSymbols(String pattern) {
        System.out.println("filterControlSymbols " + pattern);
        if (pattern != null && pattern.length() > 0) {
            this.contributor.query(pattern);
        }
        return pattern;
    }

    /**
     * 获取所选元素的上下文数据
     *
     * @param element
     * @param dataId
     * @return
     */
    public Object getDataForItem(Object element, String dataId) {
        log.info("getDataForItem " + element + ", " + dataId);
        return super.getDataForItem(element, dataId);
    }

    public int getElementPriority(@NotNull Object element, @NotNull String searchPattern) {
        return 0;
    }

    /**
     * 返回可显示在搜索字段右侧的广告文本
     *
     * @return 广告文本
     */
    public String getAdvertisement() {
        return "Maven Repository";
    }

    protected @NotNull FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return new MavenFinderFilteringGotoByModel(project, this.contributor);
    }

    public @NotNull List<AnAction> createRightActions(Function1<? super AnAction, Unit> function1, Runnable runnable) {
        return ContainerUtil.emptyList();
    }

    /**
     * 定义是否应在“搜索无处不在”对话框中为此贡献者显示单独的选项卡。<br>
     * 除非绝对必要，否则请不要重写此方法。太多单独的选项卡会导致“到处搜索”对话框无法使用。
     *
     * @return true表示有单独的选项卡
     */
    public boolean isShownInSeparateTab() {
        return true;
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
    public @NotNull String getGroupName() {
        return "MavenRepository";
    }

    /**
     * 搜索框中标签页的排序序号
     *
     * @return 排序编号
     */
    public int getSortWeight() {
        return 0;
    }

    /**
     * 定义是否可以在“查找”工具窗口中显示找到的结果
     *
     * @return
     */
    public boolean showInFindResults() {
        return true;
    }
}
