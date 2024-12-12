package cn.org.expect.maven.search;

import java.io.File;

import cn.org.expect.maven.Artifact;
import cn.org.expect.maven.ArtifactOption;
import cn.org.expect.maven.concurrent.ArtifactSearchExecutorService;
import cn.org.expect.maven.repository.ArtifactRepository;
import cn.org.expect.maven.repository.ArtifactRepositoryDatabase;
import cn.org.expect.maven.repository.local.LocalRepository;
import cn.org.expect.maven.repository.local.LocalRepositorySettings;

/**
 * 搜索结果
 */
public interface ArtifactSearch {

    /**
     * 返回配置信息
     *
     * @return 配置信息
     */
    ArtifactSearchSettings getSettings();

    /**
     * 返回上下文信息
     *
     * @return 上下文信息
     */
    ArtifactSearchContext getContext();

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
    void sendNotification(ArtifactSearchNotification type, String text, Object... array);

    /**
     * 推送通知
     *
     * @param type       通知类型
     * @param text       通知内容
     * @param actionName 操作名称
     * @param file       打开的文件
     * @param textParams 通知内容的参数
     */
    void sendNotification(ArtifactSearchNotification type, String text, String actionName, File file, Object... textParams);

    /**
     * 在等待搜索结果时，显示进度的文本信息
     *
     * @param message       文本信息
     * @param messageParams 文本的参数
     */
    void setProgress(String message, Object... messageParams);

    /**
     * 设置状态栏的信息
     *
     * @param type          文本的类型
     * @param message       文本信息
     * @param messageParams 文本参数
     */
    void setStatusBar(ArtifactSearchStatusMessageType type, String message, Object... messageParams);

    /**
     * 文本处理器
     *
     * @return 文本处理器
     */
    ArtifactSearchPattern getPattern();

    /**
     * 提交到线程池并发执行任务
     *
     * @param command 任务
     */
    void execute(Runnable command);

    /**
     * 返回线程池
     *
     * @return 线程池
     */
    ArtifactSearchExecutorService getService();

    /**
     * 返回仓库信息
     *
     * @return 仓库信息
     */
    ArtifactOption getRepositoryInfo();

    /**
     * 设置仓库ID
     *
     * @param repositoryId 仓库ID
     */
    void setRepository(String repositoryId);

    /**
     * 返回仓库接口
     *
     * @return Maven Maven仓库接口
     */
    ArtifactRepository getRepository();

    /**
     * 返回本地仓库接口
     *
     * @return 本地Maven仓库接口
     */
    LocalRepository getLocalRepository();

    /**
     * 返回本地仓库的配置信息
     *
     * @return 配置信息
     */
    LocalRepositorySettings getLocalRepositorySettings();

    /**
     * 返回数据库接口
     *
     * @return 数据库接口
     */
    ArtifactRepositoryDatabase getDatabase();

    /**
     * 下载工件
     *
     * @param artifact 工件信息
     */
    void download(Artifact artifact);

    /**
     * 下载工件
     *
     * @param artifact 工件信息
     */
    void asyncDownload(Artifact artifact);

    /**
     * 等待工件下载完毕
     *
     * @param artifact 工件信息
     * @param timeout
     */
    void waitDownload(Artifact artifact, long timeout);

    /**
     * 显示搜索结果
     */
    void display();

    /**
     * 异步显示搜索结果
     */
    default void asyncDisplay() {
        this.execute(this::display);
    }

    /**
     * 如果参数对象是 {@linkplain ArtifactSearchAware}，则向参数对象设置搜索接口
     *
     * @param t   参数对象
     * @param <T> 参数类型
     * @return 参数对象
     */
    default <T> T aware(T t) {
        if (t instanceof ArtifactSearchAware) {
            ((ArtifactSearchAware) t).setSearch(this);
        }
        return t;
    }
}
