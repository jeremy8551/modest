package cn.org.expect.message;

import java.util.Set;

/**
 * 资源信息接口
 */
public interface ResourceMessageBundle {

    /**
     * 设置类加载器
     *
     * @param classLoader 类加载器
     */
    void load(ClassLoader classLoader);

    /**
     * 判断资源信息是否存在
     *
     * @param key 资源编号
     * @return 返回true表示存在 false表示不存在
     */
    boolean contains(String key);

    /**
     * 返回资源信息
     *
     * @param key 资源编号
     * @return 资源信息
     */
    String get(String key);

    /**
     * 返回所有资源编号集合
     *
     * @return 资源编号集合
     */
    Set<String> getKeys();
}
