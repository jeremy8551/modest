package cn.org.expect.maven.repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.org.expect.maven.repository.impl.ArtifactSearchResultType;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * 搜索结果
 */
public interface ArtifactSearchResult {

    /**
     * 搜索结果类型
     *
     * @return 类型
     */
    ArtifactSearchResultType getType();

    /**
     * Maven 工件列表
     *
     * @return 集合
     */
    List<Artifact> getList();

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
     * 是否有未读数据
     *
     * @return true表示还有未读数据，false表示已全部读取
     */
    boolean isHasMore();

    /**
     * 重置操作
     */
    default void reset() {
        List<Artifact> list = this.getList();
        for (Artifact artifact : list) {
            artifact.setFold(true);
        }
    }

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
        for (Artifact artifact : this.getList()) {
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
    default void addArtifact(Artifact artifact) {
        List<Artifact> list = this.getList();
        for (int i = 0; i < list.size(); i++) {
            Artifact mavenArtifact = list.get(i);
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

    /**
     * 模糊查询结果的排序规则：按时间戳倒序
     */
    default void sortByPattern() {
        this.getList().sort(PATTERN_RESULT_COMPARATOR.reversed());
    }

    /**
     * 精确查询结果的排序规则：按版本数、最新发布时间等排序
     */
    default void sortByTimeDesc() {
        this.getList().sort((m1, m2) -> TIMESTAMP_COMPARATOR.compare(m2.getTimestamp(), m1.getTimestamp()));
    }

    Comparator<Date> TIMESTAMP_COMPARATOR = (o1, o2) -> {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    };

    Comparator<Artifact> PATTERN_RESULT_COMPARATOR = (o1, o2) -> {
        int vv = o1.getVersionCount() - o2.getVersionCount(); // 版本数
        if (vv != 0) {
            return vv;
        }

        int tv = TIMESTAMP_COMPARATOR.compare(o1.getTimestamp(), o2.getTimestamp()); // 最新发布
        if (tv != 0) {
            return tv;
        }

        int gv = o1.getGroupId().compareTo(o2.getGroupId());
        if (gv != 0) {
            return gv;
        }

        return o1.getArtifactId().compareTo(o2.getArtifactId());
    };

    /** 版本号文本的分隔符 */
    List<String> DELIMITERS = ArrayUtils.asList(".", "-");

    /**
     * 同一个工件有多个版本时的排序规则，按版本号排序
     */
    Comparator<Artifact> COMPARATOR = (a1, a2) -> {
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
