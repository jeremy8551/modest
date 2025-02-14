package cn.org.expect.ioc;

/**
 * 组件识别/检测接口，容器使用这个接口判断一个类是不是组件
 */
public interface EasyBeanAnnotation {

    /**
     * 判断类是否是一个组件
     *
     * @param type 类信息
     * @return 返回true表示是，false表示不是
     */
    boolean isPresent(Class<?> type);

    /**
     * 返回类信息对应的组件信息
     *
     * @param type 类信息
     * @return 组件信息
     */
    EasyBeanEntry getBean(Class<?> type);
}
