package cn.org.expect.ioc;

/**
 * 类扫描器的加载规则
 *
 * @author jeremy8551@gmail.com
 */
public interface EasyClassScan {

    /**
     * 处理类扫描器加载的类信息
     *
     * @param repository 组件仓库
     * @param type       类信息（类扫描器加载的类信息）
     * @return 返回true表示已加载, false表示未加载
     */
    boolean load(EasyBeanRepository repository, Class<?> type);
}
