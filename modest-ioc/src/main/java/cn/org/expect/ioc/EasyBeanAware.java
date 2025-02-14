package cn.org.expect.ioc;

/**
 * 感知（aware of）资源（标记）接口，可以让容器向对象注入一些特定资源。
 */
public interface EasyBeanAware {

    /**
     * 返回接口信息 <br>
     * 如果一个对象实现了这个接口，容器会执行 {@linkplain #execute(EasyContext, Object)} 方法（实现注入一些特定的资源）
     *
     * @return 接口信息
     */
    Class<?> getInterfaceClass();

    /**
     * 向对象注入一些资源
     *
     * @param ioc    容器上下信息
     * @param object 对象
     */
    void execute(EasyContext ioc, Object object);
}
