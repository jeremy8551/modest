package cn.org.expect.maven.repository;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.Terminate;
import cn.org.expect.maven.repository.central.CentralRepository;

/**
 * Maven 仓库接口
 */
public interface MavenRepository extends Terminate {

    /**
     * 默认的 Maven 仓库 ID
     */
    String DEFAULT_SELECTED_REPOSITORY = CentralRepository.class.getAnnotation(EasyBean.class).value();

    /**
     * 返回支持功能
     *
     * @return 支持功能
     */
    MavenArtifactOperation getSupported();

    /**
     * 返回数据库
     *
     * @return 数据库
     */
    MavenRepositoryDatabase getDatabase();

    /**
     * 返回 Maven 仓库的 URL 地址
     *
     * @return URL地址
     */
    String getAddress();

    /**
     * 模糊查询
     *
     * @param pattern 字符串
     * @param start   起始记录位置，从1开始
     * @return 查询结果
     * @throws Exception 模糊查询发生错误
     */
    MavenSearchResult query(String pattern, int start) throws Exception;

    /**
     * 精确查询
     *
     * @param groupId    工件的域名
     * @param artifactId 工件的名
     * @return 查询结果
     * @throws Exception 精确查询发生错误
     */
    MavenSearchResult query(String groupId, String artifactId) throws Exception;

    /**
     * 终止查询
     */
    void terminate();
}
