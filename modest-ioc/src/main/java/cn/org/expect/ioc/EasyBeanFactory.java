package cn.org.expect.ioc;

/**
 * 组件工厂接口
 *
 * @param <E> 组件的类信息
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-08
 */
public interface EasyBeanFactory<E> {

    /**
     * 生成一个组件的实例对象
     *
     * @param context 容器的上下文信息
     * @param args    参数数组
     * @return 实例对象
     * @throws Exception 生成实例对象发生错误
     */
    E build(EasyContext context, Object... args) throws Exception;
}
