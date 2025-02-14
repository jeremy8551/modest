package cn.org.expect.ioc;

/**
 * 第三方容器的仓库
 */
public interface EasyContainerRepository {

    /**
     * 添加容器实例
     *
     * @param ioc 容器实例
     * @return 如果容器重名，则会替换掉重名容器，并返回替换掉的容器
     */
    EasyContainer addContainer(EasyContainer ioc);

    /**
     * 删除容器实例对象
     *
     * @param name 容器名
     * @return 被删除的容器
     */
    EasyContainer removeContainer(String name);
}
