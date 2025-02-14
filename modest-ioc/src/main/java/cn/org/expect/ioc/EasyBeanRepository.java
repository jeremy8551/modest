package cn.org.expect.ioc;

import java.util.List;

/**
 * 组件的注册接口
 */
public interface EasyBeanRepository {

    /**
     * 加载类或接口实现类的实例对象
     *
     * @param <E>     类或接口
     * @param service 类或接口
     * @return 组件集合
     */
    <E> List<E> loadBean(Class<E> service);

    /**
     * 查询类或接口对应的组件信息
     *
     * @param type 类或接口
     * @return 组件信息
     * @throws BeanRepeatDefineException 组件重复
     */
    EasyBeanEntry getBeanEntry(Class<?> type);

    /**
     * 查询类或接口对应的组件信息
     *
     * @param type 类或接口
     * @param name 组件名
     * @return 组件信息
     * @throws BeanRepeatDefineException 组件重复
     */
    EasyBeanEntry getBeanEntry(Class<?> type, String name);

    /**
     * 查询类或接口对应所有组件的元数据
     *
     * @param type 类或接口
     * @return 查询结果
     */
    EasyBeanEntryCollection getBeanEntryCollection(Class<?> type);

    /**
     * 查询类或接口对应所有组件的元数据
     *
     * @param type 类或接口
     * @param name 组件名
     * @return 查询结果
     */
    EasyBeanEntryCollection getBeanEntryCollection(Class<?> type, String name);

    /**
     * 查询容器中已注册类和接口
     *
     * @return 类信息集合（会保留注册顺序）
     */
    List<Class<?>> getBeanClassList();

    /**
     * 判断容器中是否存在 {@code type} 的实现类 {@code impl}
     *
     * @param beanClass 组件信息
     * @param type      实现类
     * @return true表示存在组件实现类
     */
    boolean containsBean(Class<?> beanClass, Class<?> type);

    /**
     * 注册组件
     *
     * @param beanClass 组件的类信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(Class<?> beanClass);

    /**
     * 注册组件
     *
     * @param beanClass   组件的类信息
     * @param name        组件名
     * @param singleton   是否单例模式
     * @param lazy        是否延迟加载
     * @param priority    权重，如果注册了多个同名的组件导致冲突，容器使用权重值最大的组件
     * @param description 说明信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(Class<?> beanClass, String name, boolean singleton, boolean lazy, int priority, String description);

    /**
     * 注册组件
     *
     * @param entry 组件信息
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(EasyBeanEntry entry);

    /**
     * 注册单例模式组件
     *
     * @param bean 组件
     * @return 返回true表示注册成功 false表示注册失败（未添加组件）
     */
    boolean addBean(Object bean);

    /**
     * 删除组件的某个实现类
     *
     * @param type 组件的类信息
     * @param cls  需要删除的实现类
     * @return 返回true表示删除成功 false表示实现类不存在
     */
    List<EasyBeanEntry> removeBean(Class<?> type, Class<?> cls);

    /**
     * 删除接口信息对应的实现类集合
     *
     * @param type 组件类信息
     * @return 组件的实现类集合
     */
    List<EasyBeanEntry> removeBean(Class<?> type);

    /**
     * 删除所有组件
     */
    void removeBean();
}
