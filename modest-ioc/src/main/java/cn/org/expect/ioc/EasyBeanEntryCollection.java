package cn.org.expect.ioc;

import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;

public interface EasyBeanEntryCollection {

    /**
     * 返回组件的类信息
     *
     * @return 类信息
     */
    Class<?> getBeanClass();

    /**
     * 判断组件信息集合中是否已添加了参数 {@code beanClass}
     *
     * @param entry 组件信息
     * @return 返回true表示已添加
     */
    boolean contains(EasyBeanEntry entry);

    /**
     * 判断是否包含参数 {@code type}
     *
     * @param type 类信息
     * @return 返回true表示包含
     */
    boolean contains(Class<?> type);

    /**
     * 注册组件
     *
     * @param entry 组件信息
     * @return 返回true表示注册成功 false表示注册失败
     */
    boolean add(EasyBeanEntry entry);

    /**
     * 移除指定组件信息
     *
     * @param type 组件的类信息
     * @return 返回true表示移除成功 false表示不存在组件信息
     */
    List<EasyBeanEntry> remove(Class<?> type);

    /**
     * 查询与参数 {@code name} 相等的组件名（{@linkplain EasyBean#value()}）的集合
     *
     * @param name 组件名
     * @return 组件信息集合
     */
    EasyBeanEntryCollection get(String name);

    /**
     * 过滤组件信息
     *
     * @param filter 查询条件
     * @return 组件的实现类信息
     */
    EasyBeanEntryCollection filter(EasyBeanEntryCollection.Filter filter);

    /**
     * 返回第一个组件 <br>
     * 返回组件信息，如果多个组件名重复了，则取同名下序号最大的
     *
     * @return 组件信息
     * @throws BeanRepeatDefineException 有重复组件会报错
     */
    EasyBeanEntry head();

    /**
     * 排序
     */
    void sort();

    /**
     * 返回组件的集合
     *
     * @return 组件集合
     */
    List<EasyBeanEntry> values();

    interface Filter {

        /**
         * 过滤条件
         *
         * @param entry 组件信息
         * @return 返回true表示满足查询条件，false表示不满足查询条件
         */
        boolean accept(EasyBeanEntry entry);
    }
}
