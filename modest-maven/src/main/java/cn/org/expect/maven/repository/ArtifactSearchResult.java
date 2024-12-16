package cn.org.expect.maven.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.maven.Artifact;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * 搜索结果
 */
public interface ArtifactSearchResult {

    /**
     * 搜索结果所属的仓库
     *
     * @return 仓库的Class信息
     */
    String getRepositoryName();

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
     * 判断搜索结果是否是指定仓库的搜索结果
     *
     * @param type 仓库Class信息
     * @return 返回true表示是 false表示不是
     */
    default boolean isRepository(Class<?> type) {
        return type != null && type.getName().equals(this.getRepositoryName());
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
     * 添加工件
     *
     * @param artifact 工件
     */
    default void addArtifact(Artifact artifact) {
        List<Artifact> list = this.getList();
        for (int i = 0; i < list.size(); i++) {
            Artifact item = list.get(i);
            if (item.getGroupId().equals(artifact.getGroupId()) //
                    && item.getArtifactId().equals(artifact.getArtifactId()) //
                    && item.getVersion().equals(artifact.getVersion()) //
            ) {
                if (artifact.getType().equalsIgnoreCase("jar")) {
                    list.set(i, artifact);
                    return;
                }

                if (item.getType().equalsIgnoreCase("jar")) {
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
        List<String> delimiters = ArrayUtils.asList(".", "-"); // 版本号文本的分隔符
        list.sort((a1, a2) -> {
            String v1 = a1.getVersion();
            String v2 = a2.getVersion();
            String[] array1 = StringUtils.split(v1, delimiters, false);
            String[] array2 = StringUtils.split(v2, delimiters, false);
            int size = Math.min(array1.length, array2.length);
            for (int i = 0; i < size; i++) {
                String element1 = array1[i];
                String element2 = array2[i];

                if (StringUtils.isNumber(element1) && StringUtils.isNumber(element2)) {
                    int v = Integer.parseInt(element1) - Integer.parseInt(element2);
                    if (v != 0) {
                        return -v;
                    }
                } else {
                    int v = element1.compareTo(element2);
                    if (v != 0) {
                        return -v;
                    }
                }
            }
            return array2.length - array1.length;
        });
    }

    /**
     * 在保持工件的原始顺序的前提下，对集合中不同类型的工件进行归类
     */
    default ArtifactSearchResult sortByGroup() {
        List<Artifact> list = this.getList();
        Map<String, List<Artifact>> map = new LinkedHashMap<>(); // 使用 LinkedHashMap 保证插入顺序
        for (Artifact artifact : list) { // 遍历列表，根据类型分组
            String key = artifact.getGroupId() + ":" + artifact.getArtifactId();
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(artifact);
        }

        list.clear();
        Set<Map.Entry<String, List<Artifact>>> entries = map.entrySet();
        for (Map.Entry<String, List<Artifact>> entry : entries) {
            List<Artifact> value = entry.getValue();
            value.sort((a1, a2) -> a2.getVersion().compareTo(a1.getVersion()));
            list.addAll(value);
        }
        return this;
    }

    /**
     * 模糊查询结果的排序规则：按版本数、时间戳倒序
     *
     * @return 搜索结果
     */
    default ArtifactSearchResult sortByPattern() {
        this.getList().sort((o1, o2) -> {
            int vv = o2.getVersionCount() - o1.getVersionCount(); // 版本数
            if (vv != 0) {
                return vv;
            }

            int tv = TIMESTAMP_COMPARATOR.compare(o2.getTimestamp(), o1.getTimestamp()); // 最新发布
            if (tv != 0) {
                return tv;
            }

            int gv = o2.getGroupId().compareTo(o1.getGroupId());
            if (gv != 0) {
                return gv;
            }

            return o2.getArtifactId().compareTo(o1.getArtifactId());
        });
        return this;
    }

    /**
     * 精确查询结果的排序规则：按最新发布时间倒序排序
     *
     * @return 搜索结果
     */
    default ArtifactSearchResult sortByTime() {
        this.getList().sort((m1, m2) -> TIMESTAMP_COMPARATOR.compare(m2.getTimestamp(), m1.getTimestamp()));
        return this;
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
}
