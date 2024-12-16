package cn.org.expect.intellij.idea.plugin.maven;

import java.awt.*;

import cn.org.expect.maven.search.SearchNavigation;
import cn.org.expect.intellij.idea.plugin.maven.navigation.SearchEverywhereNavigationCollection;
import cn.org.expect.maven.search.ArtifactSearchContext;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 搜索插件的上下文信息
 */
public interface MavenSearchPluginContext extends ArtifactSearchContext {

    /**
     * 返回事件
     *
     * @return 事件
     */
    AnActionEvent getActionEvent();

    /**
     * 返回选中的导航记录
     *
     * @return 导航记录
     */
    SearchNavigation geSelectedNavigation();

    /**
     * 设置选中的导航记录
     *
     * @param selectNavigation 导航记录
     */
    void setSelectedNavigation(SearchNavigation selectNavigation);

    /**
     * 返回 JList 当前的位置
     *
     * @return 位置信息
     */
    Rectangle getVisibleRect();

    /**
     * 保存 JList 当前的位置
     *
     * @param visibleRect 位置信息
     */
    void setVisibleRect(Rectangle visibleRect);

    /**
     * 返回导航记录
     *
     * @return 导航记录
     */
    SearchEverywhereNavigationCollection getNavigationList();

    /**
     * 设置导航记录
     *
     * @param navigationList 导航记录
     */
    void setNavigationList(SearchEverywhereNavigationCollection navigationList);

    /**
     * 复制上下文信息
     *
     * @param context 上下文信息
     */
    default void clone(MavenSearchPluginContext context) {
        this.setSelectedNavigation(context.geSelectedNavigation());
        this.setVisibleRect(context.getVisibleRect());
        this.setNavigationList(context.getNavigationList());
    }
}
