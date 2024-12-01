package cn.org.expect.maven.repository;

import java.util.List;
import java.util.regex.Matcher;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.Terminate;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.search.ArtifactSearchMessage;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 工件仓库
 */
public interface ArtifactRepository extends Terminate {

    String KEY_PREFIX = "maven\\.search\\.repository\\.([^\\.]+)\\.id";

    /**
     * 返回仓库ID
     *
     * @return 仓库ID
     */
    default String getId() {
        return this.getClass().getAnnotation(EasyBean.class).value();
    }

    /**
     * 返回仓库名
     *
     * @return 仓库名
     */
    default String getName() {
        return getName(this.getId());
    }

    /**
     * 返回仓库名
     *
     * @return 仓库名
     */
    static String getName(String repositoryId) {
        String str = StringUtils.replaceAll(KEY_PREFIX, "\\", "");
        int begin = str.indexOf("(", 0);
        int end = str.indexOf(")", begin);
        String key = str.substring(0, begin) + repositoryId + str.substring(end + 1);
        return ArtifactSearchMessage.get(key);
    }

    /**
     * 返回仓库ID
     *
     * @param name 仓库ID
     * @return 仓库ID
     */
    static String getId(String name) {
        String key = ArtifactSearchMessage.getKey(name);
        Matcher compile = StringUtils.compile(key, KEY_PREFIX);
        return Ensure.notNull(compile, name).group(1);
    }

    /**
     * 返回支持功能
     *
     * @return 支持功能
     */
    ArtifactOperation getSupported();

    /**
     * 返回数据库
     *
     * @return 数据库
     */
    ArtifactRepositoryDatabase getDatabase();

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
    ArtifactSearchResult query(String pattern, int start) throws Exception;

    /**
     * 精确查询
     *
     * @param groupId    工件的域名
     * @param artifactId 工件的名
     * @return 查询结果
     * @throws Exception 精确查询发生错误
     */
    ArtifactSearchResult query(String groupId, String artifactId) throws Exception;

    /**
     * 终止查询
     */
    void terminate();

    /**
     * 返回已注册工件仓库的实现类集合
     *
     * @param ioc 容器上下文信息
     * @return 实现类集合
     */
    static List<EasyBeanInfo> getEasyBeanInfo(EasyContext ioc) {
        return ioc.getBeanInfoList(ArtifactRepository.class).stream().sorted((b1, b2) -> b2.getPriority() - b1.getPriority()).toList();
    }

    /**
     * 返回已注册工件仓库的仓库名数组
     *
     * @param ioc 容器上下文信息
     * @return 仓库名数组
     */
    static String[] getNames(EasyContext ioc) {
        return getEasyBeanInfo(ioc).stream().map(beanInfo -> ArtifactRepository.getName(beanInfo.getName())).toArray(String[]::new);
    }
}
