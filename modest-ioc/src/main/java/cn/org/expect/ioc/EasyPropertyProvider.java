package cn.org.expect.ioc;

import java.util.List;
import java.util.Properties;

/**
 * 属性提供者，容器注入属性时，根据属性名查询对应的属性值作为注入值
 */
public interface EasyPropertyProvider {

    /**
     * 判断属性是否存在
     *
     * @param name 属性名
     * @return 返回true表示属性存在，false表示属性不存在
     */
    boolean hasProperty(String name);

    /**
     * 添加属性集合
     *
     * @param properties 属性集合
     * @return 返回true表示成功，false表示失败（集合已存在）
     */
    boolean addProperties(Properties properties);

    /**
     * 返回属性值
     *
     * @param name 属性名
     * @return 返回null表示属性不存在
     */
    String getProperty(String name);

    /**
     * 返回属性集合
     *
     * @return 不可变集合
     */
    List<Properties> getProperties();
}
