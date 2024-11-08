package cn.org.expect.maven.search;

import java.io.File;
import javax.swing.*;

import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.search.db.MavenArtifactDatabase;

public interface SearchOperation {

    /**
     * 返回上下文信息
     *
     * @return 上下文信息
     */
    SearchContext getContext();

    /**
     * 多线程执行模糊搜索
     *
     * @param pattern 字符串
     */
    void asyncSearch(String pattern);

    /**
     * 多线程执行精确搜索
     *
     * @param groupId    域名
     * @param artifactId 工件名
     */
    void asyncSearch(String groupId, String artifactId);

    /**
     * 将文本信息复制到剪切板中
     *
     * @param text 文本信息
     */
    void copyToClipboard(String text);

    /**
     * 推送正常通知
     *
     * @param text 通知内容
     */
    void sendNotification(String text, Object... array);

    /**
     * 推送错误通知
     *
     * @param text 通知内容
     */
    void sendErrorNotification(String text, Object... array);

    /**
     * 推送通知
     *
     * @param text       通知内容
     * @param actionName 操作名称
     * @param file       操作打开的文件
     */
    void sendNotification(String text, String actionName, File file);

    /**
     * 设置搜索输入框中的文本
     *
     * @param text 文本信息
     */
    void setSearchFieldText(String text);

    /**
     * 更新搜索结果下方：广告栏中的信息
     *
     * @param message 文本信息
     * @param icon    图标
     */
    void setAdvertiser(String message, Icon icon);

    /**
     * 设置提醒文本 <br>
     * 不能使用 Idea 的渲染线程执行这个方法，需要有单独的线程
     *
     * @param message 文本信息
     */
    void setReminderText(String message);

    /**
     * 使用最新的查询结果，渲染 UI 界面
     */
    void repaint();

    /**
     * 使用参数指定的查询结果，渲染 UI 界面
     *
     * @param result 查询结果
     */
    void repaint(MavenSearchResult result);

    /**
     * 使用参数指定的查询结果，渲染 UI 界面
     *
     * @param result 查询结果
     */
    void repaintMore(MavenSearchResult result);

    /**
     * 返回 Maven 仓库信息
     *
     * @return Maven 仓库信息
     */
    MavenRepository getRemoteRepository();

    /**
     * 返回本地 Maven 仓库信息
     *
     * @return 本地 Maven 仓库信息
     */
    LocalRepository getLocalRepository();

    /**
     * 返回模糊查询工具
     *
     * @return 模糊查询工具
     */
    SearchInputThread getInputSearch();

    /**
     * 返回精确查询工具
     *
     * @return 精确查询工具
     */
    SearchServiceThread getServiceSearch();

    /**
     * 返回数据库对象
     *
     * @return 数据库对象
     */
    MavenArtifactDatabase getDatabase();
}
