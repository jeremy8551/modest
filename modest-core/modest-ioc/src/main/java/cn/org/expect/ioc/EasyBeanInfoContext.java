package cn.org.expect.ioc;

import java.util.List;

/**
 * 组件信息的上下文信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/28
 */
public interface EasyBeanInfoContext {

    /**
     * 扫描包下的组件
     *
     * @param args 参数数组
     */
    int scanPackages(String... args);

    /**
     * 返回类包扫描规则
     *
     * @return 类包数组
     */
    String[] getScanRule();

    /**
     * 返回容器中存储的所有组件的查询条件（按组件添加的顺序）
     *
     * @return 组件类信息的集合（按组件添加的顺序）
     */
    List<Class<?>> getBeanInfoTypes();

    /**
     * 查询接口信息对应的实现类
     *
     * @param type 组件类信息
     * @param name 组件名
     * @return 组件类信息
     */
    EasyBeanDefine getBeanInfo(Class<?> type, String name);

    /**
     * 查找类或接口对应的（所有）组件信息
     *
     * @param type 类或接口
     * @return 组件对应的所有实现类
     */
    List<EasyBeanInfo> getBeanInfoList(Class<?> type);

    /**
     * 查找类信息对应的实现类集合
     *
     * @param type 组件类信息
     * @param name 组件名
     * @return 组件对应的所有实现类
     */
    List<EasyBeanInfo> getBeanInfoList(Class<?> type, String name);

    /**
     * 判断组件 {@code type} 的实现类 {@code impl} 是否已注册
     *
     * @param type 组件信息
     * @param cls  实现类
     * @return true表示存在组件实现类
     */
    boolean containsBeanInfo(Class<?> type, Class<?> cls);

    /**
     * 删除组件的某个实现类
     *
     * @param type 组件的类信息
     * @param cls  需要删除的实现类
     * @return 返回true表示删除成功 false表示实现类不存在
     */
    List<EasyBeanDefine> removeBeanInfo(Class<?> type, Class<?> cls);

    /**
     * 删除接口信息对应的实现类集合
     *
     * @param type 组件类信息
     * @return 组件的实现类集合
     */
    List<EasyBeanInfo> removeBeanInfoList(Class<?> type);

    /**
     * 删除所有已注册的组件信息
     */
    void removeBeanInfo();

}
