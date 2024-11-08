package cn.org.expect.maven.search;

import java.io.File;

import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.maven.search.db.MavenArtifactDatabase;

public interface MavenSearch {

    /**
     * 返回上下文信息
     *
     * @return 上下文信息
     */
    MavenSearchContext getContext();

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
     * 推送通知
     *
     * @param type 通知类型
     * @param text 通知内容
     */
    void sendNotification(MavenSearchNotification type, String text, Object... array);

    /**
     * 推送通知
     *
     * @param type       通知类型
     * @param text       通知内容
     * @param actionName 操作名称
     * @param file       打开的文件
     */
    void sendNotification(MavenSearchNotification type, String text, String actionName, File file);

    /**
     * 设置 UI 界面中的搜索文本
     *
     * @param text 文本信息
     */
    void setSearchText(String text);

    /**
     * 在等待搜索时，显示的文本信息
     *
     * @param message 文本信息
     */
    void setWaitingText(String message);

    /**
     * 执行搜索时，显示的文本信息
     *
     * @param type    文本类型
     * @param message 文本信息
     */
    void setRunningText(MavenSearchAdvertiser type, String message);

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
     * 触发更多按钮操作时，执行的方法
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
    MavenRepository getLocalRepository();

    /**
     * 返回数据库对象
     *
     * @return 数据库对象
     */
    MavenArtifactDatabase getDatabase();
}
