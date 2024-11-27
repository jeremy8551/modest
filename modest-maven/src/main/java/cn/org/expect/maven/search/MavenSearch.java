package cn.org.expect.maven.search;

import java.io.File;

import cn.org.expect.intellij.idea.plugin.maven.concurrent.EDTJob;
import cn.org.expect.maven.concurrent.MavenSearchExecutorService;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.repository.MavenRepositoryDatabase;

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
    void setProgress(String message);

    /**
     * 设置状态栏的信息
     *
     * @param type    文本的类型
     * @param message 文本信息
     */
    void setStatusBar(MavenSearchAdvertiser type, String message);

    /**
     * 多线程执行任务 <br>
     * 如果想要使用官方的 EDT 线程，需要让 task 实现 {@linkplain EDTJob} 接口 <br>
     * 想要刷新 Idea 查询结果的 JList，需要与 Idea 使用同一个数据库连接池：{@link com.intellij.ide.actions.searcheverywhere.SearchEverywhereUI#rebuildListAlarm}，否则多线程刷新 JList 时会出现混乱
     *
     * @param command 任务
     */
    void execute(Runnable command);

    /**
     * 返回线程池
     *
     * @return 线程池
     */
    MavenSearchExecutorService getService();

    /**
     * 返回 Maven仓库接口
     *
     * @return Maven Maven仓库接口
     */
    MavenRepository getRepository();

    /**
     * 返回本地Maven仓库接口
     *
     * @return 本地Maven仓库接口
     */
    MavenRepository getLocalRepository();

    /**
     * 返回数据库接口
     *
     * @return 数据库接口
     */
    MavenRepositoryDatabase getDatabase();

    /**
     * 如果参数对象是 {@linkplain MavenSearchAware}，则向参数对象设置搜索接口
     *
     * @param t   参数对象
     * @param <T> 参数类型
     * @return 参数对象
     */
    default <T> T aware(T t) {
        if (t instanceof MavenSearchAware) {
            ((MavenSearchAware) t).setSearch(this);
        }
        return t;
    }
}
