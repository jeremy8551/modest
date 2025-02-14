package cn.org.expect.ioc;

import cn.org.expect.ioc.annotation.EasyBean;

/**
 * 第三方容器接口
 */
public interface EasyContainer {

    /**
     * 容器名，必须唯一
     *
     * @return 容器名
     */
    String getName();

    /**
     * 返回（搜索/创建）组件的实例对象
     *
     * @param <E>  类或接口
     * @param type 类或接口
     * @param args 搜索条件 <br>
     *             第一个参数，对应组件上的注解 {@link EasyBean#value()} 属性 <br>
     *             第二个参数开始作为<b>组件构造方法</b>的参数
     * @return 组件
     * @throws BeanRepeatDefineException 组件重复
     */
    <E> E getBean(Class<E> type, Object... args);

    /**
     * 返回（搜索/创建）组件的实例对象（如果发生异常，则返回null）
     *
     * @param <E>  类或接口
     * @param type 类或接口
     * @param args 搜索条件 <br>
     *             第一个参数，对应组件上的注解 {@link EasyBean#value()} 属性 <br>
     *             第二个参数开始作为<b>组件构造方法</b>的参数
     * @return 组件，发生异常（不会打印异常信息）返回null
     */
    <E> E getBeanQuietly(Class<E> type, Object... args);

    /**
     * 返回（搜索/创建）组件的实例对象
     *
     * @param <E>         类或接口
     * @param type        类或接口
     * @param name        组件名
     * @param defaultName 默认组件名（如果返回null，则再次尝试使用默认组件名）
     * @return 组件
     */
    <E> E getBeanQuietly(Class<E> type, String name, String defaultName);
}
