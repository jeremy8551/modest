package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.util.List;
import javax.swing.*;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearch;
import cn.org.expect.maven.Artifact;
import cn.org.expect.util.ClassUtils;
import com.intellij.navigation.NavigationItem;

/**
 * 查询结果导航结果中每条记录的接口
 */
public interface MavenSearchNavigation extends NavigationItem {

    /**
     * 层级深度
     *
     * @return 层级深度，从1开始
     */
    int getDepth();

    /**
     * 设置层级深度
     *
     * @param depth 层级深度，从1开始
     */
    void setDepth(int depth);

    /**
     * 返回 Maven 工件信息
     *
     * @return Maven 工件信息
     */
    Artifact getArtifact();

    /**
     * 设置左侧图标
     *
     * @param icon 图标
     */
    void setLeftIcon(Icon icon);

    /**
     * 返回左侧图标
     *
     * @return 图标
     */
    Icon getLeftIcon();

    /**
     * 设置导航记录右侧的图标
     *
     * @param rightIcon 图标
     */
    void setRightIcon(Icon rightIcon);

    /**
     * 返回导航记录右侧的图标与文本
     *
     * @return 图标与文本
     */
    Icon getRightIcon();

    /**
     * 设置右侧文本
     *
     * @param rightText 文本信息
     */
    void setRightText(String rightText);

    /**
     * 返回右侧文本
     *
     * @return 文本信息
     */
    String getRightText();

    /**
     * 设置左侧文本
     *
     * @param text 文本信息
     */
    void setPresentableText(String text);

    /**
     * 返回左侧文本
     *
     * @return 文本信息
     */
    String getPresentableText();

    /**
     * 返回左侧（小字）文本
     *
     * @param text 文本信息
     */
    void setLocationString(String text);

    /**
     * 设置左侧（小字）文本
     *
     * @return 文本信息
     */
    String getLocationString();

    /**
     * 是否支持展开与折叠操作
     *
     * @param search 搜索接口
     * @return 返回true表示支持，false表示不支持
     */
    boolean supportFold(MavenSearch search);

    /**
     * 判断是否折叠
     *
     * @return 返回true表示折叠，false表示展开
     */
    boolean isFold();

    /**
     * 设置展开
     *
     * @param search 搜索接口
     */
    void setUnfold(MavenSearch search);

    /**
     * 设置折叠
     *
     * @param search 搜索接口
     */
    void setFold(MavenSearch search);

    /**
     * 展开操作
     *
     * @param search 搜索接口
     */
    void unfold(MavenSearch search);

    /**
     * 折叠操作
     *
     * @param search 搜索接口
     */
    void fold(MavenSearch search);

    /**
     * 返回子导航记录集合
     *
     * @return 导航记录集合
     */
    List<? extends MavenSearchNavigation> getNavigationList();

    /**
     * 判断工件信息是否相等
     *
     * @param object 对象
     * @return 返回true表示Class相同且工件相等，false表示不等
     */
    default boolean match(Object object) {
        return object != null && ClassUtils.equals(this.getClass(), object.getClass()) && this.getArtifact().equals(((MavenSearchNavigation) object).getArtifact());
    }

    /**
     * 是否支持在导航记录上显示菜单
     *
     * @return 返回true表示支持，false表示不支持
     */
    boolean supportMenu();
}
