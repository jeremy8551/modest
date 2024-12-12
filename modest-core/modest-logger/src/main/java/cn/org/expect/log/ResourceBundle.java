package cn.org.expect.log;

import java.util.Set;

/**
 * 国际化资源接口
 */
public interface ResourceBundle {

    /**
     * 判断国际化资源是否存在
     *
     * @param key 资源编号
     * @return 返回true表示存在 false表示不存在
     */
    boolean contains(String key);

    /**
     * 返回国际化资源文本
     *
     * @param key 资源编号
     * @return 国际化资源文本，其中的参数必须是 {0} 格式
     */
    String get(String key);

    /**
     * 返回所有资源编号集合
     *
     * @return 资源编号集合
     */
    Set<String> getKeys();
}
