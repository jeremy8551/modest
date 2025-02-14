package cn.org.expect.ioc;

/**
 * 反射工具
 */
public interface EasyBeanInjector {

    /**
     * 判断字符串参数 className 对应的Java类是否存在 <br>
     * 不存在时会返回 null
     *
     * @param <E>       类信息
     * @param className 类名
     * @return 类信息
     */
    <E> Class<E> forName(String className);

    /**
     * 创建 {@code type} 的实例对象
     * <p>
     * 容器会向组件实例对象中自动注入属性（容器上下文信息等）
     *
     * @param type 类或接口
     * @param args 执行构造方法时，传入的参数
     * @param <E>  类型
     * @return 返回组件实例对象
     * @throws IocException 发生错误
     */
    <E> E newInstance(Class<?> type, Object... args);

    /**
     * 向实例对象注入特定资源与属性 <br>
     * 向实例对象注入特定资源：执行 {@linkplain EasyContextAware#setContext(EasyContext)} 获取容器上下文信息 <br>
     *
     * @param object 对象实例
     * @throws AutowireException 发生错误
     */
    void autowire(Object object);
}
