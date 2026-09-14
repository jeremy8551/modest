package cn.org.expect.util;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.message.ResourceScanner;

/**
 * Modest 源码目录配置解析器
 */
public class SourceIndex {

    /** 索引资源名称 */
    public final static String INDEX_RESOURCE = FileUtils.joinPath("META-INF", Settings.getProjectName(), "sourceIndex.txt");

    public static List<SourceInfo> load() {
        return load(ClassUtils.getClassLoader(), INDEX_RESOURCE);
    }

    public static List<SourceInfo> load(ClassLoader classLoader) {
        return load(classLoader, INDEX_RESOURCE);
    }

    public static List<String> getSources() {
        List<SourceInfo> list = load();
        List<String> paths = new ArrayList<String>();
        for (SourceInfo info : list) {
            if (info.getType() == SourceType.SOURCE || info.getType() == SourceType.TEST_SOURCE) {
                paths.add(info.getPath());
            }
        }
        return paths;
    }

    public static List<String> getResources() {
        List<SourceInfo> list = load();
        List<String> paths = new ArrayList<String>();
        for (SourceInfo info : list) {
            if (info.getType() == SourceType.RESOURCE || info.getType() == SourceType.TEST_RESOURCE) {
                paths.add(info.getPath());
            }
        }
        return paths;
    }

    /**
     * 从类路径读取索引并加载其中的类
     *
     * @param classLoader  类加载器
     * @param resourceName 索引资源名称
     * @return 按索引顺序排列的类集合
     */
    public static List<SourceInfo> load(ClassLoader classLoader, String resourceName) {
        List<SourceInfo> list = new ArrayList<SourceInfo>();
        ResourceScanner scanner = new ResourceScanner(classLoader, resourceName);
        while (scanner.hasNext()) {
            try {
                for (String line : StringUtils.splitLines(new String(IO.read(scanner.next()), CharsetName.UTF_8), new ArrayList<String>())) {
                    if (StringUtils.isBlank(line)) {
                        continue;
                    }

                    list.add(parseLine(line));
                }
            } catch (Exception e) {
                Logs.error(resourceName, e);
            }
        }
        return list;
    }

    /**
     * 解析一行配置
     *
     * 格式：
     * artifact,类型,目录
     *
     * @param line 配置行
     * @return 配置信息
     */
    private static SourceInfo parseLine(String line) {
        int first = line.indexOf(',');
        if (first == -1) {
            throw new IllegalArgumentException("配置格式错误，缺少第一个逗号: " + line);
        }

        int second = line.indexOf(',', first + 1);
        if (second == -1) {
            throw new IllegalArgumentException("配置格式错误，缺少第二个逗号: " + line);
        }

        String str = line.substring(0, first);
        String[] array = StringUtils.split(str, ':');
        Ensure.isTrue(array.length == 2, str);
        String groupId = array[0];
        String artifact = array[1];
        String type = StringUtils.trimBlank(line.substring(first + 1, second));
        String path = StringUtils.trimBlank(line.substring(second + 1));
        return new SourceInfo(groupId, artifact, SourceType.of(type), path);
    }

    /**
     * 源码目录信息
     */
    public static class SourceInfo {

        private final String groupId;

        private final String artifact;

        private final SourceType type;

        private final String path;

        public SourceInfo(String groupId, String artifact, SourceType type, String path) {
            this.groupId = groupId;
            this.artifact = artifact;
            this.type = type;
            this.path = path;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifact() {
            return artifact;
        }

        public SourceType getType() {
            return type;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "SourceInfo{groupId='" + groupId + '\'' + ", artifact='" + artifact + '\'' + ", type=" + type + ", path='" + path + '\'' + '}';
        }
    }

    /**
     * 目录类型
     */
    public enum SourceType {

        SOURCE("source"),

        TEST_SOURCE("testSource"),

        RESOURCE("resource"),

        TEST_RESOURCE("testResource");

        private final String value;

        SourceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static SourceType of(String value) {
            for (SourceType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }

            throw new IllegalArgumentException("不支持的源码目录类型: " + value);
        }
    }
}
