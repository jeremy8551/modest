package cn.org.expect.maven.repository;

import java.util.Comparator;
import java.util.List;

import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * Maven 仓库搜索结果
 */
public interface MavenSearchResult {

    /**
     * Maven 工件列表
     *
     * @return 集合
     */
    List<MavenArtifact> getList();

    /**
     * 下次查询的起始位置
     *
     * @return 位置信息，从1开始
     */
    int getStart();

    /**
     * 返回总记录数
     *
     * @return 总记录数
     */
    int getFoundNumber();

    /**
     * Maven 工件个数
     *
     * @return 工件个数
     */
    int size();

    /**
     * 返回查询时间
     *
     * @return 查询时间
     */
    long getQueryTime();

    /**
     * 判断查询结果是否过期
     *
     * @param timeMillis 过期时间，单位毫秒
     * @return 返回true表示过期，false表示未过期
     */
    default boolean isExpire(long timeMillis) {
        return System.currentTimeMillis() - this.getQueryTime() >= timeMillis;
    }

    /**
     * 判断工件是否存在
     *
     * @param groupId    工件域名
     * @param artifactId 工件ID
     * @param version    版本号
     * @return 返回true表示存在
     */
    default boolean contains(String groupId, String artifactId, String version) {
        for (MavenArtifact artifact : this.getList()) {
            if (artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId) && artifact.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加一个工件
     *
     * @param artifact 工件
     */
    default void addArtifact(MavenArtifact artifact) {
        List<MavenArtifact> list = this.getList();
        for (int i = 0; i < list.size(); i++) {
            MavenArtifact mavenArtifact = list.get(i);
            if (mavenArtifact.getGroupId().equals(artifact.getGroupId()) //
                    && mavenArtifact.getArtifactId().equals(artifact.getArtifactId()) //
                    && mavenArtifact.getVersion().equals(artifact.getVersion()) //
            ) {
                if (artifact.getType().equalsIgnoreCase("jar")) {
                    list.set(i, artifact);
                    return;
                }

                if (mavenArtifact.getType().equalsIgnoreCase("jar")) {
                    return;
                }

                if (!artifact.getType().equalsIgnoreCase("pom")) {
                    list.set(i, artifact);
                    return;
                }

                return;
            }
        }

        list.add(artifact);
        list.sort(COMPARATOR.reversed());
    }

    /** 版本号文本的分隔符 */
    List<String> DELIMITERS = ArrayUtils.asList(".", "-");

    /**
     * 同一个工件有多个版本时的排序规则，按版本号排序
     */
    Comparator<MavenArtifact> COMPARATOR = (a1, a2) -> {
        String v1 = a1.getVersion();
        String v2 = a2.getVersion();
        String[] array1 = StringUtils.split(v1, DELIMITERS, false);
        String[] array2 = StringUtils.split(v2, DELIMITERS, false);
        int size = Math.min(array1.length, array2.length);
        for (int i = 0; i < size; i++) {
            String element1 = array1[i];
            String element2 = array2[i];

            if (StringUtils.isNumber(element1) && StringUtils.isNumber(element2)) {
                int v = Integer.parseInt(element1) - Integer.parseInt(element2);
                if (v != 0) {
                    return v;
                }
            } else {
                int v = element1.compareTo(element2);
                if (v != 0) {
                    return v;
                }
            }
        }
        return array1.length - array2.length;
    };
}
