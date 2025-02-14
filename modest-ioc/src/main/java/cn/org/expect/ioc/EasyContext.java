package cn.org.expect.ioc;

/**
 * 容器上下文信息
 *
 * @author jeremy8551@gmail.com
 */
public interface EasyContext extends EasyContainer, EasyBeanInjector, EasyBeanRepository, EasyBeanFactoryRepository, EasyContainerRepository, EasyPropertyProvider {

    /**
     * 返回上级容器
     *
     * @return 上级容器对象
     */
    EasyContext getParent();

    /**
     * 设置上级容器
     *
     * @param parent 上级容器
     */
    void setParent(EasyContext parent);

    /**
     * 设置容器启动参数
     *
     * @param args 启动参数数组
     */
    void setArgument(String... args);

    /**
     * 返回容器启动的参数
     *
     * @return 启动参数数组
     */
    String[] getArgument();

    /**
     * 设置类加载器
     *
     * @param classLoader 类加载器
     */
    void setClassLoader(ClassLoader classLoader);

    /**
     * 返回容器使用的类加载器
     *
     * @return 类加载器
     */
    ClassLoader getClassLoader();

    /**
     * 扫描Java包中的组件
     *
     * @param args Java包名的数组（也可以在数组中的一个元素中配置多个包名，用逗号分隔）
     */
    int scanPackages(String... args);

    /**
     * 返回类包扫描规则
     *
     * @return 类包数组
     */
    String[] getScanPackages();

    /**
     * 刷新组件信息
     */
    void refresh();
}
