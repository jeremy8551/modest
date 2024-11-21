package cn.org.expect.maven.search;

import java.io.File;

import cn.org.expect.maven.concurrent.EDTJob;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;
import cn.org.expect.maven.repository.MavenSearchResult;

public interface MavenSearch {

    /**
     * 返回配置信息
     *
     * @return 配置信息
     */
    MavenSearchSettings getSettings();

    /**
     * 返回上下文信息
     *
     * @return 上下文信息
     */
    MavenSearchContext getContext();

    /**
     * 根据输入设备中的文本，异步进行查询
     */
    void asyncSearch();

    /**
     * 刷新查询结果（异步执行删除缓存，再重新查询）
     */
    void asyncRefresh();

    /**
     * 异步执行模糊搜索
     *
     * @param pattern 字符串
     */
    void asyncSearch(String pattern);

    /**
     * 异步执行精确搜索
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
     * 在等待搜索结果时，显示进度的文本信息
     *
     * @param message 文本信息
     */
    void setProgressText(String message);

    /**
     * 设置状态栏的信息
     *
     * @param type    文本类型
     * @param message 文本信息
     */
    void setStatusbarText(MavenSearchAdvertiser type, String message);

    /**
     * 使用最新的查询结果，渲染 UI 界面
     */
    void showSearchResult();

    /**
     * 使用参数指定的查询结果，渲染 UI 界面
     *
     * @param result 查询结果
     */
    void showSearchResult(MavenSearchResult result);

    /**
     * 多线程执行任务 <br>
     * 如果想要使用官方的 EDT 线程，需要让 task 实现 {@linkplain EDTJob} 接口 <br>
     * 想要刷新 Idea 查询结果的 JList，需要与 Idea 使用同一个数据库连接池：{@link com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI#rebuildListAlarm}，否则多线程刷新 JList 时会出现混乱，感谢我主 耶稣基督 带领我发现解决这个问题
     *
     * @param command 任务
     */
    void execute(Runnable command);

    /**
     * 判断当前是否正在查询某个 Maven 工件
     *
     * @return 返回true表示正在查询
     */
    MavenSearchExecutorService getService();

    /**
     * 返回 Maven 仓库信息
     *
     * @return Maven 仓库信息
     */
    MavenRepository getRepository();

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
    MavenRepositoryDatabase getDatabase();
}
