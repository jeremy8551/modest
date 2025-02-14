package cn.org.expect.ioc;

import java.util.List;

/**
 * 组件工厂仓库
 */
public interface EasyBeanFactoryRepository {

    /**
     * 返回组件工厂的泛型
     *
     * @return 返回组件工程集合
     */
    List<Class<?>> getBeanFactoryClass();

    /**
     * 查询接口信息对应的工厂
     *
     * @param type 组件类信息
     * @return 组件工厂的类信息
     */
    EasyBeanFactory<?> getBeanFactory(Class<?> type);

    /**
     * 删除接口信息对应的工厂
     *
     * @param type 组件类信息
     * @return 组件工厂实例
     */
    EasyBeanFactory<?> removeBeanFactory(Class<?> type);

    /**
     * 注册组件工厂
     *
     * @param builder 组件工厂
     * @return 返回true表示添加成功
     */
    boolean addBeanFactory(EasyBeanFactory<?> builder);
}
